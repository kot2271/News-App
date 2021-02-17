package com.trendsoft.newsApp.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModerationPostDto {
  private String decision;

  @JsonProperty("post_id")
  private Integer postId;
}
