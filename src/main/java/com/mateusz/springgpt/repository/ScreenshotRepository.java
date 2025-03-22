package com.mateusz.springgpt.repository;

import com.mateusz.springgpt.entity.Screenshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScreenshotRepository extends JpaRepository<Screenshot, Long> {
}