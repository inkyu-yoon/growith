package com.growith.domain.comment;

import com.growith.domain.comment.dto.CommentGetResponse;
import com.growith.domain.post.Post;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentCustomRepository {

    List<CommentGetResponse> getComments(Post post);
}
