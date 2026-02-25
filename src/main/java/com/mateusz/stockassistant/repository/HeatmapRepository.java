package com.mateusz.stockassistant.repository;

import com.mateusz.stockassistant.entity.HeatmapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HeatmapRepository extends JpaRepository<HeatmapEntity, Long> {

    Optional<HeatmapEntity> findById(Long id);
}