package com.growith.controller;

import com.google.gson.Gson;
import com.growith.domain.comment.dto.*;
import com.growith.domain.likes.dto.LikeResponse;
import com.growith.domain.post.Category;
import com.growith.domain.post.dto.*;
import com.growith.domain.user.User;
import com.growith.domain.user.UserRole;
import com.growith.global.aop.BindingCheck;
import com.growith.global.config.SecurityConfig;
import com.growith.global.exception.AppException;
import com.growith.global.exception.ErrorCode;
import com.growith.global.util.JwtUtil;
import com.growith.service.CommentService;
import com.growith.service.PostLikeService;
import com.growith.service.PostService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static com.growith.global.exception.ErrorCode.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = PostApiController.class)
@EnableAspectJAutoProxy
@Import({SecurityConfig.class, BindingCheck.class})
class PostApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    WebApplicationContext wac;

    @Autowired
    Gson gson;

    @MockBean
    PostService postService;
    @MockBean
    CommentService commentService;
    @MockBean
    UserDetailsService userDetailsService;
    @MockBean
    PostLikeService postLikeService;


    @Value("${jwt.secret}")
    String secretKey;

    Long postId;
    Long commentId;
    String userName;
    String comment;
    String token;
    Cookie cookie;
    PostResponse postResponse;
    PostGetResponse postGetresponse;
    CommentResponse commentResponse;
    CommentCreateRequest commentCreateRequest;
    CommentGetResponse commentGetResponse;
    CommentUpdateRequest commentUpdateRequest;
    LikeResponse likeResponse;
    CommentReplyResponse commentReplyResponse;

    @BeforeEach
    public void setUpMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        given(userDetailsService.loadUserByUsername(anyString()))
                .willReturn(User.builder()
                        .userName("userName")
                        .email("email")
                        .point(0L)
                        .blog("blog")
                        .githubUrl("githubUrl")
                        .nickName("nickName")
                        .imageUrl("imageUrl")
                        .userRole(UserRole.ROLE_USER)
                        .build());

        postId = 1L;
        userName = "userName";
        token = JwtUtil.createToken(1L,userName, "ROLE_USER", secretKey, 1000L * 60 * 60);
        cookie = new Cookie("jwt", token);
        commentId = 1L;
        comment = "comment";

        postResponse = PostResponse.builder()
                .postId(postId)
                .build();

        postGetresponse = PostGetResponse.builder()
                .postId(postId)
                .title("title")
                .content("content")
                .category(Category.QNA)
                .userId(1L)
                .nickName("nickName")
                .createdDate("createdDate")
                .lastModifiedDate("lastModifiedDate")
                .imageUrl("imageUrl")
                .view(0L)
                .build();

        commentResponse = CommentResponse.builder()
                .comment(comment)
                .commentId(commentId)
                .build();

        commentCreateRequest = CommentCreateRequest.builder()
                .comment("comment")
                .build();

        commentGetResponse = CommentGetResponse.builder()
                .commentId(commentId)
                .comment(comment)
                .createdDate("1일전")
                .imageUrl("imageUrl")
                .userName(userName)
                .replySize(1)
                .build();

        commentUpdateRequest = CommentUpdateRequest.builder()
                .comment(comment)
                .build();

        commentReplyResponse = CommentReplyResponse.builder()
                .commentId(commentId)
                .fromUserId(1L)
                .build();
        likeResponse = LikeResponse.builder()
                .postId(postId)
                .isHistoryFound(false)
                .build();
    }

    @Nested
    @DisplayName("게시글 조회 테스트")
    class GetPostTest {

        @Test
        @DisplayName("게시글 페이지 조회 성공 테스트")
        void getPageSuccess() throws Exception {

            List<PostGetResponse> posts = new ArrayList<>();
            posts.add(postGetresponse);

            Page<PostGetResponse> postsPage = new PageImpl<>(posts);

            given(postService.getAllPosts(any()))
                    .willReturn(postsPage);

            mockMvc.perform(get("/api/v1/posts"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.content").exists())
                    .andExpect(jsonPath("$.result.content[0].postId").value(1))
                    .andExpect(jsonPath("$.result.content[0].title").value("title"))
                    .andExpect(jsonPath("$.result.content[0].content").value("content"))
                    .andExpect(jsonPath("$.result.content[0].category").value("QNA"))
                    .andExpect(jsonPath("$.result.content[0].userId").value("1"))
                    .andExpect(jsonPath("$.result.content[0].nickName").value("nickName"))
                    .andExpect(jsonPath("$.result.content[0].createdDate").value("createdDate"))
                    .andExpect(jsonPath("$.result.content[0].lastModifiedDate").value("lastModifiedDate"))
                    .andExpect(jsonPath("$.result.content[0].view").value(0))
                    .andExpect(jsonPath("$.result.content[0].imageUrl").value("imageUrl"));
        }

        @Test
        @DisplayName("게시글 단건 조회 성공")
        void getPostSuccess() throws Exception {


            given(postService.getPost(postId))
                    .willReturn(postGetresponse);

            mockMvc.perform(get("/api/v1/posts/" + postId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.postId").value(1))
                    .andExpect(jsonPath("$.result.title").value("title"))
                    .andExpect(jsonPath("$.result.content").value("content"))
                    .andExpect(jsonPath("$.result.category").value("QNA"))
                    .andExpect(jsonPath("$.result.userId").value(1))
                    .andExpect(jsonPath("$.result.nickName").value("nickName"))
                    .andExpect(jsonPath("$.result.createdDate").value("createdDate"))
                    .andExpect(jsonPath("$.result.lastModifiedDate").value("lastModifiedDate"))
                    .andExpect(jsonPath("$.result.view").value(0))
                    .andExpect(jsonPath("$.result.imageUrl").value("imageUrl"));
        }

        @Test
        @DisplayName("게시글 단건 조회 실패 (게시글이 존재하지 않는 경우)")
        void getPostError() throws Exception {


            when(postService.getPost(postId))
                    .thenThrow(new AppException(POST_NOT_FOUND));

            mockMvc.perform(get("/api/v1/posts/" + postId))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

        @Test
        @DisplayName("게시글 카테고리별 리스트 조회 성공")
        void getPostsByCategorySuccess() throws Exception {

            PostGetListResponse postGetListResponse = PostGetListResponse
                    .builder()
                    .postId(postId)
                    .title("title")
                    .content("content")
                    .date("date")
                    .nickName("nickName")
                    .view(0L)
                    .build();

            List<PostGetListResponse> posts = new ArrayList<>();
            posts.add(postGetListResponse);

            Page<PostGetListResponse> postsPage = new PageImpl<>(posts);

            given(postService.getAllPostsByCategory(any(), any()))
                    .willReturn(postsPage);

            mockMvc.perform(get("/api/v1/posts/categories")
                            .param("category", "QNA"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.content").exists())
                    .andExpect(jsonPath("$.result.content[0].postId").value(1))
                    .andExpect(jsonPath("$.result.content[0].title").value("title"))
                    .andExpect(jsonPath("$.result.content[0].content").value("content"))
                    .andExpect(jsonPath("$.result.content[0].date").value("date"))
                    .andExpect(jsonPath("$.result.content[0].view").value(0))
                    .andExpect(jsonPath("$.result.content[0].nickName").value("nickName"));
        }

    }

    @Nested
    @DisplayName("게시글 생성 테스트")
    class CreatePostTest {
        PostCreateRequest request = new PostCreateRequest("title", "content", Category.QNA);

        @Test
        @DisplayName("게시글 생성 성공 테스트")
        void createPostSuccess() throws Exception {
            given(postService.createPost(anyString(), any()))
                    .willReturn(postResponse);

            mockMvc.perform(post("/api/v1/posts")
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(request)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.postId").value(1));

        }

        @Test
        @DisplayName("게시글 생성 에러 테스트 (회원을 찾을 수 없는 경우)")
        void createPostError() throws Exception {

            when(postService.createPost(anyString(), any(PostCreateRequest.class)))
                    .thenThrow(new AppException(USER_NOT_FOUND));

            mockMvc.perform(post("/api/v1/posts")
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());

        }

        @Test
        @DisplayName("게시글 생성 에러 테스트 (BindingError)")
        void createPostError2() throws Exception {

            PostCreateRequest request = new PostCreateRequest(null, "content", Category.QNA);

            mockMvc.perform(post("/api/v1/posts")
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());

        }
    }

    @Nested
    @DisplayName("게시글 수정 테스트")
    class UpdatePostTest {
        PostUpdateRequest request = new PostUpdateRequest("title", "content", Category.QNA);

        @Test
        @DisplayName("게시글 수정 성공 테스트")
        void updatePostSuccess() throws Exception {
            given(postService.updatePost(anyLong(), anyString(), any(PostUpdateRequest.class)))
                    .willReturn(postResponse);

            mockMvc.perform(put("/api/v1/posts/" + postId)
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.postId").value(1));
        }

        @Test
        @DisplayName("게시글 수정 에러 테스트 (게시글을 찾을 수 없는 경우)")
        void updatePostError1() throws Exception {

            when(postService.updatePost(anyLong(), anyString(), any(PostUpdateRequest.class)))
                    .thenThrow(new AppException(POST_NOT_FOUND));

            mockMvc.perform(put("/api/v1/posts/" + postId)
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());

        }

        @Test
        @DisplayName("게시글 수정 에러 테스트 (회원을 찾을 수 없는 경우)")
        void updatePostError2() throws Exception {

            when(postService.updatePost(anyLong(), anyString(), any(PostUpdateRequest.class)))
                    .thenThrow(new AppException(USER_NOT_FOUND));

            mockMvc.perform(put("/api/v1/posts/" + postId)
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());

        }

        @Test
        @DisplayName("게시글 수정 에러 테스트 (삭제 요청자가 본인이 아닌 경우)")
        void updatePostError3() throws Exception {

            when(postService.updatePost(anyLong(), anyString(), any(PostUpdateRequest.class)))
                    .thenThrow(new AppException(USER_NOT_MATCH));

            mockMvc.perform(put("/api/v1/posts/" + postId)
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());

        }

        @Test
        @DisplayName("게시글 수정 에러 테스트 (BindingError 발생)")
        void updatePostError4() throws Exception {

            PostUpdateRequest request = new PostUpdateRequest("null", "content", null);


            mockMvc.perform(put("/api/v1/posts/" + postId)
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());

        }
    }

    @Nested
    @DisplayName("게시글 삭제 테스트")
    class DeletePostTest {
        @Test
        @DisplayName("게시글 삭제 성공 테스트")
        void deletePostSuccess() throws Exception {
            given(postService.deletePost(anyLong(), anyString()))
                    .willReturn(postResponse);
            mockMvc.perform(delete("/api/v1/posts/" + postId)
                            .cookie(cookie))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.postId").value(1));
        }

        @Test
        @DisplayName("게시글 삭제 에러 테스트 (게시글을 찾을 수 없는 경우)")
        void deletePostError1() throws Exception {

            when(postService.deletePost(anyLong(), anyString()))
                    .thenThrow(new AppException(POST_NOT_FOUND));

            mockMvc.perform(delete("/api/v1/posts/" + postId)
                            .cookie(cookie))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());

        }

        @Test
        @DisplayName("게시글 삭제 에러 테스트 (회원을 찾을 수 없는 경우)")
        void deletePostError2() throws Exception {

            when(postService.deletePost(anyLong(), anyString()))
                    .thenThrow(new AppException(USER_NOT_FOUND));

            mockMvc.perform(delete("/api/v1/posts/" + postId)
                            .cookie(cookie))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());

        }

        @Test
        @DisplayName("게시글 삭제 에러 테스트 (삭제 요청자가 본인이 아닌 경우)")
        void deletePostError3() throws Exception {

            when(postService.deletePost(anyLong(), anyString()))
                    .thenThrow(new AppException(USER_NOT_MATCH));

            mockMvc.perform(delete("/api/v1/posts/" + postId)
                            .cookie(cookie))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());

        }
    }

    @Nested
    @DisplayName("댓글 등록 테스트")
    class writeComment {

        @Test
        @DisplayName("댓글 등록 성공 테스트")
        void writeCommentSuccess() throws Exception {
            given(commentService.createComment(anyLong(), anyString(), any(CommentCreateRequest.class)))
                    .willReturn(commentResponse);

            mockMvc.perform(post("/api/v1/posts/" + postId + "/comments")
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(commentCreateRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.commentId").value(commentId))
                    .andExpect(jsonPath("$.result.comment").value(comment));
        }

        @Test
        @DisplayName("댓글 등록 실패 테스트(가입된 회원이 아닌 경우)")
        void writeCommentError1() throws Exception {
            when(commentService.createComment(anyLong(), anyString(), any(CommentCreateRequest.class)))
                    .thenThrow(new AppException(USER_NOT_FOUND));

            mockMvc.perform(post("/api/v1/posts/" + postId + "/comments")
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(commentCreateRequest)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

        @Test
        @DisplayName("댓글 등록 실패 테스트(게시글이 존재하지 않는 경우)")
        void writeCommentError2() throws Exception {
            when(commentService.createComment(anyLong(), anyString(), any(CommentCreateRequest.class)))
                    .thenThrow(new AppException(POST_NOT_FOUND));

            mockMvc.perform(post("/api/v1/posts/" + postId + "/comments")
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(commentCreateRequest)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }
    }

    @Nested
    @DisplayName("댓글 조회 테스트")
    class getAllComment {

        @Test
        @DisplayName("댓글 조회 성공 테스트")
        void getAllCommentSuccess() throws Exception {
            given(commentService.getAllComments(anyLong()))
                    .willReturn(List.of(commentGetResponse));

            mockMvc.perform(get("/api/v1/posts/" + postId + "/comments")
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(commentCreateRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result[0].commentId").value(commentId))
                    .andExpect(jsonPath("$.result[0].comment").value(comment))
                    .andExpect(jsonPath("$.result[0].userName").value(userName))
                    .andExpect(jsonPath("$.result[0].createdDate").exists())
                    .andExpect(jsonPath("$.result[0].imageUrl").exists())
                    .andExpect(jsonPath("$.result[0].replySize").exists());
        }

        @Test
        @DisplayName("댓글 조회 실패 (게시글이 존재하지 않는 경우)")
        void getAllCommentError() throws Exception {
            when(commentService.getAllComments(anyLong()))
                    .thenThrow(new AppException(POST_NOT_FOUND));

            mockMvc.perform(get("/api/v1/posts/" + postId + "/comments")
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(commentCreateRequest)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }
    }

    @Nested
    @DisplayName("댓글 삭제 테스트")
    class DeleteCommentTest {

        @Test
        @DisplayName("댓글 삭제 성공 테스트")
        void deleteCommentSuccess() throws Exception {
            given(commentService.deleteComment(anyLong(), anyString(), anyLong()))
                    .willReturn(commentResponse);

            mockMvc.perform(delete("/api/v1/posts/" + postId + "/comments/" + commentId)
                            .cookie(cookie))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.commentId").value(commentId))
                    .andExpect(jsonPath("$.result.comment").value(comment));
        }

        @Test
        @DisplayName("댓글 삭제 실패")
        void deleteCommentError() throws Exception {
            when(commentService.deleteComment(anyLong(), anyString(), anyLong()))
                    .thenThrow(new AppException(USER_NOT_FOUND));

            mockMvc.perform(delete("/api/v1/posts/" + postId + "/comments/" + commentId)
                            .cookie(cookie))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

    }

    @Nested
    @DisplayName("댓글 수정 테스트")
    class UpdateCommentTest {

        @Test
        @DisplayName("댓글 수정 성공 테스트")
        void updateCommentSuccess() throws Exception {
            given(commentService.updateComment(anyLong(), anyString(), anyLong(), any(CommentUpdateRequest.class)))
                    .willReturn(commentResponse);

            mockMvc.perform(put("/api/v1/posts/" + postId + "/comments/" + commentId)
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(commentUpdateRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.commentId").value(commentId))
                    .andExpect(jsonPath("$.result.comment").value(comment));
        }

        @Test
        @DisplayName("댓글 수정 실패")
        void updateCommentError() throws Exception {
            when(commentService.updateComment(anyLong(), anyString(), anyLong(), any(CommentUpdateRequest.class)))
                    .thenThrow(new AppException(USER_NOT_FOUND));

            mockMvc.perform(put("/api/v1/posts/" + postId + "/comments/" + commentId)
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(commentUpdateRequest)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }
    }

    @Nested
    @DisplayName("대댓글 등록 테스트")
    class writeCommentReply {

        @Test
        @DisplayName("대댓글 등록 성공 테스트")
        void writeCommentReplySuccess() throws Exception {
            given(commentService.createCommentReply(anyLong(), anyString(), anyLong(), any(CommentCreateRequest.class)))
                    .willReturn(commentReplyResponse);

            mockMvc.perform(post("/api/v1/posts/" + postId + "/comments/" + commentId)
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(commentCreateRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.commentId").value(commentId))
                    .andExpect(jsonPath("$.result.fromUserId").value(1));
        }

        @Test
        @DisplayName("대댓글 등록 실패 테스트")
        void writeCommentReplyError1() throws Exception {
            when(commentService.createCommentReply(anyLong(), anyString(), anyLong(), any(CommentCreateRequest.class)))
                    .thenThrow(new AppException(USER_NOT_FOUND));

            mockMvc.perform(post("/api/v1/posts/" + postId + "/comments/" + commentId)
                            .cookie(cookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(commentCreateRequest)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

    }

    @Nested
    @DisplayName("게시글 좋아요 테스트")
    class PostLikeTest {

        @Test
        @DisplayName("게시글 좋아요 성공 테스트")
        void PostLikeSuccess() throws Exception {
            given(postLikeService.addLike(anyString(), anyLong()))
                    .willReturn(likeResponse);

            mockMvc.perform(post("/api/v1/posts/" + postId + "/likes")
                            .cookie(cookie))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.postId").value(postId));
        }

        @Test
        @DisplayName("게시글 좋아요 실패 테스트 (가입된 회원이 아닌 경우)")
        void PostLikeError1() throws Exception {

            when(postLikeService.addLike(anyString(), anyLong()))
                    .thenThrow(new AppException(USER_NOT_FOUND));

            mockMvc.perform(post("/api/v1/posts/" + postId + "/likes")
                            .cookie(cookie))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }
        @Test
        @DisplayName("게시글 좋아요 실패 테스트 (게시글이 존재하지 않는 경우)")
        void PostLikeError2() throws Exception {

            when(postLikeService.addLike(anyString(), anyLong()))
                    .thenThrow(new AppException(POST_NOT_FOUND));

            mockMvc.perform(post("/api/v1/posts/" + postId + "/likes")
                            .cookie(cookie))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value("ERROR"))
                    .andExpect(jsonPath("$.result").exists());
        }

    }

}