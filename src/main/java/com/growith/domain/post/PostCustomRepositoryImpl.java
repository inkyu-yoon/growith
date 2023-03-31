package com.growith.domain.post;

import com.growith.domain.post.dto.PostGetListResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.growith.domain.post.QPost.post;
import static com.growith.domain.user.QUser.user;
import static com.querydsl.core.group.GroupBy.groupBy;

@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<PostGetListResponse> getPostListsByCategory(Category category, Pageable pageable) {
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
}
