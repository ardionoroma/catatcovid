package com.benica.catatcovid.dbo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DistrictRepository extends JpaRepository<District, Integer> {
    abstract Optional<District> findById(Integer id);
}