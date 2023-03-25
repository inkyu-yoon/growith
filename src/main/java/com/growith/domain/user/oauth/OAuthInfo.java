package com.growith.domain.user.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthInfo {

    @JsonProperty("access_token")
    private String accessToken;

}
