package com.growith.service.post;

import com.growith.domain.post.Category;
import com.growith.domain.post.Post;
import com.growith.domain.post.PostRepository;
import com.growith.domain.post.dto.*;
import com.growith.domain.user.User;
import com.growith.domain.user.UserRepository;
import com.growith.global.exception.AppException;
import com.growith.global.exception.ErrorCode;
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
    public PostResponse createPost(String email, PostCreateRequest postCreateRequest) {
        User foundUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(USER_NOT_FOUND));

        Post savedPost = postRepository.save(postCreateRequest.toEntity(foundUser));

        return savedPost.toPostResponse();
    }

    public Page<PostGetListResponse> getAllPostsByCategory(Category category,Pageable pageable) {
        return postRepository.getPostsListByCategory(category, pageable);
    }

    public PostGetResponse getPost(Long postId) {
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(POST_NOT_FOUND));

        return foundPost.toPostGetResponse();
    }
    @Transactional
    public PostResponse deletePost(Long postId, String email) {
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(POST_NOT_FOUND));

        User requestUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(USER_NOT_FOUND));

        requestUser.checkAuth(foundPost.getUser().getEmail());

        postRepository.delete(foundPost);

        return foundPost.toPostResponse();
    }

    @Transactional
    public PostResponse updatePost(Long postId, String email, PostUpdateRequest requestDto) {
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(POST_NOT_FOUND));

        User requestUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(USER_NOT_FOUND));

        requestUser.checkAuth(foundPost.getUser().getEmail());

        foundPost.update(requestDto);

        return foundPost.toPostResponse();
    }
}
