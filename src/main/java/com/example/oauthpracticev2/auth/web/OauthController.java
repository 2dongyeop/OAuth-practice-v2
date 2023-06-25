package com.example.oauthpracticev2.auth.web;

import com.example.oauthpracticev2.auth.application.LoginResponse;
import com.example.oauthpracticev2.auth.application.OauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OauthController {

    private final OauthService oauthService;

    @GetMapping("/login/oauth/{provider}")
    public ResponseEntity<LoginResponse> login(
            @PathVariable String provider,
            @RequestParam String code) {

        LoginResponse loginResponse = oauthService.login(provider, code);
        return ResponseEntity.ok().body(loginResponse);
    }
}
