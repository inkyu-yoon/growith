package com.growith.controller;

import com.growith.domain.comment.dto.*;
import com.growith.domain.likes.dto.LikeResponse;
import com.growith.domain.post.Category;
import com.growith.domain.post.dto.*;
import com.growith.global.Response;
import com.growith.service.CommentService;
import com.growith.service.PostLikeService;
import com.growith.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.cloud.contract.spec.internal.HttpStatus.CREATED;

@RestController
@Slf4j
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostApiController {

    private final PostService postService;
    private final CommentService commentService;
    private final PostLikeService postLikeService;

    @Tag(name = "Post", description = "게시글 관련 API")
    @Operation(summary = "게시글 조회", description = "💡카테고리와 상관없이 모든 게시글을 페이지로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"content\":[{\"postId\":1,\"title\":\"title\",\"content\":\"content\",\"category\":\"category\",\"userId\":1,\"nickName\":\"nickName\",\"userName\":\"userName\",\"createdDate\":\"2023/04/27 21:14\",\"lastModifiedDate\":\"2023/04/27 21:14\",\"imageUrl\":\"imageUrl\",\"view\":0,\"totalNumOfComments\":0,\"totalNumOfLikes\":0},{\"postId\":2,\"title\":\"title\",\"content\":\"content\",\"category\":\"category\",\"userId\":1,\"nickName\":\"nickName\",\"userName\":\"userName\",\"createdDate\":\"2023/05/13 00:15\",\"lastModifiedDate\":\"2023/05/13 00:15\",\"imageUrl\":\"imageUrl\",\"view\":0,\"totalNumOfComments\":0,\"totalNumOfLikes\":0}],\"pageable\":{\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"offset\":0,\"pageNumber\":0,\"pageSize\":20,\"unpaged\":false,\"paged\":true},\"totalPages\":1,\"totalElements\":2,\"last\":true,\"size\":20,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"numberOfElements\":2,\"first\":true,\"empty\":false}}")}, schema = @Schema(implementation = Response.class))),
    })
    @GetMapping
    public ResponseEntity<Response<Page<PostGetResponse>>> getAll(Pageable pageable) {
        Page<PostGetResponse> response = postService.getAllPosts(pageable);
        return ResponseEntity.ok(Response.success(response));
    }


    @Tag(name = "Post", description = "게시글 관련 API")
    @Operation(summary = "게시글 작성", description = "<strong>🔑JWT 필요</strong><br>💡게시글을 작성합니다.<br>🚨가입된 회원이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"id\":1,\"nickName\":\"nickName\",\"blog\":\"blog\",\"email\":\"email\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"가입된 회원이 아닙니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PostMapping
    public ResponseEntity<Response<PostResponse>> create(Authentication authentication, @Validated @RequestBody PostCreateRequest requestDto, BindingResult br) {
        String userName = authentication.getName();
        PostResponse response = postService.createPost(userName, requestDto);
        return ResponseEntity.status(CREATED).body(Response.success(response));
    }

    @Tag(name = "Post", description = "게시글 관련 API")
    @Operation(summary = "게시글 단건 조회", description = "💡게시글을 단건 조회합니다.<br>🚨게시글이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"postId\":1,\"title\":\"title\",\"content\":\"content\",\"category\":\"category\",\"userId\":1,\"nickName\":\"nickName\",\"userName\":\"userName\",\"createdDate\":\"2023/04/27 21:14\",\"lastModifiedDate\":\"2023/04/27 21:14\",\"imageUrl\":\"imageUrl\",\"view\":0,\"totalNumOfComments\":0,\"totalNumOfLikes\":0}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (게시글이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"해당 게시글을 찾을 수 없습니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @GetMapping("/{postId}")
    public ResponseEntity<Response<PostGetResponse>> get(@PathVariable(name = "postId") Long postId) {
        PostGetResponse response = postService.getPost(postId);
        return ResponseEntity.ok(Response.success(response));
    }

    @Tag(name = "Post", description = "게시글 관련 API")
    @Operation(summary = "게시글 카테고리 별 조회", description = "💡게시글을 카테고리 별로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"postId\":1,\"title\":\"title\",\"content\":\"content\",\"category\":\"category\",\"userId\":1,\"nickName\":\"nickName\",\"userName\":\"userName\",\"createdDate\":\"2023/04/27 21:14\",\"lastModifiedDate\":\"2023/04/27 21:14\",\"imageUrl\":\"imageUrl\",\"view\":0,\"totalNumOfComments\":0,\"totalNumOfLikes\":0}}")}, schema = @Schema(implementation = Response.class)))
    })
    @GetMapping("/categories")
    public ResponseEntity<Response<Page<PostGetListResponse>>> getAllByCategory(@RequestParam Category category, Pageable pageable) {
        Page<PostGetListResponse> response = postService.getAllPostsByCategory(category, pageable);
        return ResponseEntity.ok(Response.success(response));
    }

    @Tag(name = "Post", description = "게시글 관련 API")
    @Operation(summary = "게시글 삭제", description = "<strong>🔑JWT 필요</strong><br>💡게시글을 삭제합니다.<br>🚨가입된 회원이 존재하지 않을 시 · 게시글이 존재하지 않을 시 · 본인 게시글 삭제 요청이 아닐시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"postId\":1}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "401", description = "❌ ERROR (본인 게시글 삭제 요청이 아닐시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"본인만 접근할 수 있습니다.\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (가입된 회원이 존재하지 않을 시 · 게시글이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"해당 게시글을 찾을 수 없습니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @DeleteMapping("/{postId}")
    public ResponseEntity<Response<PostResponse>> delete(@PathVariable(name = "postId") Long postId, Authentication authentication) {
        String userName = authentication.getName();
        PostResponse response = postService.deletePost(postId, userName);
        return ResponseEntity.ok(Response.success(response));
    }

    @Tag(name = "Post", description = "게시글 관련 API")
    @Operation(summary = "게시글 수정", description = "<strong>🔑JWT 필요</strong><br>💡게시글을 수정합니다.<br>🚨가입된 회원이 존재하지 않을 시 · 게시글이 존재하지 않을 시 · 본인 게시글 수정 요청이 아닐시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"postId\":1}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "401", description = "❌ ERROR (본인 게시글 수정 요청이 아닐시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"본인만 접근할 수 있습니다.\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (가입된 회원이 존재하지 않을 시 · 게시글이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"해당 게시글을 찾을 수 없습니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PutMapping("/{postId}")
    public ResponseEntity<Response<PostResponse>> update(Authentication authentication, @PathVariable(name = "postId") Long postId, @Validated @RequestBody PostUpdateRequest requestDto, BindingResult br) {
        String userName = authentication.getName();
        PostResponse response = postService.updatePost(postId, userName, requestDto);
        return ResponseEntity.ok(Response.success(response));
    }

    @Tag(name = "Comment", description = "댓글 관련 API")
    @Operation(summary = "댓글 작성", description = "<strong>🔑JWT 필요</strong><br>💡댓글을 작성합니다.<br>🚨가입된 회원이 존재하지 않을 시 · 게시글이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"commentId\":1,\"comment\":\"comment\",\"postId\":1,\"fromUserId\":1}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (가입된 회원이 존재하지 않을 시 · 게시글이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"해당 게시글을 찾을 수 없습니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PostMapping("/{postId}/comments")
    public ResponseEntity<Response<CommentResponse>> createComment(Authentication authentication, @PathVariable(name = "postId") Long postId, @Validated @RequestBody CommentCreateRequest requestDto, BindingResult br) {
        String userName = authentication.getName();
        CommentResponse response = commentService.createComment(postId, userName, requestDto);

        return ResponseEntity.ok(Response.success(response));
    }

    @Tag(name = "Comment", description = "댓글 관련 API")
    @Operation(summary = "댓글 조회", description = "💡postId에 등록된 댓글을 모두 조회합니다.<br>🚨게시글이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":[{\"commentId\":1,\"comment\":\"comment\",\"userName\":\"userName\",\"createdDate\":\"1초전\",\"imageUrl\":\"imageUrl\",\"replies\":[],\"replySize\":0}]}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (게시글이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"해당 게시글을 찾을 수 없습니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @GetMapping("/{postId}/comments")
    public ResponseEntity<Response<List<CommentGetResponse>>> getAllComments(@PathVariable(name = "postId") Long postId) {
        List<CommentGetResponse> response = commentService.getAllComments(postId);

        return ResponseEntity.ok(Response.success(response));
    }

    @Tag(name = "Comment", description = "댓글 관련 API")
    @Operation(summary = "댓글 삭제", description = "<strong>🔑JWT 필요</strong><br>💡댓글을 삭제합니다.<br>🚨가입된 회원이 존재하지 않을 시 · 게시글이 존재하지 않을 시 · 댓글이 존재하지 않을 시 · 본인 댓글 삭제 요청이 아닐시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"commentId\":1,\"comment\":\"comment\",\"postId\":1,\"fromUserId\":1}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "401", description = "❌ ERROR (본인 댓글 삭제 요청이 아닐시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"본인만 접근할 수 있습니다.\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (가입된 회원이 존재하지 않을 시 · 게시글이 존재하지 않을 시 · 댓글이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"해당 게시글을 찾을 수 없습니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Response<CommentResponse>> deleteComments(Authentication authentication, @PathVariable(name = "postId") Long postId, @PathVariable(name = "commentId") Long commentId) {
        String userName = authentication.getName();
        CommentResponse response = commentService.deleteComment(postId, userName, commentId);

        return ResponseEntity.ok(Response.success(response));
    }

    @Tag(name = "Comment", description = "댓글 관련 API")
    @Operation(summary = "댓글 수정", description = "<strong>🔑JWT 필요</strong><br>💡댓글을 수정합니다.<br>🚨가입된 회원이 존재하지 않을 시 · 게시글이 존재하지 않을 시 · 댓글이 존재하지 않을 시 · 본인 댓글 수정 요청이 아닐시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"commentId\":1,\"comment\":\"comment\",\"postId\":1,\"fromUserId\":1}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "401", description = "❌ ERROR (본인 댓글 수정 요청이 아닐시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"본인만 접근할 수 있습니다.\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (가입된 회원이 존재하지 않을 시 · 게시글이 존재하지 않을 시 · 댓글이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"해당 게시글을 찾을 수 없습니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Response<CommentResponse>> updateComments(Authentication authentication, @PathVariable(name = "postId") Long postId, @PathVariable(name = "commentId") Long commentId, @Validated @RequestBody CommentUpdateRequest requestDto, BindingResult br) {
        String userName = authentication.getName();
        CommentResponse response = commentService.updateComment(postId, userName, commentId, requestDto);

        return ResponseEntity.ok(Response.success(response));
    }

    @Tag(name = "Comment", description = "댓글 관련 API")
    @Operation(summary = "댓글의 댓글 작성", description = "<strong>🔑JWT 필요</strong><br>💡댓글의 댓글을 작성합니다.<br>🚨가입된 회원이 존재하지 않을 시 · 게시글이 존재하지 않을 시 · 댓글이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"commentId\":1,\"fromUserId\":1}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (가입된 회원이 존재하지 않을 시 · 게시글이 존재하지 않을 시 · 댓글이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"해당 게시글을 찾을 수 없습니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PostMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Response<CommentReplyResponse>> createCommentReply(Authentication authentication, @PathVariable(name = "postId") Long postId, @PathVariable(name = "commentId") Long commentId, @Validated @RequestBody CommentCreateRequest requestDto, BindingResult br) {
        String userName = authentication.getName();
        CommentReplyResponse response = commentService.createCommentReply(postId, userName, commentId, requestDto);

        return ResponseEntity.ok(Response.success(response));
    }

    @Tag(name = "Like", description = "좋아요 관련 API")
    @Operation(summary = "좋아요 입력", description = "<strong>🔑JWT 필요</strong><br>💡게시글에 좋아요를 입력합니다.<br>🚨가입된 회원이 존재하지 않을 시 · 게시글이 존재하지 않을 시 · 본인 게시글에 좋아요를 입력할 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"commentId\":1,\"fromUserId\":1,\"isHistoryFound\":\"false\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "401", description = "❌ ERROR (본인 게시글에 좋아요를 입력할 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"본인 게시글에 좋아요를 누를 수 없습니다.\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (가입된 회원이 존재하지 않을 시 · 게시글이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"해당 게시글을 찾을 수 없습니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PostMapping("/{postId}/likes")
    public ResponseEntity<Response<LikeResponse>> changePostLike(Authentication authentication, @PathVariable(name = "postId") Long postId) {
        String userName = authentication.getName();
        LikeResponse response = postLikeService.addLike(userName, postId);

        return ResponseEntity.ok(Response.success(response));
    }
}
