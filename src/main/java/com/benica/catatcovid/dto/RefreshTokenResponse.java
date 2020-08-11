package com.benica.catatcovid.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class RefreshTokenResponse
{
    @JsonProperty("token")
    @Getter @Setter private String token;

    @JsonProperty("refresh_token")
    @Getter @Setter private String refreshToken;
}