package com.growith.domain.Image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FileResponse {
    private String division;
    private Long divisionId;
}

