package com.trendsoft.newsApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InitEntityDto {

  private String title;

  private String subtitle;

  private String phone;

  private String email;

  private String copyright;

  private String copyrightFrom;
}
