package com.trendsoft.newsApp.services;

import com.trendsoft.newsApp.dto.requestDto.AddPostDto;
import com.trendsoft.newsApp.dto.requestDto.ModerationPostDto;
import com.trendsoft.newsApp.dto.responseDto.*;
import com.trendsoft.newsApp.exceptions.BadRequestException;
import com.trendsoft.newsApp.models.Enums.ModerationStatus;
import com.trendsoft.newsApp.models.Post;
import com.trendsoft.newsApp.models.PostComment;
import com.trendsoft.newsApp.models.Tag;
import com.trendsoft.newsApp.models.User;
import com.trendsoft.newsApp.repositories.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class PostService {

  private PostRepository postRepository;
  /** количество символов для анонса текста поста */
  private final int numberForAnnounce = 15;

  /** получение постов по заданному параметру */
  @Transactional
  public List<PostDto> getAllPosts(Integer offset, Integer limit, String mode) {

    Pageable pageable = PageRequest.of(offset / limit, limit);
    switch (mode) {
      case "recent":
        return getPostsDtos(
            postRepository.findAllByModerationStatusAndIsActiveOrderByTimeDesc(
                ModerationStatus.ACCEPTED, (byte) 1, pageable));
      case "early":
        return getPostsDtos(
            postRepository.findAllByModerationStatusAndIsActiveOrderByTimeAsc(
                ModerationStatus.ACCEPTED, (byte) 1, pageable));
      case "popular":
        return getPostsDtos(postRepository.mostPopularPosts(pageable));
      case "best":
        return getPostsDtos(postRepository.bestPosts(pageable));
      default:
        return null;
    }
  }

  /** получение общего количества постов */
  public Integer getCountAllPosts() {
    return postRepository.countAllPosts("ACCEPTED");
  }

  /** получения поста по ID */
  @Transactional
  public OnePostDto getPostById(Integer id, User user) {
    if (user != null) {
      Integer userIdByPostId = postRepository.getUserIdByPostId(id);
      if (user.getIsModerator() == 1 || userIdByPostId.equals(user.getId())) {
        Post post = postRepository.getPostById(id);
        if (post == null) throw new BadRequestException("Запрашиваемый пост несуществует");
        return getPostByIdDto(post);
      }
    }
    Post post =
        postRepository.getByIdAndModerationStatusAndIsActive(
            id, ModerationStatus.ACCEPTED, (byte) 1);
    if (post == null) throw new BadRequestException("Данный пост недоступен для вас");
    incrementPostViewCount(post);

    return getPostByIdDto(post);
  }

  /** получение постов, содержащих заданный тег */
  @Transactional
  public List<PostDto> getPostsByTag(String tagName, Integer offset, Integer limit) {
    Pageable pageable = PageRequest.of(offset / limit, limit);
    return getPostsDtos(postRepository.getPostsByTag(tagName, pageable));
  }

  /** получение количества постов, содержащих заданный тег */
  public Integer getCountPostsByTag(String tagName) {
    return postRepository.countPostsByTag(tagName);
  }

  /** получение постов по поисковому запросу */
  @Transactional
  public List<PostDto> getPostsBySearchQuery(String query, Integer limit, Integer offset) {
    LocalDateTime time = LocalDateTime.now();
    Pageable pageable = PageRequest.of(offset / limit, limit);
    return getPostsDtos(
        postRepository
            .findAllByTitleContainingAndIsActiveAndModerationStatusAndTimeIsBeforeOrderByTimeDesc(
                query, (byte) 1, ModerationStatus.ACCEPTED, time, pageable));
  }

  /** получение количества постов по поисковому запросу */
  public Integer getCountPostsBySearchQuery(String query) {
    LocalDateTime time = LocalDateTime.now();
    return postRepository
        .countAllByTitleContainingAndIsActiveAndModerationStatusAndTimeIsBeforeOrderByTimeDesc(
            query, (byte) 1, ModerationStatus.ACCEPTED, time);
  }

  /** получение постов для календаря */
  public CalendarDto getPostsToCalendar(String year) {
    List<String> years = postRepository.getAllPostsYears();
    List<Post> postList = postRepository.getPostsByYear(year + "%");
    Map<String, Integer> postMap = new TreeMap<>();

    postList.forEach(
        post -> {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
          String postDate = post.getTime().format(formatter);
          Integer postCount = getCountPostsByDate(postDate);
          postMap.put(postDate, postCount);
        });
    return new CalendarDto(years, postMap);
  }

  /** получение постов за определенную дату (используется в календаре) */
  @Transactional
  public List<PostDto> getPostsByDate(String date, Integer offset, Integer limit) {
    Pageable pageable = PageRequest.of(offset / limit, limit);
    return getPostsDtos(postRepository.getPostsByDate(date + "%", pageable));
  }

  /** получение количества постов за определенную дату */
  public Integer getCountPostsByDate(String date) {
    return postRepository.countPostsByDate(date + "%");
  }

  /** получение количества постов на модерацию, т.е. "новых" постов */
  public Integer getCountPostsToModeration() {
    return postRepository.countAllPosts("NEW");
  }

  /** Получение всех постов пользователя в зависимости от активности и статуса модерации */
  @Transactional
  public List<PostDto> getMyPosts(Integer offset, Integer limit, Integer userId, String status) {
    Pageable pageable = PageRequest.of(offset / limit, limit);
    switch (status) {
      case "inactive":
        return getPostsDtos(postRepository.getAllMyInactivePosts(userId));
      case "pending":
        return getPostsDtos(postRepository.getAllMyPosts(userId, "NEW", pageable));
      case "declined":
        return getPostsDtos(postRepository.getAllMyPosts(userId, "DECLINED", pageable));
      case "published":
        return getPostsDtos(postRepository.getAllMyPosts(userId, "ACCEPTED", pageable));
      default:
        return null;
    }
  }

  /**
   * расчет количества всех постов определенного пользователя в зависимости от активности и статуса
   * модерации
   */
  public Integer getCountMyPosts(Integer userId, String status) {
    switch (status) {
      case "inactive":
        return postRepository.countAllMyInactivePosts(userId);
      case "pending":
        return postRepository.countAllMyPosts(userId, "NEW");
      case "declined":
        return postRepository.countAllMyPosts(userId, "DECLINED");
      case "published":
        return postRepository.countAllMyPosts(userId, "ACCEPTED");
      default:
        return null;
    }
  }

  /** добавление нового поста */
  public Integer addNewPost(AddPostDto addPostDto, User user, boolean preModeration) {
    Post post = new Post();
    post.setIsActive(addPostDto.getActive());
    post.setText(addPostDto.getText());
    post.setTitle(addPostDto.getTitle());
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    LocalDateTime dateTime = LocalDateTime.parse(addPostDto.getTime(), formatter);
    post.setTime(dateTime);
    post.setUserId(user);
    if (user.getIsModerator() == 1) {
      post.setModerationStatus(ModerationStatus.ACCEPTED);
      post.setModeratorId(user.getId());
    } else if (preModeration) {
      post.setModerationStatus(ModerationStatus.ACCEPTED);
    } else {
      post.setModerationStatus(ModerationStatus.NEW);
    }
    post.setViewCount(0);
    postRepository.save(post);
    return post.getId();
  }

  /** изменение поста по айди */
  public void editPostById(AddPostDto addPostDto, Post post, User user, boolean preModeration) {
    post.setIsActive(addPostDto.getActive());
    post.setText(addPostDto.getText());
    post.setTitle(addPostDto.getTitle());
    String postTime = addPostDto.getTime();
    if (postTime.equals("NaN-NaN-NaN NaN:NaN")) {
      throw new BadRequestException("Вы не указали время публикации поста");
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    LocalDateTime dateTime = LocalDateTime.parse(postTime, formatter);
    post.setTime(dateTime);
    if (user.getIsModerator() == 1) {
      post.setModerationStatus(ModerationStatus.ACCEPTED);
      post.setModeratorId(user.getId());
    } else if (preModeration) {
      post.setModerationStatus(ModerationStatus.ACCEPTED);
    } else {
      post.setModerationStatus(ModerationStatus.NEW);
    }
    postRepository.save(post);
  }

  /** получение количества просмотров на всех постах */
  public Integer getPostsViewsCount() {
    return postRepository.postsViewsCount();
  }

  /** получение количества лайков или дизлайков на всех постах */
  public Integer getAllPostsLikesOrDislikesCount(Integer likeOrDislike) {
    return postRepository.allPostsLikesOrDislikesCount(likeOrDislike);
  }

  /** получение времени публикации первого опубликованного поста */
  public LocalDateTime getFirstPublicationFromAllPosts() {
    return postRepository.firstPublicationFromAllPosts();
  }

  /** получение количества постов определенного пользователя */
  public Integer getCountUserPosts(Integer userId) {
    return postRepository.countAllMyPosts(userId, "ACCEPTED");
  }

  /** получение количества лайков или дизлайков определенного пользователя */
  public Integer getMyLikesOrDislikesCount(Integer userId, Integer likeOrDislike) {
    return postRepository.myLikesOrDislikesCount(userId, likeOrDislike);
  }

  /** получение даты первой публикации определенного пользователя */
  public LocalDateTime getMyFirstPublication(Integer userId) {
    return postRepository.myFirstPublication(userId);
  }

  /** получение количества просмотров на постах определенного пользователя */
  public Integer getMyPostsViewsCount(Integer userId) {
    return postRepository.myPostsViewsCount(userId);
  }

  /**
   * получение постов для модерации, или постов, которые утвердил или отклонил определенный
   * модератор
   */
  @Transactional
  public PostListDto getPostsToModeration(
      Integer offset, Integer limit, String status, Integer moderatorId) {
    Pageable pageable = PageRequest.of(offset / limit, limit);
    List<PostDto> postDtoList = new ArrayList<>();
    Integer count;
    switch (status) {
      case "new":
        postDtoList.addAll(
            getPostsDtos(
                postRepository.findAllByModerationStatusAndIsActiveOrderByTimeDesc(
                    ModerationStatus.NEW, (byte) 1, pageable)));
        count = getCountPostsToModeration();
        return new PostListDto(count, postDtoList, offset, limit, status);
      case "accepted":
        postDtoList.addAll(
            getPostsDtos(
                postRepository.findAllByIsActiveAndModerationStatusAndAndModeratorId(
                    (byte) 1, ModerationStatus.ACCEPTED, moderatorId, pageable)));
        count =
            postRepository.countAllByModerationStatusAndAndIsActiveAndAndModeratorId(
                ModerationStatus.ACCEPTED, (byte) 1, moderatorId);
        return new PostListDto(count, postDtoList, offset, limit, status);
      case "declined":
        postDtoList.addAll(
            getPostsDtos(
                postRepository.findAllByIsActiveAndModerationStatusAndAndModeratorId(
                    (byte) 1, ModerationStatus.DECLINED, moderatorId, pageable)));
        count =
            postRepository.countAllByModerationStatusAndAndIsActiveAndAndModeratorId(
                ModerationStatus.DECLINED, (byte) 1, moderatorId);
        return new PostListDto(count, postDtoList, offset, limit, status);

      default:
        return null;
    }
  }

  /** модерация поста */
  public void moderationPost(ModerationPostDto moderationPostDto, Integer moderatorId) {
    Post post =
        postRepository.getByIdAndModerationStatusAndIsActive(
            moderationPostDto.getPostId(), ModerationStatus.NEW, (byte) 1);
    switch (moderationPostDto.getDecision()) {
      case "accept":
        post.setModerationStatus(ModerationStatus.ACCEPTED);
        post.setModeratorId(moderatorId);
        postRepository.save(post);
        break;
      case "decline":
        post.setModerationStatus(ModerationStatus.DECLINED);
        post.setModeratorId(moderatorId);
        postRepository.save(post);
        break;
    }
  }

  /** получение поста только по айди. используется для лайков\дизлайков и комментариев */
  public Post getPostFromPostId(Integer postId) {
    return postRepository.getPostById(postId);
  }

  /**
   * -------------------------------------------------------- приватные методы для различных
   * превращений и не только..
   */
  private List<PostDto> getPostsDtos(List<Post> postList) {
    return postList.stream()
        .map(
            post ->
                new PostDto(
                    post.getId(),
                    post.getTime(),
                    new UserToPostDto(post.getUserId().getId(), post.getUserId().getName()),
                    post.getTitle(),
                    post.getText().substring(0, numberForAnnounce),
                    (int)
                        (post.getPostVoteList().stream()
                            .filter(postVote -> postVote.getValue() == 1)
                            .count()),
                    (int)
                        (post.getPostVoteList().stream()
                            .filter(postVote -> postVote.getValue() == -1)
                            .count()),
                    post.getPostCommentList().size(),
                    post.getViewCount()))
        .collect(toList());
  }

  private List<String> getPostTagList(Post post) {
    return post.getTagList().stream().map(Tag::getName).collect(toList());
  }

  private List<CommentToPostDto> getCommentToPostDto(List<PostComment> postCommentList) {
    return postCommentList.stream()
        .map(
            postComment ->
                new CommentToPostDto(
                    postComment.getId(),
                    postComment.getTime(),
                    postComment.getText(),
                    new UserToPostCommentDto(
                        postComment.getUserId().getId(),
                        postComment.getUserId().getName(),
                        postComment.getUserId().getPhoto())))
        .collect(toList());
  }

  private OnePostDto getPostByIdDto(Post post) {
    List<CommentToPostDto> commentToPostDtoList = getCommentToPostDto(post.getPostCommentList());
    List<String> tagList = getPostTagList(post);

    return new OnePostDto(
        post.getId(),
        post.getTime(),
        new UserToPostDto(post.getUserId().getId(), post.getUserId().getName()),
        post.getTitle(),
        post.getText().substring(0, numberForAnnounce),
        (int)
            (post.getPostVoteList().stream().filter(postVote -> postVote.getValue() == 1).count()),
        (int)
            (post.getPostVoteList().stream().filter(postVote -> postVote.getValue() == -1).count()),
        post.getPostCommentList().size(),
        post.getViewCount(),
        post.getText(),
        commentToPostDtoList,
        tagList);
  }

  /** увеличение количества просмотров у поста */
  private void incrementPostViewCount(Post post) {
    post.setViewCount(post.getViewCount() + 1);
    postRepository.save(post);
  }
}
