package com.growith.domain.post;

import com.growith.domain.post.dto.PostGetListResponse;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static com.growith.domain.post.QPost.post;
import static com.growith.domain.user.QUser.user;
import static com.querydsl.core.group.GroupBy.groupBy;

@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<PostGetListResponse> getPostsListByCategory(Category category, Pageable pageable) {
        List<PostGetListResponse> posts = jpaQueryFactory.from(post)
                .where(post.category.eq(category))
                .join(user).on(user.id.eq(post.user.id))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.createdDate.desc())
                .transform(groupBy(post.id)
                        .list(Projections.constructor(PostGetListResponse.class, post, user)
                        ));

        long total = jpaQueryFactory.selectFrom(post)
                .where(post.category.eq(category))
                .join(user).on(user.eq(post.user))
                .fetch()
                .size();

        return new PageImpl<>(posts, pageable,total);
    }

    @Override
    public Page<PostGetListResponse> getPostsListByUserName(String userName, Pageable pageable) {
        List<PostGetListResponse> posts = jpaQueryFactory.from(post)
                .where(post.user.userName.eq(userName))
                .join(user).on(user.eq(post.user))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.createdDate.desc())
                .transform(groupBy(post.id)
                        .list(Projections.constructor(PostGetListResponse.class, post, user)
                        ));

        long total = jpaQueryFactory.selectFrom(post)
                .where(post.user.userName.eq(userName))
                .join(user).on(user.eq(post.user))
                .fetch()
                .size();

        return new PageImpl<>(posts, pageable,total);
    }

    @Override
    public Page<PostGetListResponse> getPostsListBySearchAndCategory(String searchCondition, String keyword, Category category, Pageable pageable) {
        List<PostGetListResponse> posts = jpaQueryFactory.from(post)
                .where(userNameContains(searchCondition,keyword),titleContains(searchCondition,keyword),contentContains(searchCondition,keyword),post.category.eq(category))
                .join(user).on(user.id.eq(post.user.id))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.createdDate.desc())
                .transform(groupBy(post.id)
                        .list(Projections.constructor(PostGetListResponse.class, post, user)
                        ));

        long total = jpaQueryFactory.selectFrom(post)
                .where(userNameContains(searchCondition,keyword),titleContains(searchCondition,keyword),contentContains(searchCondition,keyword),post.category.eq(category))
                .join(user).on(user.eq(post.user))
                .fetch()
                .size();


        return new PageImpl<>(posts, pageable,total);
    }

    private Predicate userNameContains(String searchCondition, String keyword) {
        if (searchCondition.equals("author")) {
            return keyword == null ? null : post.user.userName.contains(keyword);
        } else {
            return null;
        }
    }

    private Predicate titleContains(String searchCondition, String keyword) {
        if (searchCondition.equals("title")) {
            return keyword == null ? null : post.title.contains(keyword);
        } else {
            return null;
        }
    }

    private Predicate contentContains(String searchCondition, String keyword) {
        if (searchCondition.equals("content")) {
            return keyword == null ? null : post.content.contains(keyword);
        } else {
            return null;
        }
    }


    @Override
    public List<PostGetListResponse> getBestPosts() {
        LocalDate now = LocalDate.now(); // 현재 시간
        LocalDateTime monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay(); // 이번주 월요일
        LocalDateTime sunday = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).atStartOfDay(); // 이번주 일요일

        List<PostGetListResponse> posts = jpaQueryFactory.from(post)
                .where(post.createdDate.between(monday,sunday).and(post.category.ne(Category.NOTICE)))
                .join(user).on(user.id.eq(post.user.id))
                .orderBy(post.likes.size().desc())
                .limit(5)
                .transform(groupBy(post.id)
                        .list(Projections.constructor(PostGetListResponse.class, post, user)
                        ));
        return posts;
    }
}
