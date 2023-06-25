package com.example.oauthpracticev2.member.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 아래에는 4개 필드는 OAuth 로그인을 통해 OAuth 자체에서 받아올 정보를 명시 */
    private String oauthId;
    private String name;
    private String email;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Role role;

    public Member(Long id, String oauthId, String name, String email, String imageUrl, Role role) {
        this.id = id;
        this.oauthId = oauthId;
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
        this.role = role;
    }

    public Member(String oauthId, String name, String email, String imageUrl, Role role) {
        this.oauthId = oauthId;
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
        this.role = role;
    }
}
