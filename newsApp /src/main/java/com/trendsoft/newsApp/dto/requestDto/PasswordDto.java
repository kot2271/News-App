package com.trendsoft.newsApp.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordDto {
  private String code;
  private String password;
  private String captcha;

  @JsonProperty("captcha_secret")
  private String captchaSecret;
}
