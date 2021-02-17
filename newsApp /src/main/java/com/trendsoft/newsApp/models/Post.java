package com.trendsoft.newsApp.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trendsoft.newsApp.models.Enums.ModerationStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(exclude = "tagList", callSuper = false)
@ToString(exclude = "tagList")
@Table(name = "posts")
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "is_active", nullable = false)
  private Byte isActive;

  @Enumerated(EnumType.STRING)
  @Column(name = "moderation_status")
  private ModerationStatus moderationStatus;

  @Column(name = "moderator_id")
  private Integer moderatorId;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User userId;

  @Column(nullable = false)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
  private LocalDateTime time;

  @Column(nullable = false)
  private String title;

  @Column(length = 1000, nullable = false)
  private String text;

  @Column(name = "view_count", nullable = false)
  private Integer viewCount;

  @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL)
  private List<PostComment> postCommentList;

  @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinTable(
      name = "tag2post",
      joinColumns = {@JoinColumn(name = "post_id")},
      inverseJoinColumns = {@JoinColumn(name = "tag_id")})
  private List<Tag> tagList = new ArrayList<>();

  @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL)
  private List<PostVote> postVoteList;
}
