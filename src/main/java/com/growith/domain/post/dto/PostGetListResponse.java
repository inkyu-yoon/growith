package com.growith.domain.post.dto;

import com.growith.domain.post.Post;
import com.growith.domain.user.User;
import com.growith.global.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PostGetListResponse {
    private Long postId;
    private String title;
    private String content;
    private String date;
    private String nickName;
    private Long view;

    public PostGetListResponse(Post post, User user) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.date = TimeUtil.convertLocaldatetimeToTime(post.getCreatedDate());
        this.nickName = user.getNickName();
        this.view = post.getView();
    }
}
