package com.trendsoft.newsApp.services;

import com.trendsoft.newsApp.dto.requestDto.RegistrationDto;
import com.trendsoft.newsApp.models.User;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
@SqlGroup({
  @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/data_test.sql"),
  @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/clean.sql")
})
public class UserServiceTest {
  @Autowired private UserService userService;

  @Test
  @SneakyThrows
  public void getUserByEmailAndPassword() {
    User user = userService.getUserByEmailAndPassword("rty@mail.ru", "qweasdzxc");
    assertEquals("Roman", user.getName());
  }

  @Test
  @SneakyThrows
  public void getUserById() {
    User user = userService.getUserById(2);
    assertEquals("Artem", user.getName());
  }

  @Test
  @SneakyThrows
  public void registration() {
    RegistrationDto registrationDto =
        new RegistrationDto("test@test.test", "Test", "testtest", "9639", "rksfyruau6ucvfrggwagrq");
    userService.registration(registrationDto);
    User user = userService.getUserByEmailAndPassword("test@test.test", "testtest");
    assertEquals("Test", user.getName());
  }

  @Test
  @SneakyThrows
  public void userExistByEmail() {
    boolean userExist = userService.userExistByEmail("qwe@mail.ru");
    assertTrue(userExist);
  }

  @Test
  @SneakyThrows
  public void getUsersRestorePasswordCode() {
    String restoreCode = userService.getUsersRestorePasswordCode("asd@mail.ru");
    User user = userService.getUserById(2);
    assertEquals(restoreCode, user.getCode());
  }

  @Test
  @SneakyThrows
  public void getUserByRestoreCode() {
    String restoreCode = userService.getUsersRestorePasswordCode("rty@mail.ru");
    User user = userService.getUserByRestoreCode(restoreCode);
    assertEquals("2020-04-09T19:13:25", user.getRegTime().toString());
  }

  @Test
  @SneakyThrows
  public void restoreUserPassword() {
    User user = userService.getUserById(1);
    userService.restoreUserPassword(user, "qqqqqq");
    User fromDB = userService.getUserById(1);
    boolean auth = new BCryptPasswordEncoder().matches("qqqqqq", fromDB.getPassword());
    assertTrue(auth);
  }
}
