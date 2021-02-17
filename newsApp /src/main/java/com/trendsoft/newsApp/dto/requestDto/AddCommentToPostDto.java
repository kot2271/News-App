package com.trendsoft.newsApp.dto.requestDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCommentToPostDto {
  @JsonProperty("parent_id")
  private Integer parentId;

  @JsonProperty("post_id")
  private Integer postId;

  private String text;
}
