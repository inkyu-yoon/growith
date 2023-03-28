package com.growith.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class UserUpdateResponse {
    private Long id;
    private String nickName;
    private String blog;
}
