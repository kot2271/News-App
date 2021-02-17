package com.trendsoft.newsApp.dto.responseDto;

import lombok.Data;

import java.util.Map;
import java.util.TreeMap;

@Data
public class ResultFalseWithErrorsDto {
  private boolean result = false;
  private Map<String, String> errors = new TreeMap<>();

  public void addNewError(String name, String text) {
    errors.put(name, text);
  }
}
