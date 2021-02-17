package com.trendsoft.newsApp.dto.responseDto;

import lombok.Data;

@Data
public class ResultTrueDtoWithUser {
  private boolean result = true;
  private UserToLoginDto user;

  public ResultTrueDtoWithUser(UserToLoginDto userToLoginDto) {
    this.user = userToLoginDto;
  }
}
