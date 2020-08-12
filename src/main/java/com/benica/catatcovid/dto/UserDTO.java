package com.benica.catatcovid.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class UserDTO
{
    @JsonProperty("id")
    @Getter @Setter private Long id;
    
    @JsonProperty("username")
    @Getter @Setter private String username;

    @JsonProperty("is_logged_in")
    @Getter @Setter private Boolean isLoggedIn;
    
    @JsonProperty("last_logged_in")
    @Getter @Setter private Timestamp lastLoggedIn;

    @JsonProperty("alert_meter")
    @Getter @Setter private BigDecimal alertMeter;

    @JsonProperty("district_name")
    @Getter @Setter private String districtName;

    @JsonProperty("district_alert_meter")
    @Getter @Setter private BigDecimal districtAlertMeter;

    @JsonProperty("is_noted")
    @Getter @Setter private Boolean isNoted;

    @JsonProperty("token")
    @Getter @Setter private String token;

    @JsonProperty("refresh_token")
    @Getter @Setter private String refreshToken;
}