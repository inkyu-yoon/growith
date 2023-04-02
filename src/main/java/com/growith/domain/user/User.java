package com.growith.domain.user;


import com.growith.domain.BaseEntity;
import com.growith.domain.user.dto.UserGetMyPageResponse;
import com.growith.domain.user.dto.UserGetResponse;
import com.growith.domain.user.dto.UserUpdateRequest;
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
@SQLDelete(sql = "UPDATE USER SET deleted_date = current_timestamp WHERE id = ?")
public class User extends BaseEntity implements UserDetails {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String imageUrl;

    private String nickName;

    private String email;

    private String blog;

    private Long point;

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
        return this.email;
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
    public User(String name, String imageUrl, String nickName, String email, String blog, Long point, UserRole userRole) {
        Assert.hasText(name, "name must not be empty");
        Assert.hasText(imageUrl, "imageUrl must not be empty");
        Assert.hasText(nickName, "nickName must not be empty");
        Assert.hasText(email, "email must not be empty");
        Assert.notNull(userRole, "userRole must not be empty");

        this.name = name;
        this.imageUrl = imageUrl;
        this.nickName = nickName;
        this.email = email;
        this.blog = blog;
        this.point = point;
        this.userRole = userRole;
    }


    public UserGetResponse toUserGetResponse() {
        return UserGetResponse.builder()
                .id(this.id)
                .name(this.name)
                .imageUrl(this.imageUrl)
                .nickName(this.nickName)
                .email(this.email)
                .blog(this.blog)
                .build();
    }

    public UserGetMyPageResponse toUserGetMyPageResponse() {
        return UserGetMyPageResponse.builder()
                .id(this.id)
                .name(this.name)
                .imageUrl(this.imageUrl)
                .nickName(this.nickName)
                .email(this.email)
                .blog(this.blog)
                .point(this.point)
                .build();
    }


    /**
     * 요청하는 자가 본인이면 가능, 혹은 관리자는 본인이 아니어도 가능
     */
    public void checkAuth(String email) {
        if (!this.email.equals(email) & !this.userRole.equals(ROLE_ADMIN)) {
            throw new AppException(ErrorCode.USER_NOT_MATCH);
        }
    }

    public void updateUserInfo(UserUpdateRequest requestDto) {
        String blog = requestDto.getBlog();
        String nickName = requestDto.getNickName();
        if (StringUtils.hasText(blog)) {
            this.blog = blog;
        }
        if (StringUtils.hasText(nickName)) {
            this.nickName = nickName;
        }
    }
}
