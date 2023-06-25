package com.example.oauthpracticev2.auth.application;

import com.example.oauthpracticev2.global.jwt.JwtTokenProvider;
import com.example.oauthpracticev2.global.oauth.InMemoryProviderRepository;
import com.example.oauthpracticev2.global.oauth.OauthProvider;
import com.example.oauthpracticev2.member.persistence.Member;
import com.example.oauthpracticev2.member.persistence.MemberRepository;
import com.example.oauthpracticev2.member.persistence.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OauthService {

    /**
     * OAuthService의 역할은 크게 2가지다.
     * 1. 인증코드를 이용해 AccessToken 받아오기
     * 2. AccessToken을 이용해 실제 사용자 정보 받아오기
     */

    private final InMemoryProviderRepository inMemoryProviderRepository;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(String providerName, String code) {

        // 프론트에서 넘어온 provider 이름을 통해 InMemoryProviderRepository에서 OauthProvider 가져오기
        OauthProvider provider = inMemoryProviderRepository.findByProviderName(providerName);

        // access token 가져오기
        // 이 과정에서 실제 OAuth 서버와 통신하기 위해 WebClient를 이용한다. (의존성 추가하기)
        OauthToken tokenResponse = getToken(code, provider);

        // 유저 정보 가져오기
        UserProfile userProfile = getUserProfile(providerName, tokenResponse, provider);

        // 사용자를 DB에 저장
        Member member = saveOrUpdate(userProfile);

        // JWT 생성
        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(member.getId()));
        String refreshToken = jwtTokenProvider.createRefreshToken(String.valueOf(member.getId()));

        //TODO 위에서 생성한 JWT를 Redis로 저장하기

        return LoginResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .imageUrl(member.getImageUrl())
                .role(member.getRole())
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private OauthToken getToken(String code, OauthProvider provider) {
        return WebClient.create()
                .post() //http method
                .uri(provider.getTokenUrl()) //path
                .headers( //header
                        header -> {
                            header.setBasicAuth(provider.getClientId(), provider.getClientSecret());
                            header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                            header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                            header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                        })
                .bodyValue(tokenRequest(code, provider)) //body
                .retrieve() //검색
                .bodyToMono(OauthToken.class) //응답을 클래스로 변환
                .block(); //동기로 동작하도록.. 비동기로 동작하는 메소드도 있으니 알아보기.
    }


    private MultiValueMap<String, String> tokenRequest(String code, OauthProvider provider) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", provider.getRedirectUrl());
        return formData;
    }


    private UserProfile getUserProfile(String providerName, OauthToken tokenResponse, OauthProvider provider) {
        Map<String, Object> userAttributes = getUserAttributes(provider, tokenResponse);
        // TODO 유저 정보(map)를 통해 UserProfile 만들기
        return OauthAttributes.extract(providerName, userAttributes);
    }


    // OAuth 서버에서 유저 정보 map으로 가져오기
    private Map<String, Object> getUserAttributes(OauthProvider provider, OauthToken tokenResponse) {
        return WebClient.create()
                .get() //http method
                .uri(provider.getUserInfoUrl()) //path
                .headers(header -> header.setBearerAuth(tokenResponse.getAccessToken())) //header
                .retrieve() //검색
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                }) //Map 형태로 반환
                .block();
    }


    private Member saveOrUpdate(UserProfile userProfile) {
        Member member = memberRepository.findByOauthId(userProfile.getOauthId())
                .map(entity -> entity.update(
                        userProfile.getEmail(), userProfile.getName(), userProfile.getImageUrl()))
                .orElseGet(userProfile::toMember);
        return memberRepository.save(member);
    }
}
