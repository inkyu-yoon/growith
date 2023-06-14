package com.growith.controller;


import com.growith.domain.Image.FileResponse;
import com.growith.global.Response;
import com.growith.service.AwsS3Service;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.cloud.contract.spec.internal.HttpStatus.CREATED;

@Hidden
@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class FileApiController {
    private final AwsS3Service awsS3Service;


    @PostMapping("/products/{productId}/files")
    public ResponseEntity<Response<FileResponse>> uploadProductFiles(Authentication authentication, @PathVariable(name = "productId") Long productId, @RequestPart List<MultipartFile> multipartFiles) {
        FileResponse response = awsS3Service.uploadProductFiles(productId, multipartFiles);
        return ResponseEntity.status(CREATED).body(Response.success(response));
    }

    @DeleteMapping("/products/{productId}/files/{fileId}")
    public ResponseEntity<Response<FileResponse>> deleteProductFiles(Authentication authentication, @PathVariable(name = "productId") Long productId, @PathVariable(name = "fileId") Long fileId) {
        FileResponse response = awsS3Service.deleteProductFile(productId, fileId);
        return ResponseEntity.status(CREATED).body(Response.success(response));
    }

    @PostMapping("/posts/{postId}/files")
    public ResponseEntity<Response<FileResponse>> uploadPostFiles(Authentication authentication, @PathVariable(name = "postId") Long postId, @RequestPart List<MultipartFile> multipartFiles) {
        FileResponse response = awsS3Service.uploadPostFiles(postId, multipartFiles);
        return ResponseEntity.status(CREATED).body(Response.success(response));
    }

    @DeleteMapping("/posts/{postId}/files/{fileId}")
    public ResponseEntity<Response<FileResponse>> deleteFileFiles(Authentication authentication, @PathVariable(name = "postId") Long postId, @PathVariable(name = "fileId") Long fileId) {
        FileResponse response = awsS3Service.deletePostFile(postId, fileId);
        return ResponseEntity.status(CREATED).body(Response.success(response));
    }
}
