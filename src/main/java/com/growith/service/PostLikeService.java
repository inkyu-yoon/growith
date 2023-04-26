package com.growith.service;

import com.growith.domain.likes.PostLike;
import com.growith.domain.likes.PostLikeRepository;
import com.growith.domain.likes.dto.LikeResponse;
import com.growith.domain.post.Post;
import com.growith.domain.post.PostRepository;
import com.growith.domain.user.User;
import com.growith.domain.user.UserRepository;
import com.growith.global.annotation.CreateAlarm;
import com.growith.global.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.growith.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class PostLikeService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    @CreateAlarm(classInfo = LikeResponse.class)
    public LikeResponse addLike(String userName, Long postId) {
        User foundUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(USER_NOT_FOUND));

        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(POST_NOT_FOUND));

        foundPost.checkUserForLike(foundUser);

        Optional<PostLike> foundPostLike = postLikeRepository.findByUserAndPost(foundUser, foundPost);

        foundPostLike
                .ifPresentOrElse(
                        postLike -> {
                            postLikeRepository.delete(postLike);
                            },

                        () -> postLikeRepository.save(PostLike.of(foundUser, foundPost))
                );

        return LikeResponse.of(foundPost, foundUser, foundPostLike);
    }
}
