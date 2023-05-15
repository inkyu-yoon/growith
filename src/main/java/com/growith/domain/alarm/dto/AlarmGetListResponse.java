package com.growith.domain.alarm.dto;

import com.growith.domain.alarm.Alarm;
import com.growith.domain.alarm.AlarmType;
import com.growith.global.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Builder
@Getter
@AllArgsConstructor
public class AlarmGetListResponse {
    private Long alarmId;
    private String fromUserNickName;
    private String postName;
    private Long postId;
    private String text;
    private String createdAt;

    public AlarmGetListResponse(Alarm alarm, String fromUserNickName, String postName) {
        this.alarmId = alarm.getId();
        this.fromUserNickName = fromUserNickName;
        this.postName = postName;
        this.postId = alarm.getTargetId();

        if (alarm.getAlarmType().equals(AlarmType.NEW_LIKE_ON_POST)) {
            this.text = "좋아요를 눌렀습니다.";
        } else if (alarm.getAlarmType().equals(AlarmType.NEW_COMMENT_ON_POST)) {
            this.text = "댓글을 달았습니다.";
        } else if (alarm.getAlarmType().equals(AlarmType.NEW_REPLY_ON_COMMENT)) {
            this.text = "대댓글을 달았습니다.";
        }

        this.createdAt = TimeUtil.convertLocaldatetimeToTime(alarm.getCreatedDate());
    }
}
