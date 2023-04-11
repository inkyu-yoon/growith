package com.growith.domain.comment.dto;

import com.growith.domain.comment.Comment;
import com.growith.domain.user.User;
import com.growith.global.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class CommentGetResponse {
    private Long commentId;
    private String comment;
    private String userName;
    private String createdDate;
    private String imageUrl;

    public CommentGetResponse(Comment comment, User user) {
        this.commentId = comment.getId();
        this.comment = comment.getComment();
        this.userName = user.getNickName();
        this.imageUrl = user.getImageUrl();
        this.createdDate = TimeUtil.convertLocaldatetimeToTime(comment.getCreatedDate());
    }
}
