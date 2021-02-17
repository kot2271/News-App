package com.trendsoft.newsApp.repositories;

import com.trendsoft.newsApp.models.Enums.ModerationStatus;
import com.trendsoft.newsApp.models.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {

  /** Запрос для api/post/recent */
  List<Post> findAllByModerationStatusAndIsActiveOrderByTimeDesc(
      ModerationStatus moderationStatus, Byte active, Pageable pageable);

  /** Запрос для api/post/early */
  List<Post> findAllByModerationStatusAndIsActiveOrderByTimeAsc(
      ModerationStatus moderationStatus, Byte active, Pageable pageable);

  /** Запрос для api/post/popular */
  @Query(
      value =
          "select posts.* "
              + "from posts "
              + "left join post_comments "
              + "on posts.id = post_comments.post_id "
              + "where posts.is_active = 1 "
              + "and posts.moderation_status = 'ACCEPTED'"
              + "and posts.time <= now()"
              + "group by posts.id "
              + "order by count(post_comments.post_id) desc",
      nativeQuery = true)
  List<Post> mostPopularPosts(Pageable pageable);

  /** Запрос для api/post/best */
  @Query(
      value =
          "SELECT posts.* "
              + "FROM posts "
              + "left join post_votes "
              + "on posts.id = post_votes.post_id "
              + "where posts.is_active = 1 and posts.moderation_status = 'ACCEPTED' "
              + "and posts.time <= now() and post_votes.value = 1 "
              + "group by posts.id "
              + "order by count(post_votes.post_id) desc",
      nativeQuery = true)
  List<Post> bestPosts(Pageable pageable);

  /** запрос общего количества постов для запроса api/post */
  @Query(
      value =
          "SELECT count(*) FROM posts "
              + "WHERE is_active = 1 "
              + "AND moderation_status = :moderation_status "
              + "AND time <= now()",
      nativeQuery = true)
  Integer countAllPosts(@Param("moderation_status") String moderationStatus);

  /** Запрос для api/post/{id} */
  Post getByIdAndModerationStatusAndIsActive(
      Integer id, ModerationStatus moderationStatus, Byte active);

  /** Запрос для api/tag/ */
  @Query(
      value =
          "SELECT posts.* FROM posts "
              + "LEFT JOIN tag2post "
              + "ON posts.id = tag2post.post_id "
              + "LEFT JOIN tags "
              + "ON tag2post.tag_id = tags.id "
              + "WHERE posts.is_active = 1 "
              + "AND posts.moderation_status = 'ACCEPTED' "
              + "AND posts.time <= now() "
              + "AND tags.name = :tag_name",
      nativeQuery = true)
  List<Post> getPostsByTag(@Param("tag_name") String tagName, Pageable pageable);

  /** запрос общего количества постов, отмеченных запрошенным тегом */
  @Query(
      value =
          "SELECT count(*) FROM posts "
              + "LEFT JOIN tag2post "
              + "ON posts.id = tag2post.post_id "
              + "LEFT JOIN tags "
              + "ON tag2post.tag_id = tags.id "
              + "WHERE posts.is_active = 1 "
              + "AND posts.moderation_status = 'ACCEPTED' "
              + "AND posts.time <= now() "
              + "AND tags.name = :tag_name",
      nativeQuery = true)
  Integer countPostsByTag(@Param("tag_name") String tagName);

  /** Запрос для api/search/ */
  List<Post> findAllByTitleContainingAndIsActiveAndModerationStatusAndTimeIsBeforeOrderByTimeDesc(
      String title,
      Byte isActive,
      ModerationStatus moderationStatus,
      LocalDateTime time,
      Pageable pageable);

  /** получение количества постов по поисковому запросу */
  Integer countAllByTitleContainingAndIsActiveAndModerationStatusAndTimeIsBeforeOrderByTimeDesc(
      String title, Byte isActive, ModerationStatus moderationStatus, LocalDateTime time);

  /** получение списка постов за определенный год */
  @Query(
      value =
          "SELECT * FROM posts "
              + "WHERE time LIKE :year "
              + "AND posts.is_active = 1 "
              + "AND posts.moderation_status = 'ACCEPTED' "
              + "AND posts.time <= now()",
      nativeQuery = true)
  List<Post> getPostsByYear(@Param("year") String year);

  /** получение списка годов, в которых были сделаны публикации */
  @Query(
      value =
          "SELECT distinct year(time) FROM posts "
              + "WHERE posts.is_active = 1 "
              + "AND posts.moderation_status = 'ACCEPTED' "
              + "AND posts.time <= now()",
      nativeQuery = true)
  List<String> getAllPostsYears();

  /** получение количества постов за определенную дату */
  @Query(
      value =
          "SELECT count(*) FROM posts "
              + "WHERE time LIKE :date "
              + "AND is_active = 1 "
              + "AND moderation_status = 'ACCEPTED'",
      nativeQuery = true)
  Integer countPostsByDate(@Param("date") String date);

  /** получение постов за определенную дату */
  @Query(
      value =
          "SELECT posts.* FROM posts "
              + "WHERE time LIKE :date "
              + "AND is_active = 1 "
              + "AND moderation_status = 'ACCEPTED'",
      nativeQuery = true)
  List<Post> getPostsByDate(@Param("date") String date, Pageable pageable);

  /** получение активных постов определенного пользователя с определенными статусами модерации */
  @Query(
      value =
          "SELECT * FROM posts "
              + "WHERE posts.user_id = :user_id "
              + "AND posts.is_active = 1 "
              + "AND posts.moderation_status = :moderation_status",
      nativeQuery = true)
  List<Post> getAllMyPosts(
      @Param("user_id") Integer userId,
      @Param("moderation_status") String moderationStatus,
      Pageable pageable);

  /**
   * Получение количества активных постов определенного пользователя с определенными статусами
   * модерации
   */
  @Query(
      value =
          "SELECT count(*) FROM posts "
              + "WHERE user_id = :user_id "
              + "AND is_active = 1 "
              + "AND moderation_status = :moderation_status "
              + "AND time <= now()",
      nativeQuery = true)
  Integer countAllMyPosts(
      @Param("user_id") Integer userId, @Param("moderation_status") String moderationStatus);

  /** получение неактивных постов определенного пользователя */
  @Query(
      value =
          "SELECT * FROM posts " + "WHERE posts.user_id = :user_id " + "AND posts.is_active = 0",
      nativeQuery = true)
  List<Post> getAllMyInactivePosts(@Param("user_id") Integer userId);

  /** получение количества неактивных постов определенного пользователя */
  @Query(
      value =
          "SELECT count(*) FROM posts "
              + "WHERE posts.user_id = :user_id "
              + "AND posts.is_active = 0",
      nativeQuery = true)
  Integer countAllMyInactivePosts(@Param("user_id") Integer userId);

  /** получение количества всех просмотров у всех активных постов */
  @Query(
      value =
          "SELECT sum(view_count) FROM posts "
              + "WHERE is_active = 1 "
              + "AND moderation_status = 'ACCEPTED' "
              + "AND time <= now()",
      nativeQuery = true)
  Integer postsViewsCount();

  /** получение количества всех лайков или дизлайков у всех постов */
  @Query(value = "SELECT count(*) FROM post_votes " + "WHERE value = :value", nativeQuery = true)
  Integer allPostsLikesOrDislikesCount(@Param("value") Integer value);

  /** получение времени публикации первого опубликованного поста */
  @Query(
      value =
          "SELECT time FROM posts "
              + "WHERE is_active = 1 "
              + "AND moderation_status = 'ACCEPTED' "
              + "AND time <= now() "
              + "ORDER BY time ASC LIMIT 1",
      nativeQuery = true)
  LocalDateTime firstPublicationFromAllPosts();

  /** получение количества лайков или дислайков на постах определенного пользователя */
  @Query(
      value =
          "SELECT count(*) FROM post_votes "
              + "LEFT JOIN posts "
              + "ON post_votes.post_id = posts.id "
              + "WHERE posts.user_id = :user_id "
              + "AND posts.is_active = 1 "
              + "AND posts.moderation_status = 'ACCEPTED' "
              + "AND posts.time <= now() "
              + "AND post_votes.value = :value",
      nativeQuery = true)
  Integer myLikesOrDislikesCount(@Param("user_id") Integer userId, @Param("value") Integer value);

  /** получение даты первой публикации определенного пользователя */
  @Query(
      value =
          "SELECT time FROM posts "
              + "WHERE is_active = 1 "
              + "AND user_id = :user_id "
              + "AND moderation_status = 'ACCEPTED' "
              + "AND time <= now() "
              + "ORDER BY time ASC LIMIT 1",
      nativeQuery = true)
  LocalDateTime myFirstPublication(@Param("user_id") Integer userId);

  /** расчет количества просмотров на постах у определенного пользователя */
  @Query(
      value =
          "SELECT sum(view_count) FROM posts "
              + "WHERE is_active = 1 "
              + "AND moderation_status = 'ACCEPTED' "
              + "AND time <= now() "
              + "AND user_id = :user_id",
      nativeQuery = true)
  Integer myPostsViewsCount(@Param("user_id") Integer userId);

  /** поиск постов, которые утвердил или отклонил определенный модератор */
  List<Post> findAllByIsActiveAndModerationStatusAndAndModeratorId(
      Byte active, ModerationStatus moderationStatus, Integer moderatorId, Pageable pageable);

  /** поиск количества постов, которые утвердил или отклонил определенный модератор */
  Integer countAllByModerationStatusAndAndIsActiveAndAndModeratorId(
      ModerationStatus moderationStatus, Byte active, Integer moderatorId);

  /**
   * поиск поста по айди, используется для лайков\дизлайков, а так же при получении поста по айди,
   * если его получает модератор или пользователь, который этот пост опубликовал
   */
  Post getPostById(Integer id);

  /**
   * поиск айди юзера по айди поста, используется для проверки при увеличении количества просмотров
   * поста
   */
  @Query(value = "SELECT user_id FROM posts " + "WHERE id = :id", nativeQuery = true)
  Integer getUserIdByPostId(@Param("id") Integer id);
}
