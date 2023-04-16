package com.growith.domain.post;

import com.growith.domain.BaseEntity;
import com.growith.domain.comment.Comment;
import com.growith.domain.likes.PostLike;
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
@SQLDelete(sql = "UPDATE post SET deleted_date = current_timestamp WHERE id = ?")
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

    private Long view;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostLike> likes = new ArrayList<>();

    @Builder
    public Post(String title, String content, Category category, User user, Long view) {
        Assert.hasText(title, "title must not be empty");
        Assert.hasText(content, "content must not be empty");
        Assert.notNull(category, "category must not be empty");
        Assert.notNull(user, "user must not be empty");

        this.title = title;
        this.content = content;
        this.category = category;
        this.user = user;
        this.view = view;
    }

    public PostGetResponse toPostGetResponse() {
        return PostGetResponse.builder()
                .userName(this.user.getUsername())
                .postId(this.id)
                .title(this.title)
                .imageUrl(this.user.getImageUrl())
                .content(this.content)
                .category(this.category)
                .userId(this.user.getId())
                .nickName(this.user.getNickName())
                .createdDate(new SimpleDateFormat("yyyy/MM/dd HH:mm").format(Timestamp.valueOf(this.getCreatedDate())))
                .lastModifiedDate(new SimpleDateFormat("yyyy/MM/dd HH:mm").format(Timestamp.valueOf(this.getLastModifiedDate())))
                .view(this.view)
                .totalNumOfComments(this.getComments().size())
                .totalNumOfLikes(this.likes.size())
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

    public void increaseView() {
        this.view += 1;
    }
}
