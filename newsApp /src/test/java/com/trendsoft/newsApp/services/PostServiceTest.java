package com.trendsoft.newsApp.services;

import com.trendsoft.newsApp.dto.requestDto.AddPostDto;
import com.trendsoft.newsApp.dto.requestDto.ModerationPostDto;
import com.trendsoft.newsApp.dto.responseDto.CalendarDto;
import com.trendsoft.newsApp.dto.responseDto.OnePostDto;
import com.trendsoft.newsApp.dto.responseDto.PostDto;
import com.trendsoft.newsApp.dto.responseDto.PostListDto;
import com.trendsoft.newsApp.models.Enums.ModerationStatus;
import com.trendsoft.newsApp.models.Post;
import com.trendsoft.newsApp.models.User;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
@SqlGroup({
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/data_test.sql"),
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/clean.sql")
})
public class PostServiceTest {

  @Autowired private PostService postService;

  @Test
  @SneakyThrows
  public void getAllPosts() {
    List<PostDto> postDtoList = postService.getAllPosts(0, 10, "recent");
    assertEquals(4, postDtoList.size());
  }

  @Test
  @SneakyThrows
  public void getCountAllPosts() {
    Integer count = postService.getCountAllPosts();
    assertEquals(4, count);
  }

  @Test
  @SneakyThrows
  public void getPostById() {
    OnePostDto post = postService.getPostById(1, null);
    assertEquals("nulla ut erat id mauris", post.getTitle());
  }

  @Test
  @SneakyThrows
  public void getPostsByTag() {
    List<PostDto> postDtoList = postService.getPostsByTag("JAVA", 0, 10);
    assertEquals(1, postDtoList.size());
  }

  @Test
  @SneakyThrows
  public void getCountPostsByTag() {
    Integer count = postService.getCountPostsByTag("WORLD");
    assertEquals(2, count);
  }

  @Test
  @SneakyThrows
  public void getPostsBySearchQuery() {
    List<PostDto> postDtoList = postService.getPostsBySearchQuery("dictumst", 10, 0);
    assertEquals(1, postDtoList.size());
  }

  @Test
  @SneakyThrows
  public void countPostsBySearchQuery() {
    Integer count = postService.getCountPostsBySearchQuery("p");
    assertEquals(2, count);
  }

  @Test
  @SneakyThrows
  public void getPostsToCalendar() {
    CalendarDto calendarDto = postService.getPostsToCalendar("2020");
    assertEquals(2, calendarDto.getYears().size());
  }

  @Test
  @SneakyThrows
  public void getPostsByDate() {
    List<PostDto> postDtoList = postService.getPostsByDate("2019-06-14", 0, 10);
    assertEquals("platea dictumst aliquam augue", postDtoList.get(0).getTitle());
  }

  @Test
  @SneakyThrows
  public void getCountPostsByDate() {
    Integer count = postService.getCountPostsByDate("2019-08-04");
    assertEquals(1, count);
  }

  @Test
  @SneakyThrows
  public void getCountPostsToModeration() {
    Integer count = postService.getCountPostsToModeration();
    assertEquals(1, count);
  }

  @Test
  @SneakyThrows
  public void getMyPosts() {
    List<PostDto> postDtoList = postService.getMyPosts(0, 10, 2, "pending");
    assertEquals(1, postDtoList.size());
  }

  @Test
  @SneakyThrows
  public void getCountMyPosts() {
    Integer count = postService.getCountMyPosts(3, "published");
    assertEquals(2, count);
  }

  @Test
  @SneakyThrows
  public void addNewPost() {
    List<String> tags = new ArrayList<>();
    tags.add("TEST");
    tags.add("PAIN");
    AddPostDto addPostDto =
        new AddPostDto(
            "2020-06-14 17:00", (byte) 1, "offline test", "tests = a lot of pain and work", tags);
    User user = new User();
    user.setIsModerator((byte) 0);
    user.setId(5);
    Integer newPostId = postService.addNewPost(addPostDto, user, false);
    assertEquals(8, newPostId);
  }

  @Test
  @SneakyThrows
  public void editPostById() {
    Post post = postService.getPostFromPostId(1);
    User user = new User();
    user.setIsModerator((byte) 1);
    AddPostDto addPostDto =
        new AddPostDto(
            "2020-06-14 17:00",
            (byte) 1,
            "offline test",
            "tests = a lot of pain and work",
            new ArrayList<>());
    postService.editPostById(addPostDto, post, user, false);
    OnePostDto onePost = postService.getPostById(1, user);
    assertEquals("tests = a lot of pain and work", onePost.getText());
  }

  @Test
  @SneakyThrows
  public void getPostsViewsCount() {
    Integer count = postService.getPostsViewsCount();
    assertEquals(187, count);
  }

  @Test
  @SneakyThrows
  public void getAllPostsLikesOrDislikesCount() {
    Integer count = postService.getAllPostsLikesOrDislikesCount(1);
    assertEquals(5, count);
  }

  @Test
  @SneakyThrows
  public void getFirstPublicationFromAllPosts() {
    LocalDateTime firstPublication = postService.getFirstPublicationFromAllPosts();
    assertEquals("2019-06-14T06:44:18", firstPublication.toString());
  }

  @Test
  @SneakyThrows
  public void getCountUserPosts() {
    Integer count = postService.getCountUserPosts(3);
    assertEquals(2, count);
  }

  @Test
  @SneakyThrows
  public void getMyLikesOrDislikesCount() {
    Integer count = postService.getMyLikesOrDislikesCount(3, 1);
    assertEquals(4, count);
  }

  @Test
  @SneakyThrows
  public void getMyFirstPublication() {
    LocalDateTime firstPublication = postService.getMyFirstPublication(1);
    assertEquals("2020-03-18T19:53:34", firstPublication.toString());
  }

  @Test
  @SneakyThrows
  public void getMyPostsViewsCount() {
    Integer count = postService.getMyPostsViewsCount(3);
    assertEquals(92, count);
  }

  @Test
  @SneakyThrows
  public void getPostsToModeration() {
    PostListDto postListDto = postService.getPostsToModeration(0, 10, "accepted", 1);
    assertEquals("nulla ut erat id mauris", postListDto.getPosts().get(0).getTitle());
  }

  @Test
  @SneakyThrows
  public void moderationPost() {
    ModerationPostDto moderationPostDto = new ModerationPostDto("accept", 3);
    postService.moderationPost(moderationPostDto, 1);
    Post post = postService.getPostFromPostId(3);
    assertEquals(ModerationStatus.ACCEPTED, post.getModerationStatus());
  }

  @Test
  @SneakyThrows
  public void getPostFromPostId() {
    Post post = postService.getPostFromPostId(2);
    assertEquals("leo rhoncus sed vestibulum", post.getTitle());
  }
}
