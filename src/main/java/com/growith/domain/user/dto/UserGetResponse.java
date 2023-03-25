package com.growith.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class UserGetResponse {
    private Long id;
    private String name;
    private String imageUrl;
    private String nickName;
    private String email;
    private String blog;
}
