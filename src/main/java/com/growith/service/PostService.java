package com.growith.service;

import com.growith.domain.post.Category;
import com.growith.domain.post.Post;
import com.growith.domain.post.PostRepository;
import com.growith.domain.post.dto.*;
import com.growith.domain.user.User;
import com.growith.domain.user.UserRepository;
import com.growith.global.exception.AppException;
import com.growith.global.util.CookieUtil;
import com.growith.global.util.TextParsingUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.growith.global.exception.ErrorCode.POST_NOT_FOUND;
import static com.growith.global.exception.ErrorCode.USER_NOT_FOUND;
import static com.growith.global.util.constant.CookieConstants.VIEW_HISTORY_COOKIE_AGE;
import static com.growith.global.util.constant.CookieConstants.VIEW_HISTORY_COOKIE_NAME;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<PostGetResponse> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(post -> post.toPostGetResponse());
    }

    @Transactional(readOnly = true)
    public Page<PostGetListResponse> getAllPostsByUserName(String userName, Pageable pageable) {
        return postRepository.getPostsListByUserName(userName, pageable);
    }

    public PostResponse createPost(String userName, PostCreateRequest postCreateRequest) {
        User foundUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(USER_NOT_FOUND));

        Post savedPost = postRepository.save(postCreateRequest.toEntity(foundUser));

        return savedPost.toPostResponse();
    }

    @Transactional(readOnly = true)
    public Page<PostGetListResponse> getAllPostsByCategory(Category category, Pageable pageable) {
        return postRepository.getPostsListByCategory(category, pageable);
    }

    @Transactional(readOnly = true)
    public Page<PostGetListResponse> getPostsBySearchAndCategory(String searchCondition, String userName, Category category, Pageable pageable) {
        return postRepository.getPostsListBySearchAndCategory(searchCondition, userName, category, pageable);
    }


    @Transactional(readOnly = true)
    public PostGetResponse getPost(Long postId) {
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(POST_NOT_FOUND));

        return foundPost.toPostGetResponse();
    }

    @Transactional(readOnly = true)
    public List<PostGetListResponse> getBestPosts() {
        return postRepository.getBestPosts();
    }

    public void increaseView(Long postId, HttpServletRequest req, HttpServletResponse res) {
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(POST_NOT_FOUND));

        /**
         * 쿠키에서 번호와 일치하는 값이 나오면 증가 안시킴
         * 번호와 일치하는 값이 없을 경우 조회수 증가 후 쿠키 추가
         */
        String viewHistory = CookieUtil.getCookie(req, VIEW_HISTORY_COOKIE_NAME);

        // 쿠키가 존재하나, 해당 postId 기록이 없는 경우 조회수 증가
        if (StringUtils.hasText(viewHistory)) {
            if (!TextParsingUtil.parsingViewHistory(viewHistory).contains(String.valueOf(postId))) {
                foundPost.increaseView();
                CookieUtil.setCookie(res, VIEW_HISTORY_COOKIE_NAME, String.format("%s%d_", viewHistory, postId), VIEW_HISTORY_COOKIE_AGE);
            }
            // 쿠키 자체가 존재하지 않는 경우 조회수 증가(어떠한 게시글도 읽지 않았다는 의미이므로)
        } else {
            foundPost.increaseView();
            CookieUtil.setCookie(res, VIEW_HISTORY_COOKIE_NAME, String.format("%d_", postId), VIEW_HISTORY_COOKIE_AGE);
        }

    }

    public PostResponse deletePost(Long postId, String userName) {
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(POST_NOT_FOUND));

        User requestUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(USER_NOT_FOUND));

        requestUser.checkAuth(foundPost.getUser().getUsername());

        postRepository.delete(foundPost);

        return foundPost.toPostResponse();
    }

    public PostResponse updatePost(Long postId, String userName, PostUpdateRequest requestDto) {
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(POST_NOT_FOUND));

        User requestUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(USER_NOT_FOUND));

        requestUser.checkAuth(foundPost.getUser().getUsername());

        foundPost.update(requestDto);

        return foundPost.toPostResponse();
    }

}
