package com.growith.domain.alarm;

import com.growith.domain.BaseEntity;
import com.growith.domain.comment.Comment;
import com.growith.domain.post.Post;
import com.growith.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_date is NULL")
@SQLDelete(sql = "UPDATE alarm SET deleted_date = current_timestamp WHERE id = ?")
public class Alarm  extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User user;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    private Long fromUserId;

    private Long targetId;

    @Builder
    public Alarm(User user, AlarmType alarmType, Long fromUserId, Long targetId) {
        this.user = user;
        this.alarmType = alarmType;
        this.fromUserId = fromUserId;
        this.targetId = targetId;
    }

    public static Alarm of(Post post,User user,AlarmType alarmType) {
        return Alarm.builder()
                .user(post.getUser())
                .alarmType(alarmType)
                .targetId(post.getId())
                .fromUserId(user.getId())
                .build();
    }
    public static Alarm of(Comment comment, User user, AlarmType alarmType) {
        return Alarm.builder()
                .user(comment.getUser())
                .alarmType(alarmType)
                .targetId(comment.getId())
                .fromUserId(user.getId())
                .build();
    }

}
