package com.growith.service.post;

import com.growith.domain.post.Category;
import com.growith.domain.post.Post;
import com.growith.domain.post.PostRepository;
import com.growith.domain.post.dto.PostCreateRequest;
import com.growith.domain.post.dto.PostGetListResponse;
import com.growith.domain.post.dto.PostGetResponse;
import com.growith.domain.user.User;
import com.growith.domain.user.UserRepository;
import com.growith.global.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.growith.global.exception.ErrorCode.POST_NOT_FOUND;
import static com.growith.global.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Page<PostGetResponse> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(post -> post.toPostGetResponse());
    }

    @Transactional
    public void createPost(String email, PostCreateRequest postCreateRequest) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(USER_NOT_FOUND));

        postRepository.save(postCreateRequest.toEntity(foundUser));
    }

    public Page<PostGetListResponse> getAllPostsByCategory(Category category,Pageable pageable) {
        return postRepository.getPostsListByCategory(category, pageable);
    }

    public PostGetResponse getPost(Long postId) {
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(POST_NOT_FOUND));

        return foundPost.toPostGetResponse();
    }
}
