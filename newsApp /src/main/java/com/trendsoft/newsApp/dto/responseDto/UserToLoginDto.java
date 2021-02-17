package com.trendsoft.newsApp.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserToLoginDto {
  private Integer id;
  private String name;
  private String photo;
  private String email;
  private boolean moderation;
  private Integer moderationCount;
  private boolean settings;
}
