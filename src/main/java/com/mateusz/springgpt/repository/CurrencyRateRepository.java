package com.mateusz.springgpt.repository;

import com.mateusz.springgpt.controller.twelvedata.dto.CurrencyRateInternalDto;
import com.mateusz.springgpt.entity.CurrencyRateEntity;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRateEntity, Long> {

    @Query("""
            select new com.mateusz.springgpt.controller.twelvedata.dto.CurrencyRateInternalDto(cr.symbol, cr.rate, cr.ratioDate)
            from CurrencyRateEntity cr
            where cr.ratioDate >= :start and cr.ratioDate < :end
            and cr.symbol = :symbol
            """)
    Optional<CurrencyRateInternalDto> findExchangeRateByRatioDateBetween(@Param("start") LocalDateTime start,
                                                                         @Param("end") LocalDateTime end,
                                                                         @Param("symbol") String symbol);
}