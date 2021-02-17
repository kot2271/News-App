package com.trendsoft.newsApp.controllers.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trendsoft.newsApp.dto.requestDto.AddPostDto;
import com.trendsoft.newsApp.dto.requestDto.LikeOrDislikeDto;
import com.trendsoft.newsApp.dto.requestDto.LoginDto;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(value = "/application-test.properties")
@SqlGroup({
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/data_test.sql"),
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/clean.sql")
})
public class ApiPostControllerTest {
  @Autowired private MockMvc mvc;
  private final String basePath = "/api/post";

  @Autowired private ApiAuthController apiAuthController;
  private static final ObjectMapper om = new ObjectMapper();
  private final MockHttpSession session = new MockHttpSession();

  @Before
  @SneakyThrows
  public void before() {
    LoginDto loginDto = new LoginDto("asd@mail.ru", "qweasdzxc");
    apiAuthController.login(loginDto);

    String userJson = om.writeValueAsString(loginDto);
    mvc.perform(
            post("/api/auth/login")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  public void getAllPosts() {
    mvc.perform(
            get(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .param("offset", "0")
                .param("limit", "10")
                .param("mode", "recent"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.count", is(4)))
        .andExpect(jsonPath("$.posts.[0].time", is("18.03.2020 19:53:34")));

    mvc.perform(
            get(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .param("offset", "0")
                .param("limit", "10")
                .param("mode", "early"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.count", is(4)))
        .andExpect(jsonPath("$.posts.[0].time", is("14.06.2019 06:44:18")));

    mvc.perform(
            get(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .param("offset", "0")
                .param("limit", "10")
                .param("mode", "popular"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.count", is(4)))
        .andExpect(jsonPath("$.posts.[0].commentCount", is(3)));

    mvc.perform(
            get(basePath)
                .contentType(MediaType.APPLICATION_JSON)
                .param("offset", "0")
                .param("limit", "10")
                .param("mode", "best"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.count", is(4)))
        .andExpect(jsonPath("$.posts.[0].likeCount", is(3)));
  }

  @Test
  @SneakyThrows
  public void getPostById() {
    mvc.perform(get(basePath + "/5"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title", is("platea dictumst aliquam augue")));
  }

  @Test
  @SneakyThrows
  public void getPostsByTag() {
    mvc.perform(
            get(basePath + "/byTag").param("offset", "0").param("limit", "10").param("tag", "JAVA"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.posts.[0].id", is(1)));
  }

  @Test
  @SneakyThrows
  public void getPostsBySearchQuery() {
    mvc.perform(
            get(basePath + "/search")
                .param("offset", "0")
                .param("limit", "10")
                .param("query", "dictumst"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.posts.[0].title", is("platea dictumst aliquam augue")));
  }

  @Test
  @SneakyThrows
  public void getPostsByDate() {
    mvc.perform(
            get(basePath + "/byDate")
                .param("offset", "0")
                .param("limit", "10")
                .param("date", "2019-08-04"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.posts.[0].title", is("in purus eu magna")));
  }

  @Test
  @SneakyThrows
  public void getMyPosts() {
    mvc.perform(
            get(basePath + "/my")
                .session(session)
                .param("offset", "0")
                .param("limit", "10")
                .param("status", "inactive"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.count", is(0)));

    mvc.perform(
            get(basePath + "/my")
                .session(session)
                .param("offset", "0")
                .param("limit", "10")
                .param("status", "pending"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.count", is(1)))
        .andExpect(jsonPath("$.posts.[0].title", is("posuere metus vitae ipsum aliquam")));

    mvc.perform(
            get(basePath + "/my")
                .session(session)
                .param("offset", "0")
                .param("limit", "10")
                .param("status", "declined"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.count", is(0)));

    mvc.perform(
            get(basePath + "/my")
                .session(session)
                .param("offset", "0")
                .param("limit", "10")
                .param("status", "published"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.count", is(1)))
        .andExpect(jsonPath("$.posts.[0].title", is("llsajwqio sjaks qq")));
  }

  @Test
  @SneakyThrows
  public void addNewPost() {
    List<String> tags = Arrays.asList("WORLD", "TEST2");
    AddPostDto addPostDto =
        new AddPostDto("2020-06-16 13:15", (byte) 1, "test", "mnogotestamnogotesta", tags);
    String json = om.writeValueAsString(addPostDto);
    mvc.perform(
            post(basePath).session(session).contentType(MediaType.APPLICATION_JSON).content(json))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is(true)));
  }

  @Test
  @SneakyThrows
  public void editPostById() {
    List<String> tags = Arrays.asList("WORLD", "TEST2");
    AddPostDto addPostDto =
        new AddPostDto("2020-06-16 13:15", (byte) 1, "test", "mnogotestamnogotesta", tags);
    String json = om.writeValueAsString(addPostDto);
    mvc.perform(
            put(basePath + "/6")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is(true)));
  }

  @Test
  @SneakyThrows
  public void postsToModeration() {
    mvc.perform(
            get(basePath + "/moderation")
                .session(session)
                .param("offset", "0")
                .param("limit", "10")
                .param("status", "new"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.count", is(1)))
        .andExpect(jsonPath("$.posts.[0].title", is("posuere metus vitae ipsum aliquam")));

    mvc.perform(
            get(basePath + "/moderation")
                .session(session)
                .param("offset", "0")
                .param("limit", "10")
                .param("status", "accepted"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.count", is(1)))
        .andExpect(jsonPath("$.posts.[0].title", is("llsajwqio sjaks qq")));

    mvc.perform(
            get(basePath + "/moderation")
                .session(session)
                .param("offset", "0")
                .param("limit", "10")
                .param("status", "declined"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.count", is(1)))
        .andExpect(jsonPath("$.posts.[0].title", is("lqweqweqw sadas")));
  }

  @Test
  @SneakyThrows
  public void takeLikeToPost() {
    LikeOrDislikeDto likeOrDislikeDto = new LikeOrDislikeDto(4);
    String json = om.writeValueAsString(likeOrDislikeDto);
    mvc.perform(
            post(basePath + "/like")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is(true)));
  }

  @Test
  @SneakyThrows
  public void takeDislikeToPost() {
    LikeOrDislikeDto likeOrDislikeDto = new LikeOrDislikeDto(5);
    String json = om.writeValueAsString(likeOrDislikeDto);
    mvc.perform(
            post(basePath + "/dislike")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is(true)));
  }
}
