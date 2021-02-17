package com.trendsoft.newsApp.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDto {
  private Byte removePhoto;
  private String name;
  private String email;
  private String password;
}
