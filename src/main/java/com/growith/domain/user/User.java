package com.growith.domain.user;


import com.growith.domain.BaseEntity;
import com.growith.domain.user.dto.UserGetMyPageResponse;
import com.growith.domain.user.dto.UserGetResponse;
import com.growith.domain.user.dto.UserUpdateRequest;
import com.growith.domain.user.dto.UserUpdateResponse;
import com.growith.global.exception.AppException;
import com.growith.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;

import static com.growith.domain.user.UserRole.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_date is NULL")
@SQLDelete(sql = "UPDATE user SET deleted_date = current_timestamp WHERE id = ?")
public class User extends BaseEntity implements UserDetails {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;

    private String imageUrl;

    private String nickName;

    private String email;

    private String blog;

    private Long point;

    private String githubUrl;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.userRole.toString()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Builder
    public User(String userName, String imageUrl, String nickName, String email, String blog, Long point, UserRole userRole, String githubUrl) {
        Assert.hasText(userName, "userName must not be empty");
        Assert.hasText(imageUrl, "imageUrl must not be empty");
        Assert.hasText(nickName, "nickName must not be empty");
        Assert.notNull(userRole, "userRole must not be empty");
        Assert.hasText(githubUrl, "githubUrl must not be empty");

        this.userName = userName;
        this.imageUrl = imageUrl;
        this.nickName = nickName;
        this.email = email;
        this.blog = blog;
        this.point = point;
        this.userRole = userRole;
        this.githubUrl = githubUrl;
    }


    public UserGetResponse toUserGetResponse() {
        return UserGetResponse.builder()
                .id(this.id)
                .userName(this.userName)
                .imageUrl(this.imageUrl)
                .nickName(this.nickName)
                .email(this.email)
                .blog(this.blog)
                .githubUrl(this.githubUrl)
                .build();
    }

    public UserGetMyPageResponse toUserGetMyPageResponse() {
        return UserGetMyPageResponse.builder()
                .id(this.id)
                .userName(this.userName)
                .imageUrl(this.imageUrl)
                .nickName(this.nickName)
                .email(this.email)
                .blog(this.blog)
                .point(this.point)
                .githubUrl(this.githubUrl)
                .build();
    }


    /**
     * 요청하는 자가 본인이면 가능, 혹은 관리자는 본인이 아니어도 가능
     */
    public void checkAuth(String userName) {
        if (!this.userName.equals(userName) & !this.userRole.equals(ROLE_ADMIN)) {
            throw new AppException(ErrorCode.USER_NOT_MATCH);
        }
    }

    public void updateUserInfo(UserUpdateRequest requestDto) {
        this.blog = requestDto.getBlog();
        this.nickName = requestDto.getNickName();
        this.email = requestDto.getEmail();
    }

    public UserUpdateResponse toUserUpdateResponse() {
        return UserUpdateResponse.builder()
                .id(this.id)
                .blog(this.blog)
                .nickName(this.nickName)
                .build();
    }

    /**
     * 닉네임 변경 요청 시, 이미 자신이 쓰고 있는 닉네임인 경우는 제외하기 위해
     */
    public boolean checkNickName(UserUpdateRequest requestDto) {
        return this.nickName.equals(requestDto.getNickName());
    }
}
