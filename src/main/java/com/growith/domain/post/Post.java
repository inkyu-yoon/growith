package com.growith.domain.post;

import com.growith.domain.BaseEntity;
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

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_date is NULL")
@SQLDelete(sql = "UPDATE POST SET deleted_date = current_timestamp WHERE id = ?")
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
                .imageUrl(this.user.getImageUrl())
                .content(this.content)
                .category(this.category)
                .userId(this.user.getId())
                .nickName(this.user.getNickName())
                .createdAt(new SimpleDateFormat("yyyy/MM/dd HH:mm").format(Timestamp.valueOf(this.getCreatedDate())))
                .lastModifiedAt(new SimpleDateFormat("yyyy/MM/dd HH:mm").format(Timestamp.valueOf(this.getLastModifiedDate())))
                .build();
    }

    public PostResponse toPostResponse() {
        return new PostResponse(this.id);
    }

    public void update(PostUpdateRequest requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.category = requestDto.getCategory();
    }
}
