package com.growith.domain.comment;

import com.growith.domain.BaseEntity;
import com.growith.domain.comment.dto.CommentResponse;
import com.growith.domain.post.Category;
import com.growith.domain.post.Post;
import com.growith.domain.post.dto.PostGetResponse;
import com.growith.domain.post.dto.PostResponse;
import com.growith.domain.post.dto.PostUpdateRequest;
import com.growith.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_date is NULL")
@SQLDelete(sql = "UPDATE comment SET deleted_date = current_timestamp WHERE id = ?")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    @Builder
    public Comment(String comment, User user, Post post) {
        Assert.hasText(comment, "comment must not be empty");
        Assert.notNull(user, "user must not be empty");
        Assert.notNull(post, "post must not be empty");

        this.comment = comment;
        this.user = user;
        this.post = post;
    }

    public CommentResponse toCommentResponse() {
        return CommentResponse.builder()
                .commentId(this.id)
                .comment(this.comment)
                .build();
    }

}
