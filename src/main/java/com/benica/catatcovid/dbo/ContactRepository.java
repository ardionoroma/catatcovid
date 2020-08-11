package com.benica.catatcovid.dbo;

import java.sql.Date;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    abstract Long countByUserIdAndContactDate(Long userId, Date contactDate);
}