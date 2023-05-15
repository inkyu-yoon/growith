package com.growith.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.growith.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AlarmService {

    private final AlarmRepository alarmRepository;

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    public void createPostCommentAlarm(Long postId, Long userId, AlarmType alarmType) {
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(POST_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(USER_NOT_FOUND));

        if(foundPost.checkUserForAlarm(user)){
            Alarm alarm = Alarm.of(foundPost, user, alarmType);
            alarmRepository.save(alarm);
        }
    }
    public void createReplyAlarm(Long commentId, Long userId, AlarmType alarmType) {
        Comment foundComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(COMMENT_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(USER_NOT_FOUND));

        if(foundComment.checkUserForAlarm(user)){
            Alarm alarm = Alarm.of(foundComment, user, alarmType);
            alarmRepository.save(alarm);
        }
    }

    public void createLikeAlarm(Long postId, Long userId, AlarmType alarmType) {
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(POST_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(USER_NOT_FOUND));

        if(foundPost.checkUserForAlarm(user)){
            Alarm alarm = Alarm.of(foundPost, user, alarmType);
            alarmRepository.save(alarm);
        }
    }

    public void delete(Long postId, Long fromUserId,AlarmType alarmType) {
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(POST_NOT_FOUND));

        userRepository.findById(fromUserId)
                .orElseThrow(() -> new AppException(USER_NOT_FOUND));

        Alarm alarm = alarmRepository.findByTargetIdAndFromUserIdAndAlarmType(foundPost.getId(), fromUserId, alarmType)
                .orElseThrow(() -> new AppException(ALARM_NOT_FOUND));

        alarmRepository.delete(alarm);
    }

    public List<AlarmGetListResponse> getAlarms(String userName) {

        User foundUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(USER_NOT_FOUND));

        return alarmRepository.getAlarms(foundUser.getId());
    }
}
