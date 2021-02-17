package com.trendsoft.newsApp.repositories;

import com.trendsoft.newsApp.models.Tag2Post;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface Tag2PostRepository extends CrudRepository<Tag2Post, Integer> {

  /** поиск и фильтрация тегов по количеству использований в постах */
  @Query(
      value =
          "SELECT tags.name, COUNT(*) as count FROM tag2post JOIN"
              + " tags ON tag_id = tags.id JOIN posts ON post_id = posts.id"
              + " WHERE posts.is_active = 1 AND posts.moderation_status = 'ACCEPTED'"
              + " GROUP BY tags.id ORDER BY count DESC;",
      nativeQuery = true)
  List<List> findTagsAndSortByCountOfPosts();

  /** удаляет все связи тегов и указанного поста */
  @Modifying
  @Transactional
  @Query(value = "DELETE FROM tag2post " + "WHERE post_id = :post_id", nativeQuery = true)
  void deleteAllByPostId(@Param("post_id") Integer postID);
}
