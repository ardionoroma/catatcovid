package com.benica.catatcovid.dbo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    abstract Optional<User> findById(Long id);
}