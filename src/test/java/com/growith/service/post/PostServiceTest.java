package com.growith.service.post;

import com.growith.domain.post.Category;
import com.growith.domain.post.Post;
import com.growith.domain.post.PostRepository;
import com.growith.domain.post.dto.PostCreateRequest;
import com.growith.domain.post.dto.PostGetListResponse;
import com.growith.domain.post.dto.PostUpdateRequest;
import com.growith.domain.user.User;
import com.growith.domain.user.UserRepository;
import com.growith.global.exception.AppException;
import com.growith.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostService postService;

    @Mock
    private User mockUser;

    @Mock
    private User mockAuthorUser;

    @Mock
    private Post mockPost;

    @Nested
    @DisplayName("게시글 조회 테스트")
    class getPostTest{

        PageRequest pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");

        @Test
        @DisplayName("게시글 리스트 조회 성공")
        void getList(){
            given(postRepository.findAll(pageable))
                    .willReturn(new PageImpl<>(List.of(mockPost)));

            assertDoesNotThrow(() -> postService.getAllPosts(pageable));

            verify(postRepository, atLeastOnce()).findAll(pageable);
        }

        @Test
        @DisplayName("게시글 카테고리별 리스트 조회 성공")
        void getListByCategory(){

            PostGetListResponse postGetListResponse = new PostGetListResponse(1L, "title", "content", "date", "nickName");

            Category category = Category.QNA;

            given(postRepository.getPostsListByCategory(category,pageable))
                    .willReturn(new PageImpl<>(List.of(postGetListResponse)));

            assertDoesNotThrow(() -> postService.getAllPostsByCategory(category,pageable));

            verify(postRepository, atLeastOnce()).getPostsListByCategory(category,pageable);
        }

        @Test
        @DisplayName("게시글 단건 조회 성공")
        void getSuccess(){
            Long postId = 1L;

            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));

            assertDoesNotThrow(() -> postService.getPost(postId));

            verify(postRepository, atLeastOnce()).findById(postId);
        }

        @Test
        @DisplayName("게시글 단건 조회 실패 (게시글이 존재하지 않은 경우)")
        void getError(){
            Long postId = 1L;

            when(postRepository.findById(postId))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> postService.getPost(postId));
            assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
            verify(postRepository, atLeastOnce()).findById(postId);
        }
    }

    @Nested
    @DisplayName("게시글 생성 테스트")
    class createPostTest {

        @Mock
        PostCreateRequest postCreateRequest;
        String email = "email";

        @Test
        @DisplayName("게시글 생성 성공 테스트")
        void createSuccess(){
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));
            given(postCreateRequest.toEntity(mockUser))
                    .willReturn(mockPost);
            given(postRepository.save(mockPost))
                    .willReturn(mockPost);

            assertDoesNotThrow(() -> postService.createPost(email, postCreateRequest));

            verify(userRepository, atLeastOnce()).findByEmail(email);
            verify(postCreateRequest, atLeastOnce()).toEntity(mockUser);
            verify(postRepository, atLeastOnce()).save(mockPost);

        }

        @Test
        @DisplayName("게시글 생성 실패 테스트")
        void createError(){
            when(userRepository.findByEmail(email))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> postService.createPost(email, postCreateRequest));
            assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByEmail(email);

        }
    }

    @Nested
    @DisplayName("게시글 수정 테스트")
    class UpdatePostTest {

        @Mock
        PostUpdateRequest postUpdateRequest;

        Long postId = 1L;
        String email = "email";
        String diffEmail = "diffEmail";

        @Test
        @DisplayName("게시글 수정 성공")
        void updateSuccess(){
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));
            given(mockPost.getUser())
                    .willReturn(mockAuthorUser);
            given(mockAuthorUser.getEmail())
                    .willReturn(email);
            given(mockUser.checkAuth(email))
                    .willReturn(true);

            assertDoesNotThrow(() -> postService.updatePost(postId, email, postUpdateRequest));

            verify(postRepository, atLeastOnce()).findById(postId);
            verify(userRepository, atLeastOnce()).findByEmail(email);
        }

        @Test
        @DisplayName("게시글 수정 실패 (게시글이 존재하지 않는 경우)")
        void updateError1(){
            when(postRepository.findById(postId))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> postService.updatePost(postId, email, postUpdateRequest));

            assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);

            verify(postRepository, atLeastOnce()).findById(postId);
        }

        @Test
        @DisplayName("게시글 수정 실패 (회원이 존재하지 않는 경우)")
        void updateError2(){
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));

            when(userRepository.findByEmail(email))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> postService.updatePost(postId, email, postUpdateRequest));

            assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);

            verify(postRepository, atLeastOnce()).findById(postId);
            verify(userRepository, atLeastOnce()).findByEmail(email);
        }

        @Test
        @DisplayName("게시글 수정 실패 (요청자가 게시글 작성자가 아닌 경우)")
        void updateError3(){
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));
            given(mockPost.getUser())
                    .willReturn(mockAuthorUser);
            given(mockAuthorUser.getEmail())
                    .willReturn(diffEmail);

            AppException appException = assertThrows(AppException.class, () -> postService.updatePost(postId, email, postUpdateRequest));

            assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_MATCH);

            verify(postRepository, atLeastOnce()).findById(postId);
            verify(userRepository, atLeastOnce()).findByEmail(email);
        }
    }
    @Nested
    @DisplayName("게시글 삭제 테스트")
    class DeletePostTest {

        Long postId = 1L;
        String email = "email";
        String diffEmail = "diffEmail";

        @Test
        @DisplayName("게시글 삭제 성공")
        void deleteSuccess(){
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));
            given(mockPost.getUser())
                    .willReturn(mockAuthorUser);
            given(mockAuthorUser.getEmail())
                    .willReturn(email);
            given(mockUser.checkAuth(email))
                    .willReturn(true);

            assertDoesNotThrow(() -> postService.deletePost(postId, email));

            verify(postRepository, atLeastOnce()).findById(postId);
            verify(userRepository, atLeastOnce()).findByEmail(email);
        }

        @Test
        @DisplayName("게시글 삭제 실패 (게시글이 존재하지 않는 경우)")
        void deleteError1(){
            when(postRepository.findById(postId))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> postService.deletePost(postId, email));

            assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);

            verify(postRepository, atLeastOnce()).findById(postId);
        }

        @Test
        @DisplayName("게시글 삭제 실패 (회원이 존재하지 않는 경우)")
        void deleteError2(){
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));

            when(userRepository.findByEmail(email))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> postService.deletePost(postId, email));

            assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);

            verify(postRepository, atLeastOnce()).findById(postId);
            verify(userRepository, atLeastOnce()).findByEmail(email);
        }

        @Test
        @DisplayName("게시글 삭제 실패 (요청자가 게시글 작성자가 아닌 경우)")
        void deleteError3(){
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(userRepository.findByEmail(email))
                    .willReturn(Optional.of(mockUser));
            given(mockPost.getUser())
                    .willReturn(mockAuthorUser);
            given(mockAuthorUser.getEmail())
                    .willReturn(diffEmail);

            AppException appException = assertThrows(AppException.class, () -> postService.deletePost(postId, email));

            assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_MATCH);

            verify(postRepository, atLeastOnce()).findById(postId);
            verify(userRepository, atLeastOnce()).findByEmail(email);
        }
    }
}