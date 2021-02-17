package com.trendsoft.newsApp.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeOrDislikeDto {
  @JsonProperty("post_id")
  private Integer postId;
}
