package com.trendsoft.newsApp.services;

import com.trendsoft.newsApp.dto.requestDto.AddCommentToPostDto;
import com.trendsoft.newsApp.models.Post;
import com.trendsoft.newsApp.models.PostComment;
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


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
@SqlGroup({
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/data_test.sql"),
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/clean.sql")
})
public class PostCommentServiceTest {
  @Autowired private PostCommentService postCommentService;

  @Test
  @SneakyThrows
  public void addNewCommentToPost() {
    AddCommentToPostDto addCommentToPostDto = new AddCommentToPostDto(1, 1, "test");
    Post post = new Post();
    post.setId(1);
    User user = new User();
    user.setId(1);
    Integer commentId = postCommentService.addNewCommentToPost(addCommentToPostDto, post, user);
    assertEquals(9, commentId);
  }

  @Test
  @SneakyThrows
  public void getPostCommentByParentIdAndPostId() {
    PostComment postComment = postCommentService.getPostCommentByParentIdAndPostId(1, 1);
    assertNull(postComment);
  }
}
