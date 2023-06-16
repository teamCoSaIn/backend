package com.cosain.trilo.auth.presentation;

import com.cosain.trilo.auth.application.AuthService;
import com.cosain.trilo.auth.application.dto.KakaoLoginParams;
import com.cosain.trilo.auth.application.dto.LoginResult;
import com.cosain.trilo.auth.application.dto.NaverLoginParams;
import com.cosain.trilo.auth.presentation.dto.AuthResponse;
import com.cosain.trilo.auth.presentation.dto.KakaoOAuthLoginRequest;
import com.cosain.trilo.auth.presentation.dto.NaverOAuthLoginRequest;
import com.cosain.trilo.auth.presentation.dto.RefreshTokenStatusResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthService authService;

    @PostMapping("/reissue")
    public ResponseEntity<AuthResponse> reissueAccessToken(@CookieValue(value = "refreshToken", required = true) String refreshToken){
        String accessToken = authService.reissueAccessToken(refreshToken);
        return ResponseEntity.ok(AuthResponse.from(accessToken));
    }

    @GetMapping("/token/refresh-token-info")
    public ResponseEntity<RefreshTokenStatusResponse> checkRefreshTokenStatus(@CookieValue(value = "refreshToken", required = false) String refreshToken){
        return ResponseEntity.ok(authService.createTokenStatus(refreshToken));
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader(value = "Authorization") String authHeaderValue, @CookieValue(value = "refreshToken", required = true) String refreshToken){
        authService.logout(authHeaderValue, refreshToken);
    }

    @PostMapping("/login/kakao")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse login(@Valid @RequestBody KakaoOAuthLoginRequest kakaoOAuthLoginRequest, HttpServletResponse response){
        LoginResult loginResult = authService.login(KakaoLoginParams.of(kakaoOAuthLoginRequest.getCode(), kakaoOAuthLoginRequest.getRedirect_uri()));
        Cookie cookie = makeRefreshTokenCookie(loginResult.getRefreshToken());
        response.addCookie(cookie);
        return AuthResponse.from(loginResult.getAccessToken());
    }

    @PostMapping("/login/naver")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse login(@Valid @RequestBody NaverOAuthLoginRequest naverOAuthLoginRequest, HttpServletResponse response){
        LoginResult loginResult = authService.login(NaverLoginParams.of(naverOAuthLoginRequest.getCode(), naverOAuthLoginRequest.getState()));
        Cookie cookie = makeRefreshTokenCookie(loginResult.getRefreshToken());
        response.addCookie(cookie);
        return AuthResponse.from(loginResult.getAccessToken());
    }

    private Cookie makeRefreshTokenCookie(String refreshTokenStr){
        Cookie cookie = new Cookie("refreshToken", refreshTokenStr);
        cookie.setMaxAge(3600);
        cookie.setPath("/");
        return cookie;
    }
}
