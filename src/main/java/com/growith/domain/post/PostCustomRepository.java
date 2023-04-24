package com.growith.domain.post;

import com.growith.domain.post.dto.PostGetListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCustomRepository {

    Page<PostGetListResponse> getPostsListByCategory(Category category, Pageable pageable);
    Page<PostGetListResponse> getPostsListByUserName(String userName, Pageable pageable);

    List<PostGetListResponse> getBestPosts();
}
