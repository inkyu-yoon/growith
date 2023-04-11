package com.growith.service.comment;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Page<CommentGetResponse> getAllComments(Long postId, Pageable pageable) {
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(POST_NOT_FOUND));

        return commentRepository.getComments(foundPost, pageable);
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
}
