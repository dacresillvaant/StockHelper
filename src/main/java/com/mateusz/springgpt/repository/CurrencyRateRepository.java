package com.mateusz.springgpt.repository;

import com.mateusz.springgpt.entity.CurrencyRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRateEntity, Long> {}