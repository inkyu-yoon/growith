package com.growith.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class UserUpdateRequest {
    @NotBlank(message = "닉네임은 존재해야합니다.")
    private String nickName;
    private String blog;
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",message = "올바른 이메일 형식이 아닙니다.")
    private String email;
    @NotBlank(message = "도로명 주소는 존재해야합니다.")
    private String roadNameAddress;
    @NotBlank(message = "상세 주소는 존재해야합니다.")
    private String detailedAddress;
    @NotBlank(message = "우편번호는 존재해야합니다.")
    private String postalCode;
}
