package com.trendsoft.newsApp.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "post_comments")
public class PostComment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "parent_id")
  private Integer parentId;

  @ManyToOne
  @JoinColumn(name = "post_id", nullable = false)
  private Post postId;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User userId;

  @Column(nullable = false)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
  private LocalDateTime time;

  @Column(nullable = false)
  private String text;
}
