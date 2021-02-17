package com.trendsoft.newsApp.controllers.rest;

import com.trendsoft.newsApp.dto.requestDto.AddPostDto;
import com.trendsoft.newsApp.dto.requestDto.LikeOrDislikeDto;
import com.trendsoft.newsApp.dto.responseDto.*;
import com.trendsoft.newsApp.exceptions.BadRequestException;
import com.trendsoft.newsApp.models.Post;
import com.trendsoft.newsApp.models.Tag;
import com.trendsoft.newsApp.models.User;
import com.trendsoft.newsApp.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/post")
@RequiredArgsConstructor
public class ApiPostController {

  private final PostService postService;
  private final AuthService authService;
  private final UserService userService;
  private final TagService tagService;
  private final Tag2PostService tag2PostService;
  private final PostVotesService postVotesService;
  private final GlobalSettingsService globalSettingsService;

  @GetMapping
  public ResponseEntity<?> getAllPosts(
      @RequestParam Integer offset, @RequestParam Integer limit, @RequestParam String mode) {
    List<PostDto> postDtoList = postService.getAllPosts(offset, limit, mode);
    Integer countAllPosts = postService.getCountAllPosts();
    return ResponseEntity.ok(new PostListDto(countAllPosts, postDtoList, offset, limit, mode));
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getPostById(@PathVariable Integer id) {
    Integer userId = authService.getUserIdOnSessionId();
    User user = userService.getUserById(userId);
    return ResponseEntity.ok(postService.getPostById(id, user));
  }

  @GetMapping("/byTag")
  public ResponseEntity<?> getPostsByTag(
      @RequestParam String tag, @RequestParam Integer offset, @RequestParam Integer limit) {
    List<PostDto> postDtoList = postService.getPostsByTag(tag, offset, limit);
    Integer countPostsByTag = postService.getCountPostsByTag(tag);
    return ResponseEntity.ok(new PostListDto(countPostsByTag, postDtoList, offset, limit, tag));
  }

  @GetMapping("/search")
  public ResponseEntity<?> getPostsBySearchQuery(
      @RequestParam Integer offset,
      @RequestParam Integer limit,
      @RequestParam(required = false) String query) {
    List<PostDto> postDtoList = postService.getPostsBySearchQuery(query, limit, offset);
    Integer countPostsBySearchQuery = postService.getCountPostsBySearchQuery(query);
    return ResponseEntity.ok(
        new PostListDto(countPostsBySearchQuery, postDtoList, offset, limit, query));
  }

  @GetMapping("/byDate")
  public ResponseEntity<?> getPostsByDate(
      @RequestParam String date, @RequestParam Integer offset, @RequestParam Integer limit) {
    List<PostDto> postDtoList = postService.getPostsByDate(date, offset, limit);
    Integer countPostsByDate = postService.getCountPostsByDate(date);
    return ResponseEntity.ok(new PostListDto(countPostsByDate, postDtoList, offset, limit, date));
  }

  @GetMapping("/my")
  public ResponseEntity<?> getMyPosts(
      @RequestParam Integer offset, @RequestParam Integer limit, @RequestParam String status) {
    Integer userId = authService.getUserIdOnSessionId();
    authService.checkAuth(userId);
    List<PostDto> myPostDtoList = postService.getMyPosts(offset, limit, userId, status);
    Integer countPosts = postService.getCountMyPosts(userId, status);
    return ResponseEntity.ok(new PostListDto(countPosts, myPostDtoList, offset, limit, status));
  }

  @PostMapping
  public ResponseEntity<?> addNewPost(@RequestBody AddPostDto addPostDto) {
    Integer userId = authService.getUserIdOnSessionId();
    authService.checkAuth(userId);
    ResultFalseWithErrorsDto errorsDto = checkPostsTitleAndText(addPostDto);
    if (errorsDto.getErrors().size() > 0) {
      return ResponseEntity.badRequest().body(errorsDto);
    }
    boolean preModeration = globalSettingsService.getGlobalSettings().isPostPreModeration();
    User userFromDB = userService.getUserById(userId);
    canAddOrEditPost(userFromDB);
    Integer postId = postService.addNewPost(addPostDto, userFromDB, preModeration);
    List<Tag> tagList = tagService.tagsToPost(addPostDto.getTags());
    tag2PostService.addNewTags2Posts(postId, tagList);
    return ResponseEntity.ok(new ResultTrueDto());
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> editPostById(
      @PathVariable Integer id, @RequestBody AddPostDto addPostDto) {
    Integer userId = authService.getUserIdOnSessionId();
    authService.checkAuth(userId);
    ResultFalseWithErrorsDto errorsDto = checkPostsTitleAndText(addPostDto);
    if (errorsDto.getErrors().size() > 0) {
      return ResponseEntity.badRequest().body(errorsDto);
    }
    boolean preModeration = globalSettingsService.getGlobalSettings().isPostPreModeration();
    User userFromDB = userService.getUserById(userId);
    Post post = postService.getPostFromPostId(id);
    postService.editPostById(addPostDto, post, userFromDB, preModeration);
    List<Tag> tagList = tagService.tagsToPost(addPostDto.getTags());
    tag2PostService.addNewTags2Posts(id, tagList);
    return ResponseEntity.ok(new ResultTrueDto());
  }

  @GetMapping("/moderation")
  public ResponseEntity<?> postsToModeration(
      @RequestParam Integer offset, @RequestParam Integer limit, @RequestParam String status) {
    Integer userId = authService.getUserIdOnSessionId();
    authService.checkAuth(userId);
    User userFromDB = userService.getUserById(userId);
    if (userFromDB.getIsModerator() == 0) {
      throw new BadRequestException("Вы не являетесь модератором");
    }
    PostListDto postListDto = postService.getPostsToModeration(offset, limit, status, userId);
    return ResponseEntity.ok(postListDto);
  }

  @PostMapping("/like")
  public ResponseEntity<?> takeLikeToPost(@RequestBody LikeOrDislikeDto likeOrDislikeDto) {
    return takeLikeOrDislike(likeOrDislikeDto.getPostId(), 1);
  }

  @PostMapping("/dislike")
  public ResponseEntity<?> takeDislikeToPost(@RequestBody LikeOrDislikeDto likeOrDislikeDto) {
    return takeLikeOrDislike(likeOrDislikeDto.getPostId(), -1);
  }

  private ResponseEntity<?> takeLikeOrDislike(Integer postId, Integer likeOrDis) {
    Integer userId = authService.getUserIdOnSessionId();
    authService.checkAuth(userId);
    Post post = postService.getPostFromPostId(postId);
    if (post != null && postVotesService.takeLikeOrDislikeToPost(post, userId, likeOrDis)) {
      return ResponseEntity.ok(new ResultTrueDto());
    }
    return ResponseEntity.ok(new ResultFalseDto());
  }

  /** проверка данных при добавлении поста */
  private ResultFalseWithErrorsDto checkPostsTitleAndText(AddPostDto addPostDto) {
    ResultFalseWithErrorsDto resultFalse = new ResultFalseWithErrorsDto();
    if (addPostDto.getText().length() < 15) {
      resultFalse.addNewError("text", "Длина текста не может быть менее 15 символов");
    }
    if (addPostDto.getTitle().equals("")) {
      resultFalse.addNewError("title", "Заголовок не может быть пуст");
    }
    return resultFalse;
  }

  /** проверка на возможность добавление или изменения поста */
  private void canAddOrEditPost(User user) {
    boolean canAddPost = globalSettingsService.getGlobalSettings().isMultiUserMode();
    if (!canAddPost) {
      if (user.getIsModerator() != 1) {
        throw new BadRequestException("На данный момент запещено создавать посты");
      }
    }
  }
}
