package com.growith.global.event;

import com.growith.domain.alarm.AlarmType;
import com.growith.domain.comment.dto.CommentReplyResponse;
import com.growith.domain.comment.dto.CommentResponse;
import com.growith.domain.likes.dto.LikeResponse;
import com.growith.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlarmEventListener {

    private final AlarmService alarmService;

    @EventListener(classes = CommentResponse.class)
    public void handle(CommentResponse event) {
        alarmService.createPostAlarm(event.getPostId(), event.getFromUserId(), AlarmType.NEW_COMMENT_ON_POST);
    }

    @EventListener(classes = CommentReplyResponse.class)
    public void handle(CommentReplyResponse event) {
        alarmService.createReplyAlarm(event.getCommentId(), event.getFromUserId(), AlarmType.NEW_REPLY_ON_COMMENT);
    }

    @EventListener(classes = LikeResponse.class)
    public void handle(LikeResponse event) {
        if (event.getIsHistoryFound()) {
            alarmService.delete(event.getPostId(), event.getFromUserId(),AlarmType.NEW_LIKE_ON_POST);
        } else {
            alarmService.createPostAlarm(event.getPostId(), event.getFromUserId(), AlarmType.NEW_LIKE_ON_POST);
        }
    }
}
