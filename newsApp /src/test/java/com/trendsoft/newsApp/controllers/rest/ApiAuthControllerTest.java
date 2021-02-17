package com.trendsoft.newsApp.controllers.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trendsoft.newsApp.dto.requestDto.EmailDto;
import com.trendsoft.newsApp.dto.requestDto.LoginDto;
import com.trendsoft.newsApp.dto.requestDto.PasswordDto;
import com.trendsoft.newsApp.dto.requestDto.RegistrationDto;
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
import org.springframework.web.context.request.RequestContextHolder;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
public class ApiAuthControllerTest {

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
  public void login() {
    LoginDto loginDto = new LoginDto("rty@mail.ru", "qweasdzxc");
    String json = om.writeValueAsString(loginDto);

    mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(json))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.user.name", is("Roman")));
  }

  @Test
  @SneakyThrows
  public void check() {
    RequestContextHolder.currentRequestAttributes().getSessionId();
    mvc.perform(get("/api/auth/check").session(session))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.user.id", is(2)));
  }

  @Test
  @SneakyThrows
  public void logout() {
    mvc.perform(get("/api/auth/logout").session(session))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is(true)));
  }

  @Test
  @SneakyThrows
  public void captcha() {
    mvc.perform(
            get("/api/auth/captcha")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  public void register() {
    RegistrationDto registrationDto =
        new RegistrationDto("test@test.test", "test", "password", "9639", "rksfyruau6ucvfrggwagrq");
    String json = om.writeValueAsString(registrationDto);
    mvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is(true)));
  }

  @Test
  @SneakyThrows
  public void restore() {
    EmailDto emailDto = new EmailDto("asd@mail.ru");
    String json = om.writeValueAsString(emailDto);
    mvc.perform(
            post("/api/auth/restore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is(true)));
  }

  @Test
  @SneakyThrows
  public void password() {
    PasswordDto passwordDto =
        new PasswordDto(
            "HWQfassdJjkgd12552899SJasfklga", "qeqeqeq", "9639", "rksfyruau6ucvfrggwagrq");
    String json = om.writeValueAsString(passwordDto);
    mvc.perform(post("/api/auth/password").contentType(MediaType.APPLICATION_JSON).content(json))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is(true)));
  }
}
