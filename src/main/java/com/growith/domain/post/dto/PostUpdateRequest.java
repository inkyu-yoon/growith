package com.growith.domain.post.dto;

import com.growith.domain.post.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateRequest {
    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    private String title;
    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    private String content;
    @NotNull(message = "유효하지 않은 카테고리가 입력되었습니다.")
    private Category category;
}
