package com.growith.domain.likes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class LikeResponse {
    private Long postId;
}
