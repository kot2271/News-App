package com.trendsoft.newsApp.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {

  private Integer id;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
  private LocalDateTime time;

  private UserToPostDto user;

  private String title;

  private String announce;

  private Integer likeCount;

  private Integer dislikeCount;

  private Integer commentCount;

  private Integer viewCount;
}
