package com.trendsoft.newsApp.repositories;

import org.springframework.data.jpa.repository.Query;
import com.trendsoft.newsApp.models.PostVote;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostVotesRepository extends CrudRepository<PostVote, Integer> {

  /** поиск лайка по айди юзера и поста */
  @Query(
      value = "SELECT * FROM post_votes " + "WHERE user_id = :user_id " + "AND post_id = :post_id",
      nativeQuery = true)
  Optional<PostVote> findByPostIdAndUserId(
      @Param("post_id") Integer postId, @Param("user_id") Integer userId);
}
