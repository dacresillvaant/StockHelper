package com.mateusz.springgpt.repository;

import com.mateusz.springgpt.entity.CurrencyRateEntity;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRateEntity, Long> {

    @Query("""
    select cr.rate from CurrencyRateEntity cr 
    where cr.ratioDate >= :start and cr.ratioDate < :end
    and cr.symbol = :symbol
    """)
    BigDecimal findRateByRatioDateBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                          @Param("symbol")String symbol);
}