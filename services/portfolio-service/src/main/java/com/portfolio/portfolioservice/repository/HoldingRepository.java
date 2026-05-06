package com.portfolio.portfolioservice.repository;

import com.portfolio.portfolioservice.entity.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, Long> {

    @Query("SELECT h FROM Holding h WHERE h.portfolio.id = :pid AND h.isDeleted = false")
    List<Holding> findActiveHoldingsByPortfolio(@Param("pid") Long portfolioId);

    @Query("SELECT DISTINCT h.stockSymbol FROM Holding h WHERE h.isDeleted = false")
    List<String> findAllActiveSymbols();

    @Query(value = "SELECT * FROM holdings WHERE portfolio_id = ?1 AND is_deleted = false ORDER BY (buy_price * quantity) DESC LIMIT ?2",
            nativeQuery = true)
    List<Holding> findTopHoldingsByPortfolio(Long portfolioId, int limit);
}
