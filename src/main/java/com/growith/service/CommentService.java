package com.growith.service;

import com.growith.domain.comment.Comment;
import com.growith.domain.comment.CommentRepository;
import com.growith.domain.comment.dto.CommentCreateRequest;
import com.growith.domain.comment.dto.CommentGetResponse;
import com.growith.domain.comment.dto.CommentResponse;
import com.growith.domain.comment.dto.CommentUpdateRequest;
import com.growith.domain.post.Post;
import com.growith.domain.post.PostRepository;
import com.growith.domain.user.User;
import com.growith.domain.user.UserRepository;
import com.growith.global.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.growith.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public CommentResponse createComment(Long postId, String userName, CommentCreateRequest requestDto) {
        User foundUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(USER_NOT_FOUND));

        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(POST_NOT_FOUND));

        Comment comment = commentRepository.save(requestDto.toEntity(foundUser, foundPost));

        return comment.toCommentResponse();
    }
    @Transactional(readOnly = true)
    public List<CommentGetResponse> getAllComments(Long postId) {
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(POST_NOT_FOUND));

        return commentRepository.getComments(foundPost);
    }

    public CommentResponse deleteComment(Long postId, String userName, Long commentId ) {
        User foundUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(USER_NOT_FOUND));

        postRepository.findById(postId)
                .orElseThrow(() -> new AppException(POST_NOT_FOUND));

        Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(COMMENT_NOT_FOUND));

        foundUser.checkAuth(foundComment.getUser().getUsername());

        commentRepository.delete(foundComment);

        return foundComment.toCommentResponse();
    }

    public CommentResponse updateComment(Long postId, String userName, Long commentId, CommentUpdateRequest requestDto) {
        User foundUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(USER_NOT_FOUND));

        postRepository.findById(postId)
                .orElseThrow(() -> new AppException(POST_NOT_FOUND));

        Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(COMMENT_NOT_FOUND));

        foundUser.checkAuth(foundComment.getUser().getUsername());

        foundComment.update(requestDto);

        return foundComment.toCommentResponse();
    }

    public CommentResponse createCommentReply(Long postId, String userName, Long commentId, CommentCreateRequest requestDto) {
        User foundUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(USER_NOT_FOUND));

        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(POST_NOT_FOUND));

        Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(COMMENT_NOT_FOUND));

        Comment comment = commentRepository.save(requestDto.toEntity(foundUser, foundPost, foundComment));

        return comment.toCommentResponse();
    }

}