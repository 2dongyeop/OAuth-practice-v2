package com.example.oauthpracticev2.member.persistence;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserProfile {

    /** OAuth 플랫폼으로부터 정보를 받아올 때 사용할 DTO */
    private final String oauthId;
    private final String name;
    private final String email;
    private final String imageUrl;

    @Builder
    public UserProfile(String oauthId, String name, String email, String imageUrl) {
        this.oauthId = oauthId;
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
    }

    public Member toMember() {
        return Member.builder()
                .oauthId(oauthId)
                .email(email)
                .name(name)
                .imageUrl(imageUrl)
                .role(Role.GUEST)
                .build();
    }
}
