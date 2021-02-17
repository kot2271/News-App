package com.trendsoft.newsApp.services;

import com.trendsoft.newsApp.dto.responseDto.TagDto;
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
public class Tag2PostServiceTest {
  @Autowired private Tag2PostService tag2PostService;

  @Test
  @SneakyThrows
  public void addNewTags2Posts() {
    Tag tag = new Tag();
    tag.setName("TEST");
    tag.setId(4);
    List<Tag> tagList = new ArrayList<>();
    tagList.add(tag);
    tag2PostService.addNewTags2Posts(1, tagList);
    List<TagDto> tagDtoList = tag2PostService.getTagsSortedByUsingInPost();
    assertEquals(2, tagDtoList.size());
  }

  @Test
  @SneakyThrows
  public void getTagsSortedByUsingInPost() {
    List<TagDto> tagDtoList = tag2PostService.getTagsSortedByUsingInPost();
    assertEquals(3, tagDtoList.size());
  }
}
