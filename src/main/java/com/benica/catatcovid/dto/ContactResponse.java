package com.benica.catatcovid.dto;

import java.math.BigDecimal;
import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class ContactResponse
{
    @JsonProperty("id")
    @Getter @Setter private Long id;
    
    @JsonProperty("name")
    @Getter @Setter private String name;

    @JsonProperty("is_healthy")
    @Getter @Setter private Boolean isHealthy;

    @JsonProperty("is_cough_sneeze")
    @Getter @Setter private Boolean isCoughSneeze;

    @JsonProperty("is_masked")
    @Getter @Setter private Boolean isMasked;

    @JsonProperty("is_crowded")
    @Getter @Setter private Boolean isCrowded;

    @JsonProperty("is_phydist")
    @Getter @Setter private Boolean isPhydist;
    
    @JsonProperty("contact_date")
    @Getter @Setter private Date contactDate;

    @JsonProperty("score")
    @Getter @Setter private BigDecimal score;

    @JsonProperty("district_id")
    @Getter @Setter private Integer districtId;

    @JsonProperty("user_id")
    @Getter @Setter private Long userId;
}