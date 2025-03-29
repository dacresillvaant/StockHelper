package com.mateusz.springgpt.repository;

import com.mateusz.springgpt.entity.HeatmapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeatmapRepository extends JpaRepository<HeatmapEntity, Long> {}