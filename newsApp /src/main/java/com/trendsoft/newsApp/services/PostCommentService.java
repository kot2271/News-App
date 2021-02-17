package com.trendsoft.newsApp.services;

import com.trendsoft.newsApp.dto.requestDto.AddCommentToPostDto;
import com.trendsoft.newsApp.models.Post;
import com.trendsoft.newsApp.models.PostComment;
import com.trendsoft.newsApp.models.User;
import com.trendsoft.newsApp.repositories.PostCommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class PostCommentService {
  private  PostCommentRepository postCommentRepository;

  /** Добавление нового комментария к посту */
  public Integer addNewCommentToPost(
      AddCommentToPostDto addCommentToPostDto, Post post, User user) {
    PostComment postComment = new PostComment();
    postComment.setPostId(post);
    postComment.setParentId(addCommentToPostDto.getParentId());
    postComment.setText(addCommentToPostDto.getText());
    postComment.setTime(LocalDateTime.now());
    postComment.setUserId(user);
    postCommentRepository.save(postComment);
    return postComment.getId();
  }

  /** Получение комментария по айди поста и айди комментария-родителя */
  @Transactional
  public PostComment getPostCommentByParentIdAndPostId(Integer parentId, Integer postId) {
    return postCommentRepository.findByIdAndPostId(parentId, postId);
  }
}
