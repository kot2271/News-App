package com.trendsoft.newsApp.controllers.rest;

import com.trendsoft.newsApp.dto.requestDto.EmailDto;
import com.trendsoft.newsApp.dto.requestDto.LoginDto;
import com.trendsoft.newsApp.dto.requestDto.PasswordDto;
import com.trendsoft.newsApp.dto.requestDto.RegistrationDto;
import com.trendsoft.newsApp.dto.responseDto.*;
import com.trendsoft.newsApp.models.CaptchaCode;
import com.trendsoft.newsApp.models.User;
import com.trendsoft.newsApp.services.*;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth/")
@RequiredArgsConstructor
public class ApiAuthController {

  private final AuthService authService;
  private final UserService userService;
  private final PostService postService;
  private final CaptchaService captchaService;

  @Autowired
  private MailSender mailSender;

  @PostMapping("login")
  public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {

    User userFromDB =
        userService.getUserByEmailAndPassword(loginDto.getEmail(), loginDto.getPassword());
    return getUserResponseEntity(userFromDB);
  }

  @GetMapping("check")
  public ResponseEntity<?> check() {
    Integer userId = authService.getUserIdOnSessionId();
    User userFromDB = userService.getUserById(userId);
    return getUserResponseEntity(userFromDB);
  }

  @GetMapping("logout")
  public ResponseEntity<?> logout() {
    authService.deleteSession();
    return ResponseEntity.ok(new ResultTrueDto());
  }

  @GetMapping("captcha")
  public ResponseEntity<?> captcha() {
    CaptchaCode captchaCode = captchaService.generateCaptcha();
    String image = captchaService.getImageBase64(captchaCode.getCode(), 20);
    return ResponseEntity.ok(new CaptchaDto(captchaCode.getSecretCode(), image));
  }

  @PostMapping("register")
  public ResponseEntity<?> registration(@RequestBody RegistrationDto registrationDto) {
    ResultFalseWithErrorsDto resultFalse = preRegistrationVerification(registrationDto);
    if (resultFalse.getErrors().size() > 0) {
      return ResponseEntity.ok(resultFalse);
    }
    userService.registration(registrationDto);
    return ResponseEntity.ok(new ResultTrueDto());
  }

  @PostMapping("restore")
  public ResponseEntity<?> restore(@RequestBody EmailDto emailDto) {
    if (!userService.userExistByEmail(emailDto.getEmail())) {
      return ResponseEntity.ok(new ResultFalseDto());
    }
    String activationCode = userService.getUsersRestorePasswordCode(emailDto.getEmail());
    String url = "http://localhost:8080/login/change-password/" + activationCode;
    String message =
        "Hello! To recover your password, visit next link:"
            + "<a href="
            + url
            + ">Восстановить пароль</a>";
    mailSender.send(emailDto.getEmail(), "Recover password", message);

    return ResponseEntity.ok(new ResultTrueDto());
  }

  @PostMapping("password")
  public ResponseEntity<?> password(@RequestBody PasswordDto passwordDto) {
    User user = userService.getUserByRestoreCode(passwordDto.getCode());
    ResultFalseWithErrorsDto resultFalse = preRestorePasswordVerification(passwordDto, user);
    if (resultFalse.getErrors().size() > 0) {
      return ResponseEntity.ok(resultFalse);
    }
    userService.restoreUserPassword(user, passwordDto.getPassword());
    return ResponseEntity.ok(new ResultTrueDto());
  }

  /** приватные методы для различных превращений */
  private ResponseEntity<?> getUserResponseEntity(User userFromDB) {
    if (userFromDB != null) {
      Integer countNewPosts = null;
      if (userFromDB.getIsModerator() == 1) {
        countNewPosts = postService.getCountPostsToModeration();
        return getAuthUserResponseEntity(userFromDB, true, true, countNewPosts);
      }
      return getAuthUserResponseEntity(userFromDB, false, false, countNewPosts);
    }
    return ResponseEntity.ok(new ResultFalseDto());
  }

  private ResponseEntity<ResultTrueDtoWithUser> getAuthUserResponseEntity(
      User userFromDB, boolean isModerator, boolean settings, Integer countNewPosts) {

    UserToLoginDto userFullInformation =
        new UserToLoginDto(
            userFromDB.getId(),
            userFromDB.getName(),
            userFromDB.getPhoto(),
            userFromDB.getEmail(),
            isModerator,
            countNewPosts,
            settings);
    authService.saveSession(userFromDB.getId());

    return ResponseEntity.ok(new ResultTrueDtoWithUser(userFullInformation));
  }

  /** проверка введенных значений при регистрации */
  private ResultFalseWithErrorsDto preRegistrationVerification(RegistrationDto registrationDto) {
    ResultFalseWithErrorsDto resultFalse = new ResultFalseWithErrorsDto();
    String captcha = captchaService.codeFromSecretCode(registrationDto.getCaptchaSecret());
    if (!captcha.equals(registrationDto.getCaptcha())) {
      resultFalse.addNewError("captcha", "Код с картинки введен неверно");
    }
    Pattern pattern = Pattern.compile("^[A-Za-z0-9_А-Яа-я]{2,16}$");
    Matcher matcher = pattern.matcher(registrationDto.getName());
    if (!matcher.matches()) {
      resultFalse.addNewError(
          "name", "Имя указано неверно, доспустимая длина - от 2х до 16 символов");
    }
    if (registrationDto.getPassword().length() < 6) {
      resultFalse.addNewError("password", "Пароль короче 6-ти символов");
    }
    if (userService.userExistByEmail(registrationDto.getEmail())) {
      resultFalse.addNewError("email", "Этот e-mail уже зарегистрирован");
    }
    return resultFalse;
  }

  /** проверка введенных значений при восстановлении пароля */
  private ResultFalseWithErrorsDto preRestorePasswordVerification(
      PasswordDto passwordDto, User user) {
    ResultFalseWithErrorsDto resultFalse = new ResultFalseWithErrorsDto();
    String captcha = captchaService.codeFromSecretCode(passwordDto.getCaptchaSecret());
    if (user == null) {
      resultFalse.addNewError(
          "code",
          "Ссылка для восстановления пароля устарела.\n"
              + "<a href=/login/restore-password>Запросить ссылку снова</a>");
    }
    if (!captcha.equals(passwordDto.getCaptcha())) {
      resultFalse.addNewError("captcha", "Код с картинки введен неверно");
    }
    if (passwordDto.getPassword().length() < 6) {
      resultFalse.addNewError("password", "Пароль короче 6-ти символов");
    }
    return resultFalse;
  }
}
