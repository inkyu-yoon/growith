package com.growith.domain.likes;

import com.growith.domain.BaseEntity;
import com.growith.domain.likes.dto.LikeResponse;
import com.growith.domain.post.Post;
import com.growith.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_date is NULL")
@SQLDelete(sql = "UPDATE post_like SET deleted_date = current_timestamp WHERE id = ?")
public class PostLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public PostLike(User user, Post post) {
        this.user = user;
        this.post = post;
        this.post.getLikes().add(this);
    }

    public static PostLike of(User user, Post post) {
        return new PostLike(user, post);
    }

}
