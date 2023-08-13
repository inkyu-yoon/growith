package com.growith.fixture;

import com.growith.domain.alarm.dto.AlarmGetListResponse;
import com.growith.domain.post.dto.PostGetListResponse;

public class AlarmFixture {
    static Long alarmId = 1L;
    static Long postId = 1L;
    static String text = "text";
    static String createdAt = "createdAt";
    static String postName = "postName";
    static String fromUserNickName = "fromUserNickName";

    public static AlarmGetListResponse createAlarmGetListResponse() {
        return AlarmGetListResponse.builder()
                .alarmId(alarmId)
                .postId(postId)
                .postName(postName)
                .fromUserNickName(fromUserNickName)
                .text(text)
                .createdAt(createdAt)
                .build();
    }

}
