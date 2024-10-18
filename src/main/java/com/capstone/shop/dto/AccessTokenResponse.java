package com.capstone.shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AccessTokenResponse {
    private final String tokenType = "Bearer";
    private String accessToken;
    private String refreshToken;
    public AccessTokenResponse(String accessToken) {
        this.accessToken = accessToken;
        this.refreshToken = null;
    }
    public AccessTokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}