package com.benica.catatcovid.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class LoginRequest
{
    @JsonProperty("username") @NotBlank
    @Size(min = 5, max = 25)
    @Getter @Setter private String username;

    @JsonProperty("password") @NotBlank
    @Getter @Setter private String password;
}