package com.growith.service;

import com.growith.domain.likes.PostLike;
import com.growith.domain.likes.PostLikeRepository;
import com.growith.domain.post.Post;
import com.growith.domain.post.PostRepository;
import com.growith.domain.user.User;
import com.growith.domain.user.UserRepository;
import com.growith.global.exception.AppException;
import com.growith.global.exception.ErrorCode;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PostLikeServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    PostLikeRepository postLikeRepository;

    @InjectMocks
    PostLikeService postLikeService;

    @Mock
    User mockUser;
    @Mock
    Post mockPost;
    @Mock
    PostLike mockPostLike;

    String userName;
    Long postId;

    @BeforeEach
    void setUp() {
        userName = "userName";
        postId = 1L;

    }

    @Nested
    @DisplayName("좋아요 증가 테스트")
    class AddLikesTest {

        @Test
        @DisplayName("좋아요 증가 성공 테스트")
        void addLikesSuccess(){
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(postLikeRepository.findByUserAndPost(mockUser, mockPost))
                    .willReturn(Optional.empty());

            assertDoesNotThrow(() -> postLikeService.addLike(userName, postId));

            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(postLikeRepository, atLeastOnce()).findByUserAndPost(mockUser, mockPost);
            verify(postLikeRepository, atLeastOnce()).save(any(PostLike.class));

        }

        @Test
        @DisplayName("좋아요 취소 성공 테스트 (이미 좋아요를 눌렀던 회원인 경우)")
        void addLikesSuccess2(){
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(postLikeRepository.findByUserAndPost(mockUser, mockPost))
                    .willReturn(Optional.of(mockPostLike));

            assertDoesNotThrow(() -> postLikeService.addLike(userName, postId));

            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(postRepository, atLeastOnce()).findById(postId);
            verify(postLikeRepository, atLeastOnce()).findByUserAndPost(mockUser, mockPost);
            verify(postLikeRepository, atLeastOnce()).delete(mockPostLike);

        }

        @Test
        @DisplayName("좋아요 실패 테스트 (가입된 회원이 아닌 경우)")
        void addLikesError1(){
            when(userRepository.findByUserName(userName))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> postLikeService.addLike(userName, postId));
            assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByUserName(userName);

        }

        @Test
        @DisplayName("좋아요 실패 테스트 (게시글이 존재하지 않는 경우)")
        void addLikesError2(){
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));

            when(postRepository.findById(postId))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> postLikeService.addLike(userName, postId));
            assertThat(appException.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(postRepository, atLeastOnce()).findById(postId);

        }
    }
}