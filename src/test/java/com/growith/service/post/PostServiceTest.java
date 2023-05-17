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
import com.growith.global.util.CookieUtil;
import com.growith.global.util.TextParsingUtil;
import com.growith.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.growith.global.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    String userName;
    Long postId;
    String diffUserName;
    PostGetListResponse postGetListResponse;
    PageRequest pageable;

    @BeforeEach
    void setUp() {
        userName = "userName";
        postId = 1L;
        diffUserName = "diffUserName";
        postGetListResponse = PostGetListResponse
                .builder()
                .postId(1L)
                .title("title")
                .content("content")
                .date("date")
                .nickName("nickName")
                .view(0L)
                .build();
        pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");

    }

    @Nested
    @DisplayName("게시글 조회 테스트")
    class getPostTest {




        @Test
        @DisplayName("게시글 리스트 조회 성공")
        void getList() {
            given(postRepository.findAll(pageable))
                    .willReturn(new PageImpl<>(List.of(mockPost)));

            assertDoesNotThrow(() -> postService.getAllPosts(pageable));

            verify(postRepository, atLeastOnce()).findAll(pageable);
        }

        @Test
        @DisplayName("게시글 카테고리별 리스트 조회 성공")
        void getListByCategory() {

            Category category = Category.QNA;

            given(postRepository.getPostsListByCategory(category, pageable))
                    .willReturn(new PageImpl<>(List.of(postGetListResponse)));

            assertDoesNotThrow(() -> postService.getAllPostsByCategory(category, pageable));

            verify(postRepository, atLeastOnce()).getPostsListByCategory(category, pageable);
        }

        @Test
        @DisplayName("게시글 회원별 리스트 조회 성공")
        void getListByUser() {


            given(postRepository.getPostsListByUserName(userName, pageable))
                    .willReturn(new PageImpl<>(List.of(postGetListResponse)));

            assertDoesNotThrow(() -> postService.getAllPostsByUserName(userName, pageable));

            verify(postRepository, atLeastOnce()).getPostsListByUserName(userName, pageable);
        }

        @Test
        @DisplayName("게시글 단건 조회 성공")
        void getSuccess() {

            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));

            assertDoesNotThrow(() -> postService.getPost(postId));

            verify(postRepository, atLeastOnce()).findById(postId);
        }

        @Test
        @DisplayName("게시글 단건 조회 실패 (게시글이 존재하지 않은 경우)")
        void getError() {

            when(postRepository.findById(postId))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> postService.getPost(postId));
            assertThat(appException.getErrorCode()).isEqualTo(POST_NOT_FOUND);
            verify(postRepository, atLeastOnce()).findById(postId);
        }
    }

    @Nested
    @DisplayName("게시글 조회 테스트 (UI용)")
    class getPostsTest{

        @Test
        @DisplayName("게시글 카테고리별 검색 조회 성공")
        void getBySearchSuccess() {

            given(postRepository.getPostsListBySearchAndCategory("SearchCondition","keyword",Category.QNA,pageable))
                    .willReturn(new PageImpl<>(List.of(postGetListResponse)));

            assertDoesNotThrow(() -> postService.getPostsBySearchAndCategory("SearchCondition","keyword",Category.QNA,pageable));

            verify(postRepository, atLeastOnce()).getPostsListBySearchAndCategory("SearchCondition","keyword",Category.QNA,pageable);
        }

        @Test
        @DisplayName("베스트 게시글 조회 성공")
        void getBestPostsSuccess() {

            given(postRepository.getBestPosts())
                    .willReturn(List.of(postGetListResponse));

            assertDoesNotThrow(() -> postService.getBestPosts());

            verify(postRepository, atLeastOnce()).getBestPosts();
        }
    }

    @Nested
    @DisplayName("게시글 생성 테스트")
    class createPostTest {

        @Mock
        PostCreateRequest postCreateRequest;

        @Test
        @DisplayName("게시글 생성 성공 테스트")
        void createSuccess() {
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));
            given(postCreateRequest.toEntity(mockUser))
                    .willReturn(mockPost);
            given(postRepository.save(mockPost))
                    .willReturn(mockPost);

            assertDoesNotThrow(() -> postService.createPost(userName, postCreateRequest));

            verify(userRepository, atLeastOnce()).findByUserName(userName);
            verify(postCreateRequest, atLeastOnce()).toEntity(mockUser);
            verify(postRepository, atLeastOnce()).save(mockPost);

        }

        @Test
        @DisplayName("게시글 생성 실패 테스트")
        void createError() {
            when(userRepository.findByUserName(userName))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> postService.createPost(userName, postCreateRequest));
            assertThat(appException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(userRepository, atLeastOnce()).findByUserName(userName);

        }
    }

    @Nested
    @DisplayName("게시글 수정 테스트")
    class UpdatePostTest {

        @Mock
        PostUpdateRequest postUpdateRequest;

        @Test
        @DisplayName("게시글 수정 성공")
        void updateSuccess() {
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));
            given(mockPost.getUser())
                    .willReturn(mockAuthorUser);
            given(mockAuthorUser.getUsername())
                    .willReturn(userName);


            assertDoesNotThrow(() -> postService.updatePost(postId, userName, postUpdateRequest));

            verify(postRepository, atLeastOnce()).findById(postId);
            verify(userRepository, atLeastOnce()).findByUserName(userName);
        }

        @Test
        @DisplayName("게시글 수정 실패 (게시글이 존재하지 않는 경우)")
        void updateError1() {
            when(postRepository.findById(postId))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> postService.updatePost(postId, userName, postUpdateRequest));

            assertThat(appException.getErrorCode()).isEqualTo(POST_NOT_FOUND);

            verify(postRepository, atLeastOnce()).findById(postId);
        }

        @Test
        @DisplayName("게시글 수정 실패 (회원이 존재하지 않는 경우)")
        void updateError2() {
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));

            when(userRepository.findByUserName(userName))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> postService.updatePost(postId, userName, postUpdateRequest));

            assertThat(appException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(postRepository, atLeastOnce()).findById(postId);
            verify(userRepository, atLeastOnce()).findByUserName(userName);
        }

        @Test
        @DisplayName("게시글 수정 실패 (요청자가 게시글 작성자가 아닌 경우)")
        void updateError3() {
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));
            given(mockPost.getUser())
                    .willReturn(mockAuthorUser);
            given(mockAuthorUser.getUsername())
                    .willReturn(diffUserName);

            doThrow(new AppException(USER_NOT_MATCH))
                    .when(mockUser).checkAuth(diffUserName);

            AppException appException = assertThrows(AppException.class, () -> postService.updatePost(postId, userName, postUpdateRequest));

            assertThat(appException.getErrorCode()).isEqualTo(USER_NOT_MATCH);

            verify(postRepository, atLeastOnce()).findById(postId);
            verify(userRepository, atLeastOnce()).findByUserName(userName);
        }
    }

    @Nested
    @DisplayName("게시글 삭제 테스트")
    class DeletePostTest {


        @Test
        @DisplayName("게시글 삭제 성공")
        void deleteSuccess() {
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));
            given(mockPost.getUser())
                    .willReturn(mockAuthorUser);
            given(mockAuthorUser.getUsername())
                    .willReturn(userName);


            assertDoesNotThrow(() -> postService.deletePost(postId, userName));

            verify(postRepository, atLeastOnce()).findById(postId);
            verify(userRepository, atLeastOnce()).findByUserName(userName);
        }

        @Test
        @DisplayName("게시글 삭제 실패 (게시글이 존재하지 않는 경우)")
        void deleteError1() {
            when(postRepository.findById(postId))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> postService.deletePost(postId, userName));

            assertThat(appException.getErrorCode()).isEqualTo(POST_NOT_FOUND);

            verify(postRepository, atLeastOnce()).findById(postId);
        }

        @Test
        @DisplayName("게시글 삭제 실패 (회원이 존재하지 않는 경우)")
        void deleteError2() {
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));

            when(userRepository.findByUserName(userName))
                    .thenReturn(Optional.empty());

            AppException appException = assertThrows(AppException.class, () -> postService.deletePost(postId, userName));

            assertThat(appException.getErrorCode()).isEqualTo(USER_NOT_FOUND);

            verify(postRepository, atLeastOnce()).findById(postId);
            verify(userRepository, atLeastOnce()).findByUserName(userName);
        }

        @Test
        @DisplayName("게시글 삭제 실패 (요청자가 게시글 작성자가 아닌 경우)")
        void deleteError3() {
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(userRepository.findByUserName(userName))
                    .willReturn(Optional.of(mockUser));
            given(mockPost.getUser())
                    .willReturn(mockAuthorUser);
            given(mockAuthorUser.getUsername())
                    .willReturn(diffUserName);


            doThrow(new AppException(USER_NOT_MATCH))
                    .when(mockUser).checkAuth(diffUserName);

            AppException appException = assertThrows(AppException.class, () -> postService.deletePost(postId, userName));

            assertThat(appException.getErrorCode()).isEqualTo(USER_NOT_MATCH);

            verify(postRepository, atLeastOnce()).findById(postId);
            verify(userRepository, atLeastOnce()).findByUserName(userName);
        }
    }

    @Nested
    @DisplayName("조회수 증가 테스트")
    class IncreaseViewTest {
        String viewHistory = "viewHistory";

        @Mock
        HttpServletRequest httpServletRequest;
        @Mock
        HttpServletResponse httpServletResponse;
        MockedStatic<CookieUtil> cookieUtilMockedStatic;
        MockedStatic<TextParsingUtil> textParsingUtilMockedStatic;

        @BeforeEach
        void setUp() {
            cookieUtilMockedStatic = mockStatic(CookieUtil.class);
            textParsingUtilMockedStatic = mockStatic(TextParsingUtil.class);
        }

        @AfterEach
        void close() {
            cookieUtilMockedStatic.close();
            textParsingUtilMockedStatic.close();
        }

        @Test
        @DisplayName("조회수 증가 성공 테스트 (쿠키는 존재하나, 해당 게시글 방문 이력이 쿠키에 없는 경우)")
        void increaseSuccess() {
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(CookieUtil.getCookie(any(), anyString()))
                    .willReturn(viewHistory);
            given(TextParsingUtil.parsingViewHistory(viewHistory))
                    .willReturn(new HashSet<>());

            assertDoesNotThrow(() -> postService.increaseView(postId, httpServletRequest, httpServletResponse));

            verify(postRepository, atLeastOnce()).findById(postId);
            verify(mockPost, atLeastOnce()).increaseView();
        }

        @Test
        @DisplayName("조회수 증가 성공 테스트 (쿠키가 자체가 존재하지 않는 경우)")
        void increaseSuccess2() {
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(CookieUtil.getCookie(any(), anyString()))
                    .willReturn(null);


            assertDoesNotThrow(() -> postService.increaseView(postId, httpServletRequest, httpServletResponse));

            verify(postRepository, atLeastOnce()).findById(postId);
            verify(mockPost, atLeastOnce()).increaseView();
        }

        @Test
        @DisplayName("조회수 증가 실패 테스트 (게시글이 존재하지 않는 경우)")
        void increaseError1() {
            given(postRepository.findById(postId))
                    .willReturn(Optional.empty());

            AppException exception = assertThrows(AppException.class, () -> postService.increaseView(postId, httpServletRequest, httpServletResponse));
            assertThat(exception.getErrorCode()).isEqualTo(POST_NOT_FOUND);

            verify(postRepository, atLeastOnce()).findById(postId);
        }

        @Test
        @DisplayName("조회수 증가 실패 테스트 (쿠키는 존재하나, 해당 게시글 방문 이력이 쿠키에 있는 경우)")
        void increaseError2() {
            given(postRepository.findById(postId))
                    .willReturn(Optional.of(mockPost));
            given(CookieUtil.getCookie(any(), anyString()))
                    .willReturn(viewHistory);
            given(TextParsingUtil.parsingViewHistory(viewHistory))
                    .willReturn(new HashSet<>(List.of(String.valueOf(postId))));

            assertDoesNotThrow(() -> postService.increaseView(postId, httpServletRequest, httpServletResponse));

            verify(postRepository, atLeastOnce()).findById(postId);
            verify(mockPost, never()).increaseView(); //increaseView 메서드 실행되지 않음
        }


    }
}