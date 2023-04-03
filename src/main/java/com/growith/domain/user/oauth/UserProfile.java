package com.growith.domain.user.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.growith.domain.user.User;
import com.growith.domain.user.UserRole;
import lombok.*;

import java.util.UUID;

@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {

    @JsonProperty("login")
    private String userName;
    @JsonProperty("avatar_url")
    private String imageUrl;
    private String blog;
    @JsonProperty("html_url")
    private String githubUrl;

    public User toEntity() {
        return User.builder()
                .userRole(UserRole.ROLE_USER)
                .point(0L)
                .blog(this.blog)
                .email("")
                .imageUrl(this.imageUrl)
                .userName(this.userName)
                .nickName(this.userName)
                .githubUrl(this.githubUrl)
                .build();
    }
}
