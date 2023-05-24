package com.growith.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class CommentReplyResponse {
    private Long commentId;
    private Long fromUserId;
    private Long toUserId;


    public CommentReplyResponse(CommentReplyResponse commentReplyResponse) {
        this.commentId = commentReplyResponse.getCommentId();
        this.fromUserId = commentReplyResponse.getFromUserId();
        this.toUserId = commentReplyResponse.getToUserId();
    }
}
