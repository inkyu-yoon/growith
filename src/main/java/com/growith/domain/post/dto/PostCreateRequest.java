package com.growith.domain.post.dto;

import com.growith.domain.post.Category;
import com.growith.domain.post.Post;
import com.growith.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateRequest {

    private String title;
    private String content;
    private Category category;

    public Post toEntity(User user) {
        return Post.builder()
                .title(this.title)
                .content(this.content)
                .category(this.category)
                .user(user)
                .build();
    }
}
