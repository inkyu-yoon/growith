package com.growith.fixture;

import com.growith.domain.post.dto.PostGetListResponse;
import com.growith.domain.user.dto.UserUpdateRequest;

public class PostFixture {
    static Long postId = 1L;
    static String title = "title";
    static String content = "content";
    static String date = "date";
    static String nickName = "nickName";
    static String imageUrl = "imageUrl";
    static Long view = 0L;
    static int numOfComments = 0;
    static int numOfLikes = 0;


    public static PostGetListResponse createPostGetListResponse() {
        return PostGetListResponse.builder()
                .postId(postId)
                .title(title)
                .content(content)
                .date(date)
                .nickName(nickName)
                .imageUrl(imageUrl)
                .view(view)
                .numOfComments(numOfComments)
                .numOfLikes(numOfLikes)
                .build();
    }

}
