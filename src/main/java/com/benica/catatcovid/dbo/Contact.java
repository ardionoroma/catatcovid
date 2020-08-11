package com.benica.catatcovid.dbo;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "contacts")
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Contact
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    @Getter @Setter @Column(name = "id") private Long id;

    @JsonProperty("name")
    @Getter @Setter @Column(name = "name") private String name;

    @JsonProperty("is_healthy")
    @Getter @Setter @Column(name = "is_healthy") private Boolean isHealthy;

    @JsonProperty("is_cough_sneeze")
    @Getter @Setter @Column(name = "is_cough_sneeze") private Boolean isCoughSneeze;

    @JsonProperty("is_masked")
    @Getter @Setter @Column(name = "is_masked") private Boolean isMasked;

    @JsonProperty("is_crowded")
    @Getter @Setter @Column(name = "is_crowded") private Boolean isCrowded;

    @JsonProperty("is_phydist")
    @Getter @Setter @Column(name = "is_phydist") private Boolean isPhydist;

    @JsonProperty("contact_date")
    @Getter @Setter @Column(name = "contact_date") private Date contactDate;

    @JsonProperty("score")
    @Getter @Setter @Column(name = "score") private BigDecimal score;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "district_id", referencedColumnName = "id")
    @JsonProperty("district")
    @Getter @Setter private District district;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonProperty("user")
    @Getter @Setter private User user;

    @JsonProperty("created_at")
    @CreationTimestamp
    @Getter @Setter @Column(name = "created_at") private Timestamp createdAt;
}