package com.growith.domain.Image.post;

import com.growith.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostFileRepository extends JpaRepository<PostFile, Long> {
    List<PostFile> findAllByPost(Post post);
}
