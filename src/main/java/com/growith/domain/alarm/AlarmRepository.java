package com.growith.domain.alarm;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    Optional<Alarm> findByTargetIdAndFromUserIdAndAlarmType(Long targetId, Long fromUserId, AlarmType alarmType);
}
