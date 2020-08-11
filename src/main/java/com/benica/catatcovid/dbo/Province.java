package com.benica.catatcovid.dbo;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "provinces")
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Province
{
    @Id
    @JsonProperty("id")
    @Getter @Setter @Column(name = "id") private Integer id;

    @JsonProperty("name")
    @Getter @Setter @Column(name = "name") private String name;

    @JsonProperty("created_at")
    @CreationTimestamp
    @Getter @Setter @Column(name = "created_at") private Timestamp createdAt;
}