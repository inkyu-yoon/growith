package com.growith.domain.Image.post;

import com.growith.domain.BaseEntity;
import com.growith.domain.Image.S3FileInfo;
import com.growith.domain.Image.product.ProductImage;
import com.growith.domain.post.Post;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_date is NULL")
@SQLDelete(sql = "UPDATE postimage SET deleted_date = current_timestamp WHERE id = ?")
public class PostFile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;
    private String fileName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public static PostFile of(S3FileInfo s3FileInfo, Post post) {
        return PostFile.builder()
                .post(post)
                .imageUrl(s3FileInfo.getImageUrl())
                .fileName(s3FileInfo.getFileName())
                .build();
    }

}
