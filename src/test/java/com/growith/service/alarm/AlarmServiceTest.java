package com.growith.service.alarm;

import com.growith.domain.alarm.Alarm;
import com.growith.domain.alarm.AlarmRepository;
import com.growith.domain.alarm.AlarmType;
import com.growith.domain.alarm.dto.AlarmGetListResponse;
import com.growith.domain.comment.Comment;
import com.growith.domain.comment.CommentRepository;
import com.growith.domain.post.Post;
import com.growith.domain.post.PostRepository;
import com.growith.domain.user.User;
import com.growith.domain.user.UserRepository;
import com.growith.global.exception.AppException;
import com.growith.service.AlarmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.growith.global.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlarmServiceTest {

    @Mock
    private AlarmRepository alarmRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private AlarmService alarmService;

    @Mock
    User mockUser;
    @Mock
    Post mockPost;

    @Mock
    Comment mockComment;
    @Mock
    Alarm mockAlarm;

    @Mock
    AlarmGetListResponse mockAlarmGetListResponse;

    Long postId;
    Long userId;
    Long commentId;
    Long alarmId;
    String userName;
    AlarmType postAlarmType;
    AlarmType replyAlarmType;
    AlarmType likeAlarmType;

    @BeforeEach
    void setUp() {
        userId = 1L;
        postId = 1L;
        commentId = 1L;
        alarmId = 1L;
        userName = "userName";
        postAlarmType = AlarmType.NEW_COMMENT_ON_POST;
        replyAlarmType = AlarmType.NEW_REPLY_ON_COMMENT;
        likeAlarmType = AlarmType.NEW_LIKE_ON_POST;
    }

    @Nested
    @DisplayName("알림 생성 테스트")
    class CreateAlarmTest {

        @Test
        @DisplayName("게시글 댓글 알림 성공")
        void postAlarmSuccess() {
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(userRepository.findById(userId))
                    .willReturn(Optional.of(mockUser));
            given(mockPost.checkUserForAlarm(mockUser))
                    .willReturn(true);

            assertDoesNotThrow(() -> alarmService.createPostAlarm(postId, userId, postAlarmType));

            verify(postRepository, atLeastOnce()).findById(postId);
            verify(userRepository, atLeastOnce()).findById(userId);
            verify(mockPost, atLeastOnce()).checkUserForAlarm(mockUser);
            verify(alarmRepository, atLeastOnce()).save(any());
        }

        @Test
        @DisplayName("게시글 댓글 알림 실패 (게시글이 존재하지 않은 경우)")
        void postAlarmError1() {

            when(postRepository.findById(postId))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> alarmService.createPostAlarm(postId, userId, postAlarmType));
            assertThat(appException.getErrorCode()).isEqualTo(POST_NOT_FOUND);
            verify(postRepository, atLeastOnce()).findById(postId);
        }

        @Test
        @DisplayName("게시글 댓글 알림 실패 (가입된 회원이 아닌 경우)")
        void postAlarmError2() {
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            when(userRepository.findById(userId))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> alarmService.createPostAlarm(postId, userId, postAlarmType));
            assertThat(appException.getErrorCode()).isEqualTo(USER_NOT_FOUND);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(userRepository, atLeastOnce()).findById(userId);
        }

        @Test
        @DisplayName("게시글 댓글 알림 실패 (본인 게시글에 댓글을 남긴 경우)")
        void postAlarmError3() {
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(userRepository.findById(userId))
                    .willReturn(Optional.of(mockUser));
            given(mockPost.checkUserForAlarm(mockUser))
                    .willReturn(false);


            assertDoesNotThrow(() -> alarmService.createPostAlarm(postId, userId, postAlarmType));

            verify(postRepository, atLeastOnce()).findById(postId);
            verify(userRepository, atLeastOnce()).findById(userId);
            verify(mockPost, atLeastOnce()).checkUserForAlarm(mockUser);

            verify(alarmRepository, never()).save(any());

        }

        @Test
        @DisplayName("댓글 대댓글 알림 성공")
        void replyAlarmSuccess() {
            given(commentRepository.findById(commentId))
                    .willReturn(Optional.of(mockComment));
            given(userRepository.findById(userId))
                    .willReturn(Optional.of(mockUser));
            given(mockComment.checkUserForAlarm(mockUser))
                    .willReturn(true);

            assertDoesNotThrow(() -> alarmService.createReplyAlarm(commentId, userId, replyAlarmType));

            verify(commentRepository, atLeastOnce()).findById(commentId);
            verify(userRepository, atLeastOnce()).findById(userId);
            verify(mockComment, atLeastOnce()).checkUserForAlarm(mockUser);
            verify(alarmRepository, atLeastOnce()).save(any());
        }

        @Test
        @DisplayName("댓글 대댓글 알림 실패 (댓글이 존재하지 않은 경우)")
        void replyAlarmError1() {

            when(commentRepository.findById(commentId))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> alarmService.createReplyAlarm(commentId, userId, replyAlarmType));
            assertThat(appException.getErrorCode()).isEqualTo(COMMENT_NOT_FOUND);
            verify(commentRepository, atLeastOnce()).findById(commentId);
        }

        @Test
        @DisplayName("댓글 대댓글 알림 실패 (가입된 회원이 아닌 경우)")
        void replyAlarmError2() {
            given(commentRepository.findById(commentId))
                    .willReturn(Optional.of(mockComment));
            when(userRepository.findById(userId))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> alarmService.createReplyAlarm(commentId, userId, replyAlarmType));
            assertThat(appException.getErrorCode()).isEqualTo(USER_NOT_FOUND);
            verify(commentRepository, atLeastOnce()).findById(commentId);
            verify(userRepository, atLeastOnce()).findById(userId);
        }

        @Test
        @DisplayName("댓글 대댓글 알림 실패 (본인 댓글에 대댓글을 남긴 경우)")
        void replyAlarmError3() {
            given(commentRepository.findById(commentId))
                    .willReturn(Optional.of(mockComment));
            given(userRepository.findById(userId))
                    .willReturn(Optional.of(mockUser));
            given(mockComment.checkUserForAlarm(mockUser))
                    .willReturn(false);


            assertDoesNotThrow(() -> alarmService.createReplyAlarm(commentId, userId, replyAlarmType));

            verify(commentRepository, atLeastOnce()).findById(commentId);
            verify(userRepository, atLeastOnce()).findById(userId);
            verify(mockComment, atLeastOnce()).checkUserForAlarm(mockUser);

            verify(alarmRepository, never()).save(any());

        }
    }

    @Nested
    @DisplayName("알림 삭제 테스트")
    class DeleteAlarmTest {

        @Test
        @DisplayName("알림 삭제 성공 테스트 (게시글 좋아요 취소 시)")
        void deleteAlarmByLikeSuccess() {
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(userRepository.findById(userId))
                    .willReturn(Optional.of(mockUser));
            given(mockPost.getId())
                    .willReturn(postId);
            given(alarmRepository.findByTargetIdAndFromUserIdAndAlarmType(postId, userId, likeAlarmType))
                    .willReturn(Optional.of(mockAlarm));

            assertDoesNotThrow(() -> alarmService.delete(postId, userId, likeAlarmType));

            verify(postRepository, atLeastOnce()).findById(postId);
            verify(userRepository, atLeastOnce()).findById(userId);
            verify(alarmRepository, atLeastOnce()).findByTargetIdAndFromUserIdAndAlarmType(postId, userId, likeAlarmType);
            verify(alarmRepository, atLeastOnce()).delete(mockAlarm);
        }

        @Test
        @DisplayName("알림 삭제 실패1 (게시글을 찾을 수 없는 경우)")
        void deleteAlarmByLikeError1() {
            when(postRepository.findById(postId))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> alarmService.delete(postId, userId, likeAlarmType));
            assertThat(appException.getErrorCode()).isEqualTo(POST_NOT_FOUND);
            verify(postRepository, atLeastOnce()).findById(postId);
        }

        @Test
        @DisplayName("알림 삭제 실패2 (가입된 회원이 아닌 경우)")
        void deleteAlarmByLikeError2() {
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));

            when(userRepository.findById(userId))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> alarmService.delete(postId, userId, likeAlarmType));
            assertThat(appException.getErrorCode()).isEqualTo(USER_NOT_FOUND);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(userRepository, atLeastOnce()).findById(userId);
        }

        @Test
        @DisplayName("알림 삭제 실패2 (삭제할 알림이 존재하지 않는 경우)")
        void deleteAlarmByLikeError3() {
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(userRepository.findById(userId))
                    .willReturn(Optional.of(mockUser));
            given(mockPost.getId())
                    .willReturn(postId);

            when(alarmRepository.findByTargetIdAndFromUserIdAndAlarmType(postId, userId, likeAlarmType))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> alarmService.delete(postId, userId, likeAlarmType));
            assertThat(appException.getErrorCode()).isEqualTo(ALARM_NOT_FOUND);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(userRepository, atLeastOnce()).findById(userId);
        }

        @Test
        @DisplayName("알림 삭제 성공 테스트 (alarmId로 삭제 시)")
        void deleteAlarmSuccess() {
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));
            given(alarmRepository.findById(alarmId))
                    .willReturn(Optional.of(mockAlarm));
            given(mockAlarm.getUser())
                    .willReturn(mockUser);
            willDoNothing()
                    .given(mockUser)
                    .checkAuth(userName);

            assertDoesNotThrow(() -> alarmService.delete(userName, alarmId));

            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(alarmRepository, atLeastOnce()).findById(alarmId);
            verify(alarmRepository, atLeastOnce()).delete(mockAlarm);
        }

        @Test
        @DisplayName("알림 삭제 실패 테스트 (alarmId로 삭제 시 가입된 회원이 아닌 경우)")
        void deleteAlarmError1() {
            when(userRepository.findByUserName(userName))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> alarmService.delete(userName, alarmId));
            assertThat(appException.getErrorCode()).isEqualTo(USER_NOT_FOUND);
            verify(userRepository, atLeastOnce()).findByUserName(userName);
        }

        @Test
        @DisplayName("알림 삭제 실패 테스트 (alarmId로 삭제 시 알림을 찾을 수 없는 경우)")
        void deleteAlarmError2() {
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));
            when(alarmRepository.findById(alarmId))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> alarmService.delete(userName, alarmId));
            assertThat(appException.getErrorCode()).isEqualTo(ALARM_NOT_FOUND);
            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(alarmRepository, atLeastOnce()).findById(alarmId);
        }

        @Test
        @DisplayName("알림 삭제 실패 테스트 (alarmId로 삭제 시 본인 알림이 아닌 경우)")
        void deleteAlarmError3() {
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));
            given(alarmRepository.findById(alarmId))
                    .willReturn(Optional.of(mockAlarm));
            given(mockAlarm.getUser())
                    .willReturn(mockUser);


            doThrow(new AppException(USER_NOT_MATCH))
                    .when(mockUser)
                    .checkAuth(userName);

            AppException appException = assertThrows(AppException.class, () -> alarmService.delete(userName, alarmId));
            assertThat(appException.getErrorCode()).isEqualTo(USER_NOT_MATCH);
            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(alarmRepository, atLeastOnce()).findById(alarmId);
        }

    }

    @Nested
    @DisplayName("알림 조회 테스트")
    class GetAlarmTest {

        @Test
        @DisplayName("알림 조회 성공 테스트")
        void getAlarmSuccess(){
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));
            given(mockUser.getId())
                    .willReturn(userId);
            given(alarmRepository.getAlarms(userId))
                    .willReturn(List.of(mockAlarmGetListResponse));

            assertDoesNotThrow(() -> alarmService.getAlarms(userName));

            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(alarmRepository, atLeastOnce()).getAlarms(userId);
        }

        @Test
        @DisplayName("알림 조회 실패 테스트 (가입된 회원이 아닌 경우)")
        void getAlarmDelete(){
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> alarmService.getAlarms(userName));
            assertThat(appException.getErrorCode()).isEqualTo(USER_NOT_FOUND);
            verify(userRepository, atLeastOnce()).findByUserName(userName);
        }
    }
}