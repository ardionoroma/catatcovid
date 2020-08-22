package com.benica.catatcovid.dbo;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ContactRepository extends JpaRepository<Contact, Long>
{
    abstract Long countByDistrictIdAndContactDate(Integer districtId, Date contactDate);
    @Query("SELECT AVG(c.score) FROM Contact c WHERE c.district=?1 AND c.contactDate=?2")
    abstract BigDecimal averageByDistrictAndContactDate(District district, Date contactDate);

    @Query("SELECT COUNT(c) FROM Contact c WHERE c.district=?1 AND c.contactDate<=?2 AND c.contactDate>=?3")
    abstract Long countDistrictWeeksAgo(District district, Date now, Date weeksAgo);
    @Query("SELECT AVG(c.score) FROM Contact c WHERE c.district=?1 AND c.contactDate<=?2 AND c.contactDate>=?3")
    abstract BigDecimal averageDistrictWeeksAgo(District district, Date now, Date weeksAgo);

    abstract Long countByUserIdAndContactDate(Long userId, Date contactDate);
    @Query("SELECT AVG(c.score) FROM Contact c WHERE c.user=?1 AND c.contactDate=?2")
    abstract BigDecimal averageByUserAndContactDate(User user, Date contactDate);
    abstract List<Contact> findByUserIdAndContactDate(Long userId, Date contactDate);

    @Query("SELECT COUNT(c) FROM Contact c WHERE c.user=?1 AND c.contactDate<=?2 AND c.contactDate>=?3")
    abstract Long countWeeksAgo(User user, Date now, Date weeksAgo);
    @Query("SELECT AVG(c.score) FROM Contact c WHERE c.user=?1 AND c.contactDate<=?2 AND c.contactDate>=?3")
    abstract BigDecimal averageWeeksAgo(User user, Date now, Date weeksAgo);
    @Query("SELECT c FROM Contact c WHERE c.user=?1  AND c.contactDate<=?2 AND c.contactDate>=?3")
    abstract List<Contact> findWeeksAgo(User user, Date now, Date weeksAgo);
}