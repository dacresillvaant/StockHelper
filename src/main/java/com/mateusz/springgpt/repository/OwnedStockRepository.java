package com.mateusz.springgpt.repository;

import com.mateusz.springgpt.entity.OwnedStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnedStockRepository extends JpaRepository<OwnedStockEntity, Long> {}