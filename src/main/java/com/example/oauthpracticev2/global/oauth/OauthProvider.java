package com.example.oauthpracticev2.global.oauth;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OauthProvider {

    /** application-oauth-yml에서 oauth.user */
    private final String clientId;
    private final String clientSecret;
    private final String redirectUrl;

    /** application-oauth-yml에서 oauth.provider */
    private final String tokenUrl;
    private final String userInfoUrl;

    public OauthProvider(OauthProperties.User user, OauthProperties.Provider provider) {
        this(user.getClientId(), user.getClientSecret(), user.getRedirectUri(), provider.getTokenUri(), provider.getUserInfoUri());
    }

    @Builder
    public OauthProvider(String clientId, String clientSecret, String redirectUrl, String tokenUrl, String userInfoUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUrl = redirectUrl;
        this.tokenUrl = tokenUrl;
        this.userInfoUrl = userInfoUrl;
    }
}