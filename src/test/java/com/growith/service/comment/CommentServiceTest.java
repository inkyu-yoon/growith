package com.growith.service.comment;

import com.growith.domain.comment.Comment;
import com.growith.domain.comment.CommentRepository;
import com.growith.domain.comment.dto.CommentCreateRequest;
import com.growith.domain.comment.dto.CommentGetResponse;
import com.growith.domain.comment.dto.CommentUpdateRequest;
import com.growith.domain.post.Post;
import com.growith.domain.post.PostRepository;
import com.growith.domain.user.User;
import com.growith.domain.user.UserRepository;
import com.growith.global.exception.AppException;
import com.growith.global.exception.ErrorCode;
import com.growith.service.CommentService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.growith.global.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @Mock
    private User mockUser;
    @Mock
    private Post mockPost;
    @Mock
    private Comment mockComment;
    @Mock
    private Comment mockCommentReply;


    String userName;
    Long postId;
    Long commentId;

    @BeforeEach
    void setUp() {
        userName = "userName";
        postId = 1L;
        commentId = 1L;
    }

    @Nested
    @DisplayName("댓글 등록 테스트")
    class writeComment {

        @Mock
        CommentCreateRequest request;

        @Test
        @DisplayName("댓글 등록 성공 테스트")
        void writeCommentSuccess() {
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(request.toEntity(mockUser, mockPost))
                    .willReturn(mockComment);
            given(commentRepository.save(mockComment))
                    .willReturn(mockComment);

            assertDoesNotThrow(() -> commentService.createComment(postId, userName, request));

            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(commentRepository, atLeastOnce()).save(mockComment);
        }

        @Test
        @DisplayName("댓글 등록 실패 테스트 (가입된 회원이 아닌 경우)")
        void writeCommentError() {
            when(userRepository.findByUserName(userName))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> commentService.createComment(postId, userName, request));
            assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByUserName(userName);

        }

        @Test
        @DisplayName("댓글 등록 실패 테스트 (게시글을 찾을 수 없는 경우)")
        void writeCommentError2() {
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));

            when(postRepository.findById(postId))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> commentService.createComment(postId, userName, request));
            assertThat(appException.getErrorCode()).isEqualTo(POST_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(postRepository, atLeastOnce()).findById(postId);

        }
    }

    @Nested
    @DisplayName("댓글 조회 테스트")
    class getAllComments{

        @Mock
        CommentGetResponse commentGetResponse;

        @Test
        @DisplayName("댓글 조회 성공 테스트")
        void getAllCommentsSuccess(){
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(commentRepository.getComments(mockPost))
                    .willReturn(List.of(commentGetResponse));

            assertDoesNotThrow(() -> commentService.getAllComments(postId));

            verify(postRepository, atLeastOnce()).findById(postId);
            verify(commentRepository, atLeastOnce()).getComments(mockPost);

        }

        @Test
        @DisplayName("댓글 조회 실패 테스트 (게시글을 찾을 수 없는 경우) ")
        void getAllCommentsError(){
            when(postRepository.findById(postId))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> commentService.getAllComments(postId));
            assertThat(appException.getErrorCode()).isEqualTo(POST_NOT_FOUND);

            verify(postRepository, atLeastOnce()).findById(postId);

        }
    }

    @Nested
    @DisplayName("댓글 삭제 테스트")
    class deleteComment{

        @Test
        @DisplayName("댓글 삭제 성공 테스트")
        void deleteCommentSuccess(){
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(commentRepository.findById(commentId))
                    .willReturn(Optional.of(mockComment));
            given(mockComment.getUser())
                    .willReturn(mockUser);

            assertDoesNotThrow(() -> commentService.deleteComment(postId, userName, commentId));

            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(commentRepository, atLeastOnce()).findById(commentId);

        }

        @Test
        @DisplayName("댓글 삭제 실패 테스트 (가입된 회원이 아닌 경우) ")
        void deleteCommentError(){
            when(userRepository.findByUserName(userName))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> commentService.deleteComment(postId, userName, commentId));
            assertThat(appException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByUserName(userName);

        }

        @Test
        @DisplayName("댓글 삭제 실패 테스트 (게시글을 찾을 수 없는 경우) ")
        void deleteCommentError2(){
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));

            when(postRepository.findById(postId))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> commentService.deleteComment(postId, userName, commentId));
            assertThat(appException.getErrorCode()).isEqualTo(POST_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(postRepository, atLeastOnce()).findById(postId);

        }

        @Test
        @DisplayName("댓글 삭제 실패 테스트 (댓글을 찾을 수 없는 경우) ")
        void deleteCommentError3(){
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));

            when(commentRepository.findById(commentId))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> commentService.deleteComment(postId, userName, commentId));
            assertThat(appException.getErrorCode()).isEqualTo(COMMENT_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(commentRepository, atLeastOnce()).findById(commentId);

        }

        @Test
        @DisplayName("댓글 삭제 실패 테스트 (댓글 작성자가 본인이 아닌 경우) ")
        void deleteCommentError4(){
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(commentRepository.findById(commentId))
                    .willReturn(Optional.of(mockComment));
            given(mockComment.getUser())
                    .willReturn(mockUser);
            given(mockUser.getUsername())
                    .willReturn(userName);

            doThrow(new AppException(USER_NOT_MATCH))
                    .when(mockUser).checkAuth(userName);


            AppException appException = assertThrows(AppException.class, () -> commentService.deleteComment(postId, userName, commentId));
            assertThat(appException.getErrorCode()).isEqualTo(USER_NOT_MATCH);

            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(commentRepository, atLeastOnce()).findById(commentId);

        }
    }

    @Nested
    @DisplayName("댓글 수정 테스트")
    class updateComment{

        @Mock
        CommentUpdateRequest request;

        @Test
        @DisplayName("댓글 수정 성공 테스트")
        void updateCommentSuccess(){
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(commentRepository.findById(commentId))
                    .willReturn(Optional.of(mockComment));
            given(mockComment.getUser())
                    .willReturn(mockUser);

            assertDoesNotThrow(() -> commentService.updateComment(postId, userName, commentId,request));

            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(commentRepository, atLeastOnce()).findById(commentId);

        }

        @Test
        @DisplayName("댓글 수정 실패 테스트 (가입된 회원이 아닌 경우) ")
        void updateCommentError(){
            when(userRepository.findByUserName(userName))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> commentService.updateComment(postId, userName, commentId,request));
            assertThat(appException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByUserName(userName);

        }

        @Test
        @DisplayName("댓글 수정 실패 테스트 (게시글을 찾을 수 없는 경우) ")
        void updateCommentError2(){
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));

            when(postRepository.findById(postId))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () ->  commentService.updateComment(postId, userName, commentId,request));
            assertThat(appException.getErrorCode()).isEqualTo(POST_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(postRepository, atLeastOnce()).findById(postId);

        }

        @Test
        @DisplayName("댓글 수정 실패 테스트 (댓글을 찾을 수 없는 경우) ")
        void updateCommentError3(){
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));

            when(commentRepository.findById(commentId))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> commentService.updateComment(postId, userName, commentId,request));
            assertThat(appException.getErrorCode()).isEqualTo(COMMENT_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(commentRepository, atLeastOnce()).findById(commentId);

        }

        @Test
        @DisplayName("댓글 수정 실패 테스트 (댓글 작성자가 본인이 아닌 경우) ")
        void updateCommentError4(){
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(commentRepository.findById(commentId))
                    .willReturn(Optional.of(mockComment));
            given(mockComment.getUser())
                    .willReturn(mockUser);
            given(mockUser.getUsername())
                    .willReturn(userName);

            doThrow(new AppException(USER_NOT_MATCH))
                    .when(mockUser).checkAuth(userName);


            AppException appException = assertThrows(AppException.class, () -> commentService.updateComment(postId, userName, commentId,request));
            assertThat(appException.getErrorCode()).isEqualTo(USER_NOT_MATCH);

            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(commentRepository, atLeastOnce()).findById(commentId);

        }
    }

    @Nested
    @DisplayName("대댓글 등록 테스트")
    class writeCommentReply {

        @Mock
        CommentCreateRequest request;

        @Test
        @DisplayName("대댓글 등록 성공 테스트")
        void writeCommentReplySuccess() {
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(commentRepository.findById(commentId))
                    .willReturn(Optional.of(mockComment));
            given(request.toEntity(mockUser, mockPost,mockComment))
                    .willReturn(mockCommentReply);
            given(commentRepository.save(mockCommentReply))
                    .willReturn(mockCommentReply);

            assertDoesNotThrow(() -> commentService.createCommentReply(postId, userName,commentId, request));

            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(commentRepository, atLeastOnce()).findById(commentId);
            verify(commentRepository, atLeastOnce()).save(mockCommentReply);
        }

        @Test
        @DisplayName("대댓글 등록 실패 테스트 (가입된 회원이 아닌 경우)")
        void writeCommentReplyError() {
            when(userRepository.findByUserName(userName))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> commentService.createCommentReply(postId, userName,commentId, request));
            assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByUserName(userName);

        }

        @Test
        @DisplayName("대댓글 등록 실패 테스트 (게시글을 찾을 수 없는 경우)")
        void writeCommentReplyError2() {
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));

            when(postRepository.findById(postId))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> commentService.createCommentReply(postId, userName,commentId, request));
            assertThat(appException.getErrorCode()).isEqualTo(POST_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(postRepository, atLeastOnce()).findById(postId);

        }

        @Test
        @DisplayName("대댓글 등록 실패 테스트 (댓글을 찾을 수 없는 경우)")
        void writeCommentReplyError3() {
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));

            when(commentRepository.findById(commentId))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> commentService.createCommentReply(postId, userName,commentId, request));
            assertThat(appException.getErrorCode()).isEqualTo(COMMENT_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(commentRepository, atLeastOnce()).findById(commentId);

        }
    }
}