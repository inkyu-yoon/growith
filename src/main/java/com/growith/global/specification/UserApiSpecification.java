package com.growith.global.specification;

import com.growith.domain.alarm.dto.AlarmGetListResponse;
import com.growith.domain.post.dto.PostGetListResponse;
import com.growith.domain.user.dto.UserGetMyPageResponse;
import com.growith.domain.user.dto.UserGetResponse;
import com.growith.domain.user.dto.UserUpdateRequest;
import com.growith.domain.user.dto.UserUpdateResponse;
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

public interface UserApiSpecification {


    @Tag(name = "User", description = "회원 관련 API")
    @Operation(summary = "회원 정보 조회", description = "💡userId에 해당하는 회원의 정보를 확인합니다.(포인트 정보는 조회 불가)<br>🚨가입된 회원이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"id\":1,\"userName\":\"userName\",\"imageUrl\":\"imageUrl\",\"nickName\":\"nickName\",\"email\":\"email\",\"blog\":\"blog\",\"githubUrl\":\"githubUrl\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "ERROR (이메일 중복 · 닉네임 중복)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"가입된 회원이 아닙니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @GetMapping("/{userId}")
    ResponseEntity<Response<UserGetResponse>> get(@PathVariable(name = "userId") Long userId);

    @Tag(name = "User", description = "회원 관련 API")
    @Operation(summary = "마이페이지 조회", description = "<strong>🔑JWT 필요</strong><br>💡회원 본인의 정보 (포인트 정보 포함) 을 확인합니다.<br>🚨가입된 회원이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"id\":1,\"userName\":\"userName\",\"imageUrl\":\"imageUrl\",\"nickName\":\"nickName\",\"email\":\"email\",\"blog\":\"blog\",\"point\":10000,\"githubUrl\":\"githubUrl\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"가입된 회원이 아닙니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @GetMapping("/mypage")
    ResponseEntity<Response<UserGetMyPageResponse>> getMyPage(Authentication authentication);

    @Tag(name = "User", description = "회원 관련 API")
    @Operation(summary = "회원 별 작성한 게시글 조회", description = "💡userId에 해당하는 회원이 작성하는 게시글을 페이지로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"content\":[{\"postId\":2,\"title\":\"title\",\"content\":\"content\",\"date\":\"30초전\",\"nickName\":\"nickName\",\"imageUrl\":\"imageUrl\",\"view\":0,\"numOfComments\":0,\"numOfLikes\":0},{\"postId\":1,\"title\":\"title\",\"content\":\"content\",\"date\":\"1일 전\",\"nickName\":\"nickName\",\"imageUrl\":\"imageUrl\",\"view\":0,\"numOfComments\":0,\"numOfLikes\":0}],\"pageable\":{\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"offset\":0,\"pageSize\":20,\"pageNumber\":0,\"paged\":true,\"unpaged\":false},\"last\":true,\"totalElements\":2,\"totalPages\":1,\"size\":20,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"first\":true,\"numberOfElements\":2,\"empty\":false}}")}, schema = @Schema(implementation = Response.class))),
    })
    @GetMapping("/{userId}/posts")
    ResponseEntity<Response<Page<PostGetListResponse>>> getAllByUser(@PathVariable Long userId,  Pageable pageable);

    @Tag(name = "User", description = "회원 관련 API")
    @Operation(summary = "회원 정보 수정", description = "<strong>🔑JWT 필요</strong><br>💡회원 본인의 정보 닉네임, 블로그 주소, 이메일 정보, 주소 정보를 갱신합니다.<br>🚨본인 정보 수정 요청이 아닐 시 · 가입된 회원이 존재하지 않을 시 · 이미 존재하는 닉네임으로 수정 요청 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"id\":1,\"nickName\":\"nickName\",\"blog\":\"blog\",\"email\":\"email\",\"roadNameAddress\":\"roadNameAddress\",\"detailedAddress\":\"detailedAddress\",\"postalCode\":\"postalCode\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "401", description = "❌ ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"본인만 접근할 수 있습니다.\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"가입된 회원이 아닙니다.\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "409", description = "❌ ERROR (이미 존재하는 닉네임으로 수정 요청 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"이미 존재하는 닉네임입니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PatchMapping("/{userId}")
    ResponseEntity<Response<UserUpdateResponse>> update(Authentication authentication, @PathVariable(name = "userId") Long userId, @Validated @RequestBody UserUpdateRequest requestDto, BindingResult br);


    @Tag(name = "Alarm", description = "알림 관련 API")
    @Operation(summary = "회원 알림 조회", description = "<strong>🔑JWT 필요</strong><br>💡회원 본인의 모든 알림 정보를 최신순으로 조회한다.<br>🚨가입된 회원이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":[{\"alarmId\":2,\"fromUserNickName\":\"buinq\",\"postName\":\"postName\",\"postId\":1,\"text\":\"댓글을 달았습니다.\",\"createdAt\":\"1분 전\"},{\"alarmId\":1,\"fromUserNickName\":\"buinq\",\"postName\":\"postName\",\"postId\":1,\"text\":\"좋아요를 눌렀습니다.\",\"createdAt\":\"1분 전\"}]}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"가입된 회원이 아닙니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @GetMapping("/alarms")
    ResponseEntity<Response<List<AlarmGetListResponse>>> getAlarms(Authentication authentication);

    @Tag(name = "Alarm", description = "알림 관련 API")
    @Operation(summary = "회원 알림 삭제", description = "<strong>🔑JWT 필요</strong><br>💡회원 본인의 알림을 단건으로 삭제한다.<br>🚨가입된 회원이 존재하지 않을 시 · 알림이 존재하지 않을 시 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":\"complete\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (가입된 회원이 존재하지 않을 시 · 알림이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"가입된 회원이 아닙니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @DeleteMapping("/alarms/{alarmId}")
    ResponseEntity<Response<String>> deleteAlarm(Authentication authentication, @PathVariable(name = "alarmId") Long alarmId);

}
