package com.mateusz.stockassistant.repository;

import com.mateusz.stockassistant.entity.OwnedStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnedStockRepository extends JpaRepository<OwnedStockEntity, Long> {

    boolean existsByTicker(String ticker);

    Optional<OwnedStockEntity> findByTicker(String ticker);

    int deleteByTicker(String ticker);
}