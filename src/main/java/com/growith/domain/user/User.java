package com.growith.domain.user;


import com.growith.domain.BaseEntity;
import com.growith.domain.user.dto.UserGetMyPageResponse;
import com.growith.domain.user.dto.UserGetResponse;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_date is NULL")
@SQLDelete(sql = "UPDATE USER SET deleted_at = current_timestamp WHERE user_id = ?")
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


}
