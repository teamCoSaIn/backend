package com.cosain.trilo.auth.infra;

import com.cosain.trilo.auth.infra.oauth.kakao.dto.KakaoProfileResponse;
import com.cosain.trilo.user.domain.AuthProvider;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthProfileDto {

    private final String name;
    private final String email;
    private final String profileImageUrl;
    private final AuthProvider provider;

    @Builder(access = AccessLevel.PUBLIC)
    private OAuthProfileDto(String name, String email, String profileImageUrl, AuthProvider provider) {
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.provider = provider;
    }

    public static OAuthProfileDto of(KakaoProfileResponse kakaoProfileResponse){
        return OAuthProfileDto.builder()
                .name(kakaoProfileResponse.getKakaoAccount().getProfile().getNickName())
                .email(kakaoProfileResponse.getKakaoAccount().getEmail())
                .profileImageUrl(kakaoProfileResponse.getKakaoAccount().getProfile().getProfileImageUrl())
                .provider(AuthProvider.KAKAO)
                .build();
    }
}
