package com.trendsoft.newsApp.services;

import com.trendsoft.newsApp.dto.responseDto.TagDto;
import com.trendsoft.newsApp.models.Tag;
import com.trendsoft.newsApp.models.Tag2Post;
import com.trendsoft.newsApp.repositories.Tag2PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class Tag2PostService {

  private Tag2PostRepository tag2PostRepository;

  /** сохранение связей постов и тегов */
  @Transactional
  public void addNewTags2Posts(Integer postId, List<Tag> tagList) {
    List<Tag2Post> tag2PostList = new ArrayList<>();
    tag2PostRepository.deleteAllByPostId(postId);
    tagList.forEach(
        tag -> {
          Tag2Post tag2Post = new Tag2Post();
          tag2Post.setPostId(postId);
          tag2Post.setTagId(tag.getId());
          tag2PostList.add(tag2Post);
        });
    tag2PostRepository.saveAll(tag2PostList);
  }

  /** получение списка постов, отсортированных по количеству использований в постах */
  public List<TagDto> getTagsSortedByUsingInPost() {
    List<List> list = tag2PostRepository.findTagsAndSortByCountOfPosts();
    if (list.size() == 0) {
      return null;
    }
    List<TagDto> tagDtos = new ArrayList<>();
    float k =
        (float) ((int) ((1.0 / ((BigInteger) list.get(0).get(1)).floatValue()) * 100) / 100.0);
    list.forEach(
        e -> {
          float weight = ((BigInteger) e.get(1)).floatValue() * k;
          tagDtos.add(new TagDto((String) e.get(0), weight < 0.3 ? 0.3f : weight));
        });
    return tagDtos;
  }
}
