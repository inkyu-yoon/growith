package com.growith.domain.likes.dto;

import com.growith.domain.likes.PostLike;
import com.growith.domain.post.Post;
import com.growith.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Builder
@Getter
public class LikeResponse {
    private Long postId;
    private Long fromUserId;
    private Boolean isHistoryFound;

    @Builder
    public LikeResponse(Long postId, Long fromUserId, boolean isHistoryFound) {
        this.postId = postId;
        this.fromUserId = fromUserId;
        this.isHistoryFound = isHistoryFound;
    }
    public LikeResponse(LikeResponse likeResponse) {
        this.postId = likeResponse.getPostId();
        this.fromUserId = likeResponse.getFromUserId();
        this.isHistoryFound = likeResponse.getIsHistoryFound();
    }

    public static LikeResponse of(Post post, User user, Optional<PostLike> postLikeOptional) {
        if (postLikeOptional.isEmpty()) {
            return LikeResponse.builder()
                    .postId(post.getId())
                    .fromUserId(user.getId())
                    .isHistoryFound(false)
                    .build();
        }else{
            return LikeResponse.builder()
                    .postId(post.getId())
                    .fromUserId(user.getId())
                    .isHistoryFound(true)
                    .build();
        }
        
    }
}
