package com.benica.catatcovid.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class ContactsResponse
{
    @JsonProperty("type")
    @Getter @Setter private String type;

    @JsonProperty("type_name")
    @Getter @Setter private String typeName;

    @JsonProperty("alert_meter")
    @Getter @Setter private BigDecimal alertMeter;

    @JsonProperty("district_alert_meter")
    @Getter @Setter private BigDecimal districtAlertMeter;
}