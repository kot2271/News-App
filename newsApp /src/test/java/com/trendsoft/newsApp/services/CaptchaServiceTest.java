package com.trendsoft.newsApp.services;

import com.trendsoft.newsApp.models.CaptchaCode;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
@SqlGroup({
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/data_test.sql"),
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/clean.sql")
})
public class CaptchaServiceTest {

  @Autowired private CaptchaService captchaService;

  @Test
  @SneakyThrows
  public void generateCaptcha() {
    CaptchaCode captcha = captchaService.generateCaptcha();
    assertEquals(2, captcha.getId());
  }

  @Test
  @SneakyThrows
  public void codeFromSecretCode() {
    CaptchaCode captcha = captchaService.generateCaptcha();
    String secretCode = captcha.getSecretCode();
    String expectedCode = captcha.getCode();
    String actualCode = captchaService.codeFromSecretCode(secretCode);
    assertEquals(expectedCode, actualCode);
  }
}
