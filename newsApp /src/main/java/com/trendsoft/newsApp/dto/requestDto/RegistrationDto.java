package com.trendsoft.newsApp.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDto {
  @JsonProperty("e_mail")
  private String email;

  private String name;
  private String password;
  private String captcha;

  @JsonProperty("captcha_secret")
  private String captchaSecret;
}
