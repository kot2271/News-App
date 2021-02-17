package com.trendsoft.newsApp.controllers.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trendsoft.newsApp.dto.requestDto.LoginDto;
import com.trendsoft.newsApp.dto.requestDto.ProfileDto;
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

import static org.hamcrest.Matchers.is;
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
public class ApiProfileControllerTest {
  @Autowired private MockMvc mvc;
  @Autowired private ApiAuthController apiAuthController;
  private static final ObjectMapper om = new ObjectMapper();
  private final String basePath = "/api/profile/my";
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

  public void editProfileWithPhoto() {}

  @Test
  @SneakyThrows
  public void editProfile() {
    ProfileDto dto = new ProfileDto((byte) 1, "Alex", "asd@mail.ru", null);
    String json = om.writeValueAsString(dto);
    mvc.perform(
            post(basePath).session(session).contentType(MediaType.APPLICATION_JSON).content(json))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result", is(true)));
  }
}
