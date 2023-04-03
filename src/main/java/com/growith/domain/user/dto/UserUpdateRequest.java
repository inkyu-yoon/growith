package com.growith.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class UserUpdateRequest {
    @NotBlank(message = "필수로 입력되어야 하는 값입니다.")
    private String nickName;
    private String blog;
    private String email;
}
