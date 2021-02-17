package com.trendsoft.newsApp.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserToPostCommentDto {

  private Integer id;

  private String name;

  private String photo;
}
