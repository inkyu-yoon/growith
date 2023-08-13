package com.growith.global.specification;

import com.growith.domain.comment.dto.*;
import com.growith.domain.likes.dto.LikeResponse;
import com.growith.domain.post.Category;
import com.growith.domain.post.dto.*;
import com.growith.global.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


public interface PostApiSpecification {

    @Tag(name = "Post", description = "게시글 관련 API")
    @Operation(summary = "게시글 조회", description = "💡카테고리와 상관없이 모든 게시글을 페이지로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"content\":[{\"postId\":1,\"title\":\"title\",\"content\":\"content\",\"category\":\"category\",\"userId\":1,\"nickName\":\"nickName\",\"userName\":\"userName\",\"createdDate\":\"2023/04/27 21:14\",\"lastModifiedDate\":\"2023/04/27 21:14\",\"imageUrl\":\"imageUrl\",\"view\":0,\"totalNumOfComments\":0,\"totalNumOfLikes\":0},{\"postId\":2,\"title\":\"title\",\"content\":\"content\",\"category\":\"category\",\"userId\":1,\"nickName\":\"nickName\",\"userName\":\"userName\",\"createdDate\":\"2023/05/13 00:15\",\"lastModifiedDate\":\"2023/05/13 00:15\",\"imageUrl\":\"imageUrl\",\"view\":0,\"totalNumOfComments\":0,\"totalNumOfLikes\":0}],\"pageable\":{\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"offset\":0,\"pageNumber\":0,\"pageSize\":20,\"unpaged\":false,\"paged\":true},\"totalPages\":1,\"totalElements\":2,\"last\":true,\"size\":20,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"numberOfElements\":2,\"first\":true,\"empty\":false}}")}, schema = @Schema(implementation = Response.class))),
    })
    @GetMapping
    ResponseEntity<Response<Page<PostGetResponse>>> getAll(Pageable pageable);


    @Tag(name = "Post", description = "게시글 관련 API")
    @Operation(summary = "게시글 작성", description = "<strong>🔑JWT 필요</strong><br>💡게시글을 작성합니다.<br>🚨가입된 회원이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"id\":1,\"nickName\":\"nickName\",\"blog\":\"blog\",\"email\":\"email\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"가입된 회원이 아닙니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PostMapping
    ResponseEntity<Response<PostResponse>> create(Authentication authentication, @Validated @RequestBody PostCreateRequest requestDto, BindingResult br);

    @Tag(name = "Post", description = "게시글 관련 API")
    @Operation(summary = "게시글 단건 조회", description = "💡게시글을 단건 조회합니다.<br>🚨게시글이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"postId\":1,\"title\":\"title\",\"content\":\"content\",\"category\":\"category\",\"userId\":1,\"nickName\":\"nickName\",\"userName\":\"userName\",\"createdDate\":\"2023/04/27 21:14\",\"lastModifiedDate\":\"2023/04/27 21:14\",\"imageUrl\":\"imageUrl\",\"view\":0,\"totalNumOfComments\":0,\"totalNumOfLikes\":0}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (게시글이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"해당 게시글을 찾을 수 없습니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @GetMapping("/{postId}")
    ResponseEntity<Response<PostGetResponse>> get(@PathVariable(name = "postId") Long postId);

    @Tag(name = "Post", description = "게시글 관련 API")
    @Operation(summary = "게시글 카테고리 별 조회", description = "💡게시글을 카테고리 별로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"postId\":1,\"title\":\"title\",\"content\":\"content\",\"category\":\"category\",\"userId\":1,\"nickName\":\"nickName\",\"userName\":\"userName\",\"createdDate\":\"2023/04/27 21:14\",\"lastModifiedDate\":\"2023/04/27 21:14\",\"imageUrl\":\"imageUrl\",\"view\":0,\"totalNumOfComments\":0,\"totalNumOfLikes\":0}}")}, schema = @Schema(implementation = Response.class)))
    })
    @GetMapping("/categories")
    ResponseEntity<Response<Page<PostGetListResponse>>> getAllByCategory(@RequestParam Category category, Pageable pageable);

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
    ResponseEntity<Response<PostResponse>> delete(@PathVariable(name = "postId") Long postId, Authentication authentication);

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
    ResponseEntity<Response<PostResponse>> update(Authentication authentication, @PathVariable(name = "postId") Long postId, @Validated @RequestBody PostUpdateRequest requestDto, BindingResult br);

    @Tag(name = "Comment", description = "댓글 관련 API")
    @Operation(summary = "댓글 작성", description = "<strong>🔑JWT 필요</strong><br>💡댓글을 작성합니다.<br>🚨가입된 회원이 존재하지 않을 시 · 게시글이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"commentId\":1,\"comment\":\"comment\",\"postId\":1,\"fromUserId\":1}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (가입된 회원이 존재하지 않을 시 · 게시글이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"해당 게시글을 찾을 수 없습니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PostMapping("/{postId}/comments")
    ResponseEntity<Response<CommentResponse>> createComment(Authentication authentication, @PathVariable(name = "postId") Long postId, @Validated @RequestBody CommentCreateRequest requestDto, BindingResult br);

    @Tag(name = "Comment", description = "댓글 관련 API")
    @Operation(summary = "댓글 조회", description = "💡postId에 등록된 댓글을 모두 조회합니다.<br>🚨게시글이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":[{\"commentId\":1,\"comment\":\"comment\",\"userName\":\"userName\",\"createdDate\":\"1초전\",\"imageUrl\":\"imageUrl\",\"replies\":[],\"replySize\":0}]}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (게시글이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"해당 게시글을 찾을 수 없습니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @GetMapping("/{postId}/comments")
    ResponseEntity<Response<List<CommentGetResponse>>> getAllComments(@PathVariable(name = "postId") Long postId);

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
    public ResponseEntity<Response<CommentResponse>> deleteComments(Authentication authentication, @PathVariable(name = "postId") Long postId, @PathVariable(name = "commentId") Long commentId);

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
    ResponseEntity<Response<CommentResponse>> updateComments(Authentication authentication, @PathVariable(name = "postId") Long postId, @PathVariable(name = "commentId") Long commentId, @Validated @RequestBody CommentUpdateRequest requestDto, BindingResult br);

    @Tag(name = "Comment", description = "댓글 관련 API")
    @Operation(summary = "댓글의 댓글 작성", description = "<strong>🔑JWT 필요</strong><br>💡댓글의 댓글을 작성합니다.<br>🚨가입된 회원이 존재하지 않을 시 · 게시글이 존재하지 않을 시 · 댓글이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"commentId\":1,\"fromUserId\":1}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (가입된 회원이 존재하지 않을 시 · 게시글이 존재하지 않을 시 · 댓글이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"해당 게시글을 찾을 수 없습니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PostMapping("/{postId}/comments/{commentId}")
    ResponseEntity<Response<CommentReplyResponse>> createCommentReply(Authentication authentication, @PathVariable(name = "postId") Long postId, @PathVariable(name = "commentId") Long commentId, @Validated @RequestBody CommentCreateRequest requestDto, BindingResult br);

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
    ResponseEntity<Response<LikeResponse>> changePostLike(Authentication authentication, @PathVariable(name = "postId") Long postId);
}
