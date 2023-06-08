package com.growith.domain.Image;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class S3FileInfo {
    private String imageUrl;
    private String filePath;
    private String fileName;
}
