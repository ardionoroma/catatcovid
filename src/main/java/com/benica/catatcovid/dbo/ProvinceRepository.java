package com.benica.catatcovid.dbo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProvinceRepository extends JpaRepository<Province, Integer>
{
    abstract Optional<Province> findById(Integer id);
}