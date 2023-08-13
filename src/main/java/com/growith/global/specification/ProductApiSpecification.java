package com.growith.global.specification;

import com.growith.domain.product.dto.ProductAddRequest;
import com.growith.domain.product.dto.ProductGetResponse;
import com.growith.domain.product.dto.ProductResponse;
import com.growith.domain.product.dto.ProductUpdateRequest;
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

public interface ProductApiSpecification {


    @Tag(name = "Product", description = "상품 관련 API")
    @Operation(summary = "상품 정보 등록", description = "<strong>🔑JWT 필요 & Admin 권한만 가능</strong><br>💡관리자가 상품 정보를 등록한다.<br>🚨가입된 회원이 존재하지 않을 시 · 관리자 계정이 아닌 경우 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"productId\":1,\"name\":\"name\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (가입된 회원이 존재하지 않을 시)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"가입된 회원이 아닙니다.\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "401", description = "❌ ERROR (관리자 권한이 아닌 경우)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"관리자만 요청할 수 있습니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PostMapping
    ResponseEntity<Response<ProductResponse>> create(Authentication authentication, @Validated @RequestBody ProductAddRequest requestDto, BindingResult br);

    @Tag(name = "Product", description = "상품 관련 API")
    @Operation(summary = "상품 정보 단건 조회", description = "💡상품 정보를 단건 조회한다.<br>🚨등록된 상품 정보가 존재하지 않는 경우 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"productId\":1,\"name\":\"name\",\"quantity\":1,\"price\":1,\"imageUrl\":\"imageUrl\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (등록된 상품 정보가 존재하지 않는 경우)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"해당 상품 정보를 찾을 수 없습니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @GetMapping("/{productId}")
    ResponseEntity<Response<ProductGetResponse>> getProduct(@PathVariable(name = "productId") Long productId);

    @Tag(name = "Product", description = "상품 관련 API")
    @Operation(summary = "상품 정보 페이지 단위 조회", description = "💡상품 정보를 페이지 단위로 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"content\":[{\"productId\":1,\"name\":\"string\",\"quantity\":1,\"price\":1,\"imageUrl\":\"string\"}],\"pageable\":{\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"offset\":0,\"pageSize\":20,\"pageNumber\":0,\"paged\":true,\"unpaged\":false},\"totalElements\":1,\"totalPages\":1,\"last\":true,\"size\":20,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"numberOfElements\":1,\"first\":true,\"empty\":false}}")}, schema = @Schema(implementation = Response.class)))
    })
    @GetMapping
    ResponseEntity<Response<Page<ProductGetResponse>>> getProducts(Pageable pageable);

    @Tag(name = "Product", description = "상품 관련 API")
    @Operation(summary = "상품 정보 삭제", description = "<strong>🔑JWT 필요 & Admin 권한만 가능</strong><br>💡관리자가 상품 정보를 삭제한다.<br>🚨가입된 회원이 존재하지 않을 시 · 관리자 계정이 아닌 경우 · 등록된 상품 정보가 존재하지 않는 경우 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"productId\":1,\"name\":\"name\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "401", description = "❌ ERROR (관리자 권한이 아닌 경우)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"관리자만 요청할 수 있습니다.\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (가입된 회원이 존재하지 않을 경우 · 등록된 상품 정보가 존재하지 않는 경우)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"해당 상품 정보를 찾을 수 없습니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @DeleteMapping("/{productId}")
    ResponseEntity<Response<ProductResponse>> deleteProduct(Authentication authentication, @PathVariable(name = "productId") Long productId);


    @Tag(name = "Product", description = "상품 관련 API")
    @Operation(summary = "상품 정보 수정", description = "<strong>🔑JWT 필요 & Admin 권한만 가능</strong><br>💡관리자가 상품 정보를 수정한다.<br>🚨가입된 회원이 존재하지 않을 시 · 관리자 계정이 아닌 경우 · 등록된 상품 정보가 존재하지 않는 경우 에러 발생")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"SUCCESS\",\"result\":{\"productId\":1,\"name\":\"name\"}}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "401", description = "❌ ERROR (관리자 권한이 아닌 경우)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"관리자만 요청할 수 있습니다.\"}")}, schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "❌ ERROR (가입된 회원이 존재하지 않을 경우 · 등록된 상품 정보가 존재하지 않는 경우)", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {@ExampleObject(value = "{\"message\":\"ERROR\",\"result\":\"해당 상품 정보를 찾을 수 없습니다.\"}")}, schema = @Schema(implementation = Response.class)))
    })
    @PutMapping("/{productId}")
    ResponseEntity<Response<ProductResponse>> updateProduct(Authentication authentication, @PathVariable(name = "productId") Long productId, @Validated @RequestBody ProductUpdateRequest requestDto, BindingResult br);
}
