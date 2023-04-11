package com.growith.domain.comment;

import com.growith.domain.comment.dto.CommentGetResponse;
import com.growith.domain.post.Category;
import com.growith.domain.post.Post;
import com.growith.domain.post.dto.PostGetListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentCustomRepository {

    Page<CommentGetResponse> getComments(Post post, Pageable pageable);
}
