package com.trendsoft.newsApp.services;

import com.trendsoft.newsApp.models.Tag;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;

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
public class TagServiceTest {
  @Autowired private TagService tagService;

  @Test
  @SneakyThrows
  public void tagsToPost() {
    List<String> tags = new ArrayList<>();
    tags.add("TEST");
    List<Tag> tagList = tagService.tagsToPost(tags);
    assertEquals("TEST", tagList.get(0).getName());
  }
}
