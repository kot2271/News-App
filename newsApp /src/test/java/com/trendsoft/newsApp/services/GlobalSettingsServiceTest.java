package com.trendsoft.newsApp.services;

import com.trendsoft.newsApp.dto.responseDto.GlobalSettingsDto;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertFalse;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
@SqlGroup({
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/data_test.sql"),
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/clean.sql")
})
public class GlobalSettingsServiceTest {

  @Autowired private GlobalSettingsService globalSettingsService;

  @Test
  @SneakyThrows
  public void getGlobalSettings() {
    GlobalSettingsDto dto = globalSettingsService.getGlobalSettings();
    assertFalse(dto.isMultiUserMode());
  }

  @Test
  @SneakyThrows
  public void addNewSettings() {
    GlobalSettingsDto globalSettingsDto = new GlobalSettingsDto(false, false, true);
    globalSettingsService.addNewSettings(globalSettingsDto);
    GlobalSettingsDto dto = globalSettingsService.getGlobalSettings();
    assertFalse(dto.isPostPreModeration());
  }
}
