package com.growith.domain.user.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.growith.domain.user.User;
import com.growith.domain.user.UserRole;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
public class GithubProfile {
    private String email;
    private String name;
    @JsonProperty("avatar_url")
    private String imageUrl;
    private String blog;

    public User toEntity() {
        return User.builder().userRole(UserRole.ROLE_USER)
                .point(0L)
                .blog(this.blog)
                .email(this.email)
                .imageUrl(this.imageUrl)
                .name(this.name)
                .nickName(this.name)
                .build();
    }
}
