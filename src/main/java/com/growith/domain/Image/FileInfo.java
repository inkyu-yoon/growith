package com.growith.domain.Image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FileInfo {
    private Long fileId;
    private String storedUrl;
    private String fileName;
}
