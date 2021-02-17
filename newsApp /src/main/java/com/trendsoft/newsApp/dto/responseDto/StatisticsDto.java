package com.trendsoft.newsApp.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class StatisticsDto {
  private Integer postsCount;
  private Integer likesCount;
  private Integer dislikesCount;
  private Integer viewsCount;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
  private LocalDateTime firstPublication;
}
