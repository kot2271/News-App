package com.trendsoft.newsApp.services;

import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(value = "/application-test.properties")
public class AuthServiceTest {

  @Autowired private AuthService authService;

  @Test
  @SneakyThrows
  public void saveSession() {
    authService.saveSession(1);
    Integer userId = authService.getUserIdOnSessionId();
    assertEquals(1, userId);
  }

  @Test
  @SneakyThrows
  public void getUserIdOnSessionId() {
    Integer userId = authService.getUserIdOnSessionId();
    assertNull(userId);
  }

  @Test
  @SneakyThrows
  public void deleteSession() {
    authService.saveSession(1);
    Integer beforeUserId = authService.getUserIdOnSessionId();
    assertEquals(1, beforeUserId);
    authService.deleteSession();
    Integer afterUserId = authService.getUserIdOnSessionId();
    assertNull(afterUserId);
  }
}
