package com.growith.domain.post;

import com.growith.domain.BaseEntity;
import com.growith.domain.post.dto.PostGetResponse;
import com.growith.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.util.Assert;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_date is NULL")
@SQLDelete(sql = "UPDATE POST SET deleted_at = current_timestamp WHERE post_id = ?")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Enumerated(value = EnumType.STRING)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Post(String title, String content, Category category, User user) {
        Assert.hasText(title, "title must not be empty");
        Assert.hasText(content, "content must not be empty");
        Assert.notNull(category, "category must not be empty");
        Assert.notNull(user, "user must not be empty");

        this.title = title;
        this.content = content;
        this.category = category;
        this.user = user;
    }

    public PostGetResponse toPostGetResponse() {
        return PostGetResponse.builder()
                .postId(this.id)
                .title(this.title)
                .content(this.content)
                .category(this.category)
                .userId(this.user.getId())
                .nickName(this.user.getNickName())
                .build();
    }

}
