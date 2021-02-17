package com.trendsoft.newsApp.controllers.rest;

import com.trendsoft.newsApp.dto.InitEntityDto;
import com.trendsoft.newsApp.dto.requestDto.AddCommentToPostDto;
import com.trendsoft.newsApp.dto.requestDto.ModerationPostDto;
import com.trendsoft.newsApp.dto.responseDto.*;
import com.trendsoft.newsApp.exceptions.BadRequestException;
import com.trendsoft.newsApp.models.Post;
import com.trendsoft.newsApp.models.PostComment;
import com.trendsoft.newsApp.models.User;
import com.trendsoft.newsApp.services.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@RestController
@RequiredArgsConstructor
public class ApiGeneralController {

  private final Tag2PostService tag2PostService;
  private final PostService postService;
  private final AuthService authService;
  private final UserService userService;
  private final GlobalSettingsService globalSettingsService;
  private final PostCommentService postCommentService;

  @Value("${upload.path}")
  private String location;

  @GetMapping("/api/init")
  public ResponseEntity<?> init() {

    InitEntityDto initEntityDto =
        new InitEntityDto(
            "DevPub",
            "Новости для разработчиков",
            "+7(931)208 39 97",
            "kot2271@yandex.ru.ru",
            "Konstantin Kovalev",
            "2021");
    return ResponseEntity.ok(initEntityDto);
  }

  @GetMapping("/api/tag")
  public ResponseEntity<?> getTag() {
    List<TagDto> tagDtoList = tag2PostService.getTagsSortedByUsingInPost();

    return ResponseEntity.ok(new TagListDto(tagDtoList));
  }

  @GetMapping("/api/calendar")
  public ResponseEntity<?> calendar(@RequestParam(defaultValue = "2021") String year) {
    return ResponseEntity.ok(postService.getPostsToCalendar(year));
  }

  @GetMapping("/api/settings")
  public ResponseEntity<?> settings() {
    GlobalSettingsDto globalSettingsDto = globalSettingsService.getGlobalSettings();
    return ResponseEntity.ok(globalSettingsDto);
  }

  @PutMapping("/api/settings")
  public ResponseEntity<?> putSettings(@RequestBody GlobalSettingsDto globalSettingsDto) {
    Integer userId = authService.getUserIdOnSessionId();
    authService.checkAuth(userId);
    User userFromDB = userService.getUserById(userId);
    if (userFromDB.getIsModerator() == 1) {
      globalSettingsService.addNewSettings(globalSettingsDto);
      return ResponseEntity.ok(globalSettingsDto);
    }
    throw new BadRequestException("У вас нет прав на изменение настроек блога");
  }

  @GetMapping("/api/statistics/all")
  public ResponseEntity<?> getAllStatistics() {
    Integer postsCount = postService.getCountAllPosts();
    Integer viewsCount = postService.getPostsViewsCount();
    Integer likesCount = postService.getAllPostsLikesOrDislikesCount(1);
    Integer dislikesCount = postService.getAllPostsLikesOrDislikesCount(-1);
    LocalDateTime firstPublication = postService.getFirstPublicationFromAllPosts();
    return ResponseEntity.ok(
        new StatisticsDto(postsCount, likesCount, dislikesCount, viewsCount, firstPublication));
  }

  @GetMapping("/api/statistics/my")
  public ResponseEntity<?> getMyStatistics() {
    Integer userId = authService.getUserIdOnSessionId();
    authService.checkAuth(userId);
    Integer postsCount = postService.getCountUserPosts(userId);
    Integer viewsCount = postService.getMyPostsViewsCount(userId);
    Integer likesCount = postService.getMyLikesOrDislikesCount(userId, 1);
    Integer dislikesCount = postService.getMyLikesOrDislikesCount(userId, -1);
    LocalDateTime firstPublication = postService.getMyFirstPublication(userId);
    return ResponseEntity.ok(
        new StatisticsDto(postsCount, likesCount, dislikesCount, viewsCount, firstPublication));
  }

  @PostMapping("/api/moderation")
  @ResponseStatus(HttpStatus.OK)
  public void moderation(@RequestBody ModerationPostDto moderationPostDto) {
    Integer userId = authService.getUserIdOnSessionId();
    authService.checkAuth(userId);
    User userFromDB = userService.getUserById(userId);
    if (userFromDB.getIsModerator() == 0) {
      throw new BadRequestException("Вы не являетесь модератором");
    }
    postService.moderationPost(moderationPostDto, userId);
  }

  @SneakyThrows
  @PostMapping("/api/image")
  public ResponseEntity<?> image(
      @RequestParam(value = "image", required = false) MultipartFile file) {
    Integer userId = authService.getUserIdOnSessionId();
    authService.checkAuth(userId);
    if (file.isEmpty()) {
      throw new BadRequestException("Вы не являетесь модератором");
    }

    String type = file.getContentType().split("/")[1];
    String randomName = randomAlphanumeric(10);
    String generateDirs =
        randomAlphabetic(2).toLowerCase()
            + "/"
            + randomAlphabetic(2).toLowerCase()
            + "/"
            + randomAlphabetic(2).toLowerCase()
            + "/";
    File uploadFolder = new File(location + generateDirs);
    if (!uploadFolder.exists()) {
      uploadFolder.mkdirs();
    }
    String path = location + generateDirs + randomName + "." + type;
    File dstImage = new File(path);
    userService.saveImage(200, file, dstImage, type);

    return ResponseEntity.ok("/" + location + generateDirs + randomName + "." + type);
  }

  @PostMapping("/api/comment")
  public ResponseEntity<?> commentToPost(@RequestBody AddCommentToPostDto addCommentToPostDto) {
    Integer userId = authService.getUserIdOnSessionId();
    authService.checkAuth(userId);
    if (addCommentToPostDto.getText().length() < 6) {
      ResultFalseWithErrorsDto resultFalseWithErrorsDto = new ResultFalseWithErrorsDto();
      resultFalseWithErrorsDto.addNewError(
          "text", "Длина комментария должна быть равна 6 или более символам");
      return ResponseEntity.ok(resultFalseWithErrorsDto);
    }
    Post post = checkPostForCommentIsPresent(addCommentToPostDto.getPostId());
    if (addCommentToPostDto.getParentId() != null) {
      checkParentIdForCommentIsPresent(
          addCommentToPostDto.getParentId(), addCommentToPostDto.getPostId());
    }
    User userFromDB = userService.getUserById(userId);
    Integer newCommentId =
        postCommentService.addNewCommentToPost(addCommentToPostDto, post, userFromDB);
    return ResponseEntity.ok(new SuccessAddCommentDto(newCommentId));
  }

  private Post checkPostForCommentIsPresent(Integer postId) {
    if (postId == null) {
      throw new BadRequestException("Пост не существует");
    }
    Post post = postService.getPostFromPostId(postId);
    if (post == null) {
      throw new BadRequestException("Пост не существует");
    }
    return post;
  }

  private void checkParentIdForCommentIsPresent(Integer parentId, Integer postId) {
    PostComment comment = postCommentService.getPostCommentByParentIdAndPostId(parentId, postId);
    if (comment == null) {
      throw new BadRequestException("Комментарий, на который вы хотите ответить, не существует");
    }
  }
}
