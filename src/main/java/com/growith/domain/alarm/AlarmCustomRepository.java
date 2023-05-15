package com.growith.domain.alarm;

import com.growith.domain.alarm.dto.AlarmGetListResponse;
import com.growith.domain.comment.dto.CommentGetResponse;
import com.growith.domain.post.Post;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmCustomRepository {

    List<AlarmGetListResponse> getAlarms(Long userId);
}
