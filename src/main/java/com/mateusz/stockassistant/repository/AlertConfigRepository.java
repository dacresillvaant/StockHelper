package com.mateusz.stockassistant.repository;

import com.mateusz.stockassistant.controller.alertconfig.AlertType;
import com.mateusz.stockassistant.entity.AlertConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertConfigRepository extends JpaRepository<AlertConfigEntity,Long> {

    boolean existsByTicker(String ticker);

    List<AlertConfigEntity> findByTicker(String ticker);

    int deleteByTickerAndAlertType(String ticker, AlertType alertType);

    List<AlertConfigEntity> findByAlertType(AlertType alertType);
}