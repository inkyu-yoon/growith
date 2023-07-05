package com.growith.domain.user;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Address {

    private String roadNameAddress;
    private String detailedAddress;
    private String postalCode;
}
