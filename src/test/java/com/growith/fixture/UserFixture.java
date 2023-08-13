package com.growith.fixture;

import com.growith.domain.user.User;
import com.growith.domain.user.UserRole;
import com.growith.domain.user.dto.UserGetMyPageResponse;
import com.growith.domain.user.dto.UserGetResponse;
import com.growith.domain.user.dto.UserUpdateRequest;
import com.growith.domain.user.dto.UserUpdateResponse;

public class UserFixture {
    static Long userId = 1L;
    static String userName = "userName";
    static String imageUrl = "imageUrl";
    static String nickName = "nickName";
    static String email = "email@email.com";
    static String blog = "blog";
    static String githubUrl = "githubUrl";
    static Long point = 0L;
    static String roadNameAddress = "roadNameAddress";
    static String detailedAddress = "detailedAddress";
    static String postalCode = "postalCode";

    public static User createUser() {
        return User.builder()
                .userName(userName)
                .email(email)
                .point(point)
                .blog(blog)
                .githubUrl(githubUrl)
                .nickName(nickName)
                .imageUrl(imageUrl)
                .userRole(UserRole.ROLE_USER)
                .build();
    }

    public static UserGetResponse createUserGetResponse() {
        return UserGetResponse.builder()
                .id(userId)
                .userName(userName)
                .imageUrl(imageUrl)
                .nickName(nickName)
                .email(email)
                .blog(blog)
                .githubUrl(githubUrl)
                .build();
    }

    public static UserGetMyPageResponse createUserGetMyPageResponse() {
        return UserGetMyPageResponse.builder()
                .id(userId)
                .userName(userName)
                .imageUrl(imageUrl)
                .nickName(nickName)
                .email(email)
                .blog(blog)
                .point(point)
                .githubUrl(githubUrl)
                .roadNameAddress(roadNameAddress)
                .detailedAddress(detailedAddress)
                .postalCode(postalCode)
                .build();
    }

    public static UserUpdateRequest createUserUpdateRequest() {
        return UserUpdateRequest.builder()
                .nickName(nickName)
                .blog(blog)
                .email(email)
                .roadNameAddress(roadNameAddress)
                .detailedAddress(detailedAddress)
                .postalCode(postalCode)
                .build();
    }

    public static UserUpdateResponse createUserUpdateResponse() {
        return UserUpdateResponse.builder()
                .id(userId)
                .nickName(nickName)
                .blog(blog)
                .email(email)
                .roadNameAddress(roadNameAddress)
                .detailedAddress(detailedAddress)
                .postalCode(postalCode)
                .build();
    }
}
