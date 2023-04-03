package com.growith.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class UserGetMyPageResponse {
    private Long id;
    private String userName;
    private String imageUrl;
    private String nickName;
    private String email;
    private String blog;
    private Long point;
    private String githubUrl;
}
