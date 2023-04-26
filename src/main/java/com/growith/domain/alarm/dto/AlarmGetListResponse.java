package com.growith.domain.alarm.dto;

import com.growith.domain.post.Post;
import com.growith.domain.user.User;
import com.growith.global.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class AlarmGetListResponse {
    private Long alarmId;
}
