package com.cosain.trilo.user.presentation.dto;

import com.cosain.trilo.user.domain.AuthProvider;
import com.cosain.trilo.user.domain.Role;
import com.cosain.trilo.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserProfileResponse {

    private Long id;
    private String name;
    private String email;
    private String profileImageUrl;
    private AuthProvider authProvider;
    private Role role;

    @Builder(access = AccessLevel.PRIVATE)
    private UserProfileResponse(Long id, String name, String email, String profileImageUrl, AuthProvider authProvider, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.authProvider = authProvider;
        this.role = role;
    }

    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .authProvider(user.getAuthProvider())
                .role(user.getRole())
                .build();
    }
}
