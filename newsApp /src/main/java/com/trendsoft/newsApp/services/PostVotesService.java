package com.trendsoft.newsApp.services;

import com.trendsoft.newsApp.models.Post;
import com.trendsoft.newsApp.models.PostVote;
import com.trendsoft.newsApp.repositories.PostVotesRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostVotesService {
  private PostVotesRepository postVotesRepository;

  /**
   * добавление лайка или дизлайка к посту, возвращает false, если лайк или дизлайк уже стоит, а
   * пользователь пытается поставить во второй раз
   */
  public boolean takeLikeOrDislikeToPost(Post post, Integer userId, int likeOrDis) {
    Optional<PostVote> postVoteOptional =
        postVotesRepository.findByPostIdAndUserId(post.getId(), userId);

    if (postVoteOptional.isEmpty()) {
      saveLikeOrDislike(post, userId, likeOrDis);
      return true;
    }
    if (postVoteOptional.get().getValue() == likeOrDis) {
      return false;
    }
    PostVote postVote = postVoteOptional.get();
    postVote.setValue((byte) likeOrDis);
    postVote.setTime(LocalDateTime.now());
    postVotesRepository.save(postVote);
    return true;
  }

  /** сохранение лайка или дизлайка */
  private void saveLikeOrDislike(Post post, Integer userId, int likeOrDis) {
    PostVote postVote = new PostVote();
    postVote.setTime(LocalDateTime.now());
    postVote.setValue((byte) likeOrDis);
    postVote.setPostId(post);
    postVote.setUserId(userId);
    postVotesRepository.save(postVote);
  }
}
