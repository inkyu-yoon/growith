package com.growith.domain.comment.dto;

import com.growith.domain.comment.Comment;
import com.growith.domain.post.Post;
import com.growith.domain.user.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreateRequest {
    @NotBlank(message = "댓글 내용은 필수 입력 항목입니다.")
    @Length(max = 245,message = "댓글은 245자를 넘을 수 없습니다.")
    private String comment;

    public Comment toEntity(User user, Post post) {
        return Comment.builder()
                .comment(this.comment)
                .user(user)
                .post(post)
                .build();
    }

    public Comment toEntity(User user, Post post, Comment comment) {
        return Comment.builder()
                .comment(this.comment)
                .user(user)
                .post(post)
                .parent(comment)
                .build();
    }
}
