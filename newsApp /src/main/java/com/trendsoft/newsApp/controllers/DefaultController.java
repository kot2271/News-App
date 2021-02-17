package com.trendsoft.newsApp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class DefaultController {

  @GetMapping
  public String index() {
    return "index";
  }

  @RequestMapping(
      method = {RequestMethod.OPTIONS, RequestMethod.GET},
      value = "/**/{path:[^\\.]*}")
  public String redirectToIndex(@PathVariable String path) {
    return "forward:/";
  }
}
