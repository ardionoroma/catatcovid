package com.benica.catatcovid.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class RegisterRequest
{
    @JsonProperty("username") @NotBlank
    @Size(min = 5, max = 25)
    @Getter @Setter String username;

    @JsonProperty("password") @NotBlank
    @Getter @Setter String password;

    @JsonProperty("province_id") @NotNull
    @Getter @Setter Integer provinceId;

    @JsonProperty("province") @NotBlank
    @Getter @Setter String province;

    @JsonProperty("city_id") @NotNull
    @Getter @Setter Integer cityId;

    @JsonProperty("city") @NotBlank
    @Getter @Setter String city;

    @JsonProperty("district_id") @NotNull
    @Getter @Setter Integer districtId;

    @JsonProperty("district") @NotBlank
    @Getter @Setter String district;
}