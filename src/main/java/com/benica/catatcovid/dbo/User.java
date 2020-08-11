package com.benica.catatcovid.dbo;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class User
{
    @Id
    @JsonProperty("id")
    @Getter @Setter @Column(name = "id") private Long id;

    @JsonProperty("username")
    @Getter @Setter @Column(name = "username") private String username;

    @JsonProperty("password")
    @Getter @Setter @Column(name = "password") private String password;

    @JsonProperty("last_logged_in")
    @Getter @Setter @Column(name = "last_logged_in") private Timestamp lastLoggedIn;

    @JsonProperty("alert_meter")
    @Getter @Setter @Column(name = "alert_meter") private BigDecimal alertMeter;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "district_id", referencedColumnName = "id")
    @JsonProperty("district")
    @Getter @Setter private District district;

    @JsonProperty("created_at")
    @CreationTimestamp
    @Getter @Setter @Column(name = "created_at") private Timestamp createdAt;
}