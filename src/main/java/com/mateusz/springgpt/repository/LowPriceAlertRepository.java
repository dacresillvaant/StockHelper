package com.mateusz.springgpt.repository;

import com.mateusz.springgpt.entity.LowPriceAlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LowPriceAlertRepository extends JpaRepository<LowPriceAlertEntity,Long> {

    boolean existsByTicker(String ticker);

    Optional<LowPriceAlertEntity> findByTicker(String ticker);

    int deleteByTicker(String ticker);
}