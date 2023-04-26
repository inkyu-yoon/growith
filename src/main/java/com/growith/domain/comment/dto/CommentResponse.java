package com.growith.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class CommentResponse {
    private Long commentId;
    private String comment;
    private Long postId;
    private Long fromUserId;

    public CommentResponse(CommentResponse commentResponse) {
        this.commentId = commentResponse.getCommentId();
        this.comment = commentResponse.getComment();
        this.postId = commentResponse.getPostId();
        this.fromUserId = commentResponse.getFromUserId();
    }
}
