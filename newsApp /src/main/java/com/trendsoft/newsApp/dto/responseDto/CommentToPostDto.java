package com.trendsoft.newsApp.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentToPostDto {

  private Integer id;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
  private LocalDateTime time;

  private String text;

  UserToPostCommentDto user;
}
