package com.trendsoft.newsApp.controllers.rest;

import com.trendsoft.newsApp.dto.requestDto.ProfileDto;
import com.trendsoft.newsApp.dto.responseDto.ResultFalseWithErrorsDto;
import com.trendsoft.newsApp.dto.responseDto.ResultTrueDto;
import com.trendsoft.newsApp.models.User;
import com.trendsoft.newsApp.services.AuthService;
import com.trendsoft.newsApp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/profile/my")
@RequiredArgsConstructor
public class ApiProfileController {
  private final AuthService authService;
  private final UserService userService;

  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<?> editProfileWithPhoto(
      @RequestParam(value = "photo", required = false) MultipartFile file,
      @ModelAttribute ProfileDto dto) {
    Integer userId = authService.getUserIdOnSessionId();
    authService.checkAuth(userId);
    User user = userService.getUserById(userId);
    ResultFalseWithErrorsDto errorsDto = editWithPhotoVerification(file, dto, user);
    if (errorsDto.getErrors().size() > 0) {
      return ResponseEntity.badRequest().body(errorsDto);
    }
    userService.editUserWithPhoto(file, dto, user);

    return ResponseEntity.ok(new ResultTrueDto());
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<?> editProfile(@RequestBody ProfileDto dto) {
    Integer userId = authService.getUserIdOnSessionId();
    authService.checkAuth(userId);
    User user = userService.getUserById(userId);
    ResultFalseWithErrorsDto errorsDto = verificationWithoutPhoto(dto, user);
    if (errorsDto.getErrors().size() > 0) {
      return ResponseEntity.badRequest().body(errorsDto);
    }
    userService.editUserWithoutPhoto(dto, user);

    return ResponseEntity.ok(new ResultTrueDto());
  }

  private ResultFalseWithErrorsDto editWithPhotoVerification(
      MultipartFile file, ProfileDto dto, User user) {
    ResultFalseWithErrorsDto errorsDto = verificationWithoutPhoto(dto, user);
    if (file.getSize() > 5242880) {
      errorsDto.addNewError("photo", "Фото слишком большое, нужно не более 5 Мб");
    }
    return errorsDto;
  }

  private ResultFalseWithErrorsDto verificationWithoutPhoto(ProfileDto dto, User user) {
    ResultFalseWithErrorsDto errorsDto = new ResultFalseWithErrorsDto();
    if (!user.getEmail().equals(dto.getEmail()) && userService.userExistByEmail(dto.getEmail())) {
      errorsDto.addNewError("email", "Этот e-mail уже зарегистрирован");
    }
    Pattern pattern = Pattern.compile("^[A-Za-z0-9_А-Яа-я]{2,16}$");
    Matcher matcher = pattern.matcher(dto.getName());
    if (!matcher.matches()) {
      errorsDto.addNewError(
          "name", "Имя указано неверно, доспустимая длина - от 2х до 16 символов");
    }
    if (dto.getPassword() != null && dto.getPassword().length() < 6) {
      errorsDto.addNewError("password", "Пароль короче 6-ти символов");
    }
    return errorsDto;
  }
}
