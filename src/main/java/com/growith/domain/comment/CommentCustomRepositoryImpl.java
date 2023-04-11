package com.growith.domain.comment;

import com.growith.domain.comment.dto.CommentGetResponse;
import com.growith.domain.post.Post;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.growith.domain.comment.QComment.comment1;
import static com.querydsl.core.group.GroupBy.groupBy;

@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public Page<CommentGetResponse> getComments(Post post, Pageable pageable) {
        List<CommentGetResponse> comments = jpaQueryFactory.from(comment1)
                .where(comment1.post.eq(post))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(comment1.createdDate.desc())
                .transform(groupBy(comment1.id)
                        .list(Projections.constructor(CommentGetResponse.class, comment1, comment1.user)
                        ));

        long total = jpaQueryFactory.selectFrom(comment1)
                .where(comment1.post.eq(post))
                .fetch()
                .size();

        return new PageImpl<>(comments, pageable,total);
    }
}
