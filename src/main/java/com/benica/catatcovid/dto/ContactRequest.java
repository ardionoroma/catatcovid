package com.benica.catatcovid.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class ContactRequest
{
    @JsonProperty("name") @NotBlank
    @Getter @Setter String name;

    @JsonProperty("is_healthy") @NotNull
    @Getter @Setter Boolean isHealthy;

    @JsonProperty("is_cough_sneeze") @NotNull
    @Getter @Setter Boolean isCoughSneeze;

    @JsonProperty("is_masked") @NotNull
    @Getter @Setter Boolean isMasked;

    @JsonProperty("is_crowded") @NotNull
    @Getter @Setter Boolean isCrowded;

    @JsonProperty("is_phydist") @NotNull
    @Getter @Setter Boolean isPhydist;
}