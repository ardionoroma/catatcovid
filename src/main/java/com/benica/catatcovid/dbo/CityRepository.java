package com.benica.catatcovid.dbo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Integer>
{
    abstract Optional<City> findById(Integer id);
}