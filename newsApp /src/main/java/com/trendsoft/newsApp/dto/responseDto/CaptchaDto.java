package com.trendsoft.newsApp.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CaptchaDto {
  private String secret;
  private String image;
}
