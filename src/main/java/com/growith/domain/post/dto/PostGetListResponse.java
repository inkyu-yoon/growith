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

    private String title;
    private String subContent;
    private String date;
    private String nickName;

    public PostGetListResponse(Post post, User user) {
        this.title = post.getTitle();

        this.subContent = post.getContent();

        this.date = TimeUtil.convertLocaldatetimeToTime(post.getCreatedDate());
        this.nickName = user.getNickName();
    }
}
