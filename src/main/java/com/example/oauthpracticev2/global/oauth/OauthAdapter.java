package com.example.oauthpracticev2.global.oauth;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OauthAdapter {

    /**
     * OauthProperties를 OauthProvider로 변환해준다.
     */

    public static Map<String, OauthProvider> getOauthProviders(OauthProperties properties) {
        Map<String, OauthProvider> oauthProviderMap = new HashMap<>();

        properties.getUser()
                .forEach((key, value) ->
                        oauthProviderMap.put(
                                key,  //google, github, naver
                                new OauthProvider(
                                        value, //User 클래스( client-id, client-secret, redirect-uri )
                                        properties.getProvider().get(key) //provider 클래스 (token-uri, user-info-uri)
                                )
                        )
                );
        return oauthProviderMap;
    }
}