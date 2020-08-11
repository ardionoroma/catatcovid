package com.benica.catatcovid.dbo;

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
@Table(name = "cities")
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class City
{
    @Id
    @JsonProperty("id")
    @Getter @Setter @Column(name = "id") private Integer id;

    @JsonProperty("name")
    @Getter @Setter @Column(name = "name") private String name;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "province_id", referencedColumnName = "id")
    @JsonProperty("province")
    @Getter @Setter private Province province;

    @JsonProperty("created_at")
    @CreationTimestamp
    @Getter @Setter @Column(name = "created_at") private Timestamp createdAt;
}