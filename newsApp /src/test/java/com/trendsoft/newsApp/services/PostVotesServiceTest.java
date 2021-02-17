package com.trendsoft.newsApp.services;

import com.trendsoft.newsApp.models.Post;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
@SqlGroup({
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/data_test.sql"),
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/clean.sql")
})
public class PostVotesServiceTest {
  @Autowired private PostVotesService postVotesService;

  @Test
  @SneakyThrows
  public void takeLikeOrDislikeToPost() {
    Post post = new Post();
    post.setId(2);
    boolean like = postVotesService.takeLikeOrDislikeToPost(post, 3, 1);
    assertTrue(like);
  }
}
