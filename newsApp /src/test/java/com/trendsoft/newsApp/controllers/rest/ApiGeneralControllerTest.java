package com.trendsoft.newsApp.controllers.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trendsoft.newsApp.dto.requestDto.AddCommentToPostDto;
import com.trendsoft.newsApp.dto.requestDto.LoginDto;
import com.trendsoft.newsApp.dto.requestDto.ModerationPostDto;
import com.trendsoft.newsApp.dto.responseDto.GlobalSettingsDto;
import com.trendsoft.newsApp.models.Enums.GlobalSetting;
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
public class ApiGeneralControllerTest {

  @Autowired private MockMvc mvc;

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
  public void init() {
    mvc.perform(get("/api/init"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.copyright", is("Alex Coont")));
  }

  @Test
  @SneakyThrows
  public void calendar() {
    List<String> strings = Arrays.asList("2020", "2019");
    mvc.perform(get("/api/calendar"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.years", is(strings)))
        .andExpect(jsonPath("$.posts.2020-03-18", is(1)));
  }

  @Test
  @SneakyThrows
  public void settings() {
    mvc.perform(get("/api/settings"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath(GlobalSetting.MULTIUSER_MODE.toString(), is(false)))
        .andExpect(jsonPath(GlobalSetting.POST_PREMODERATION.toString(), is(false)))
        .andExpect(jsonPath(GlobalSetting.STATISTICS_IS_PUBLIC.toString(), is(false)));
  }

  @Test
  @SneakyThrows
  public void putSettings() {
    GlobalSettingsDto globalSettingsDto = new GlobalSettingsDto(false, false, false);
    String json = om.writeValueAsString(globalSettingsDto);
    mvc.perform(
            put("/api/settings")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.MULTIUSER_MODE", is(false)));
  }

  @Test
  @SneakyThrows
  public void getAllStatistics() {
    mvc.perform(get("/api/statistics/all"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.postsCount", is(4)))
        .andExpect(jsonPath("$.likesCount", is(5)))
        .andExpect(jsonPath("$.dislikesCount", is(1)))
        .andExpect(jsonPath("$.viewsCount", is(187)))
        .andExpect(jsonPath("$.firstPublication", is("14.06.2019 06:44")));
  }

  @Test
  @SneakyThrows
  public void getMyStatistics() {
    mvc.perform(get("/api/statistics/my").session(session))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.viewsCount", is(65)));
  }

  @Test
  @SneakyThrows
  public void moderation() {
    ModerationPostDto moderationPostDto = new ModerationPostDto("ACCEPT", 3);
    String json = om.writeValueAsString(moderationPostDto);
    mvc.perform(
            post("/api/moderation")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  public void commentToPost() {
    AddCommentToPostDto addCommentToPostDto =
        new AddCommentToPostDto(null, 1, "testtesttesttesttesttest");
    String json = om.writeValueAsString(addCommentToPostDto);
    mvc.perform(
            post("/api/comment")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(9)));
  }
}
