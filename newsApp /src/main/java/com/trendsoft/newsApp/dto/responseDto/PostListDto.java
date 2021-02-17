package com.trendsoft.newsApp.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PostListDto {

  private Integer count;

  private List<PostDto> posts;

  private Integer offset;

  private Integer limit;

  private String mode;
}
