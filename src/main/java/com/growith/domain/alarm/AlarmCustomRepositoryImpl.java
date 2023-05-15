package com.growith.domain.alarm;

import com.growith.domain.alarm.dto.AlarmGetListResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.growith.domain.alarm.QAlarm.*;
import static com.growith.domain.post.QPost.*;
import static com.growith.domain.user.QUser.*;
import static com.querydsl.core.group.GroupBy.groupBy;

@RequiredArgsConstructor
public class AlarmCustomRepositoryImpl implements AlarmCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<AlarmGetListResponse> getAlarms(Long userId) {
        List<AlarmGetListResponse> result = jpaQueryFactory.from(alarm)
                .where(alarm.user.id.eq(userId))
                .join(user).on(user.id.eq(alarm.fromUserId))
                .join(post).on(post.id.eq(alarm.targetId))
                .orderBy(alarm.createdDate.desc())
                .transform(groupBy(alarm.id).list(
                        Projections.constructor(AlarmGetListResponse.class,
                                alarm, user.nickName, post.title)));
        return result;
    }
}
