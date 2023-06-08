package com.growith.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.growith.domain.Image.FileResponse;
import com.growith.domain.Image.S3FileInfo;
import com.growith.domain.Image.post.PostFile;
import com.growith.domain.Image.post.PostFileRepository;
import com.growith.domain.Image.product.ProductImage;
import com.growith.domain.Image.product.ProductImageRepository;
import com.growith.domain.post.Post;
import com.growith.domain.post.PostRepository;
import com.growith.domain.product.Product;
import com.growith.domain.product.ProductRepository;
import com.growith.global.exception.AppException;
import com.growith.global.exception.ErrorCode;
import com.growith.global.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.growith.global.util.constant.AwsS3Constants.ORIGIN_POST_FOLDER;
import static com.growith.global.util.constant.AwsS3Constants.ORIGIN_PRODUCT_FOLDER;

@RequiredArgsConstructor
@Service
@Transactional
public class AwsS3Service {

    private final AmazonS3Client amazonS3Client;

    private final ProductRepository productRepository;
    private final PostRepository postRepository;
    private final ProductImageRepository productImageRepository;
    private final PostFileRepository postFileRepository;


    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public FileResponse uploadProductFiles(Long productId, List<MultipartFile> multipartFiles) {

        FileUtil.checkFileExist(multipartFiles);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        multipartFiles.forEach(multipartFile -> {
                    S3FileInfo s3FileInfo = upload(multipartFile, bucket, ORIGIN_PRODUCT_FOLDER);
                    productImageRepository.save(ProductImage.of(s3FileInfo, product));
                }
        );

        return FileResponse.builder().division(ORIGIN_PRODUCT_FOLDER)
                .divisionId(productId)
                .build();
    }

    public FileResponse uploadPostFiles(Long postId, List<MultipartFile> multipartFiles) {

        FileUtil.checkFileExist(multipartFiles);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        multipartFiles.forEach(multipartFile -> {
                    S3FileInfo s3FileInfo = upload(multipartFile, bucket, ORIGIN_POST_FOLDER);
                    postFileRepository.save(PostFile.of(s3FileInfo, post));
                }
        );

        return FileResponse.builder().division(ORIGIN_POST_FOLDER)
                .divisionId(postId)
                .build();
    }

    public S3FileInfo upload(MultipartFile file, String bucket, String folder) {

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        String originalFileName = file.getOriginalFilename();

        // 파일 형식 체크
        FileUtil.checkFileFormat(originalFileName);

        // 파일 생성
        String key = FileUtil.makeFileName(originalFileName, folder);

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, key, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_ERROR);
        }

        String storedFileUrl = amazonS3Client.getUrl(bucket, key).toString();

        return S3FileInfo.builder()
                .fileName(key)
                .imageUrl(storedFileUrl)
                .build();
    }


    public FileResponse deleteProductFile(Long productId, Long fileId) {

        productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        ProductImage productImage = productImageRepository.findById(fileId)
                .orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_FOUND));

        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, productImage.getFileName()));

        return FileResponse.builder().division(ORIGIN_PRODUCT_FOLDER)
                .divisionId(productId)
                .build();
    }

    public FileResponse deletePostFile(Long postId, Long fileId) {

        postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        PostFile postFile = postFileRepository.findById(fileId)
                .orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_FOUND));

        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, postFile.getFileName()));

        return FileResponse.builder().division(ORIGIN_POST_FOLDER)
                .divisionId(postId)
                .build();
    }


}
