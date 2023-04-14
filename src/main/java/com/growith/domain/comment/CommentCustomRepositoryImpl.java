package com.growith.domain.comment;

import com.growith.domain.comment.dto.CommentGetResponse;
import com.growith.domain.post.Post;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.growith.domain.comment.QComment.comment1;
import static com.querydsl.core.group.GroupBy.groupBy;

@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<CommentGetResponse> getComments(Post post) {
        List<CommentGetResponse> comments = jpaQueryFactory.from(comment1)
                .where(foundByPost(post),isParent())
                .orderBy(comment1.createdDate.asc())
                .transform(groupBy(comment1.id)
                        .list(Projections.constructor(CommentGetResponse.class, comment1, comment1.user)
                        ));


        return comments;
    }

    private Predicate foundByPost(Post post) {
        return comment1.post.eq(post);
    }

    private Predicate isParent() {
        return comment1.parent.isNull();
    }

}
