package com.trendsoft.newsApp.services;

import com.trendsoft.newsApp.models.Tag;
import com.trendsoft.newsApp.repositories.TagRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class TagService {

  private TagRepository tagRepository;
  /**
   * метод сохраняет новые теги и возвращает лист тегов, для дальнейшего сохранения связей с
   * постами. Используется для добавления или изменения поста
   */
  public List<Tag> tagsToPost(List<String> tags) {
    return tags.stream()
        .map(
            s -> {
              Tag tag = tagRepository.findByName(s);
              if (tag == null) {
                Tag newTag = new Tag();
                newTag.setName(s);
                tagRepository.save(newTag);
                return newTag;
              } else {
                return tag;
              }
            })
        .collect(toList());
  }
}
