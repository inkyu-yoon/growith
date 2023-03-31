package com.growith.domain.post.dto;

import com.growith.domain.post.Category;
import com.growith.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PostGetResponse {

    private Long postId;
    private String title;
    private String content;
    private Category category;
    private Long userId;
    private String nickName;
}
