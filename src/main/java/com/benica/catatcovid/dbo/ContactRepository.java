package com.benica.catatcovid.dbo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<User, Long> {

}