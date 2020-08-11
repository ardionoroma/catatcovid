package com.benica.catatcovid.dto;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class TokenRequest
{
    @JsonProperty("token") @NotBlank
    @Getter @Setter private String token;    
}