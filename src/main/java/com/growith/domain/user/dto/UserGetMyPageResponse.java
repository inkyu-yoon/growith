package com.growith.domain.user.dto;

import com.growith.domain.user.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class UserGetMyPageResponse {
    private Long id;
    private String userName;
    private String imageUrl;
    private String nickName;
    private String email;
    private String blog;
    private Long point;
    private String githubUrl;
    private String roadNameAddress;
    private String detailedAddress;
    private String postalCode;

    public void setAddress(Address address) {
        this.roadNameAddress = address.getRoadNameAddress();
        this.detailedAddress = address.getDetailedAddress();
        this.postalCode = address.getPostalCode();
    }
}
