package com.portfolio.pricefetcher.repository;

import com.portfolio.pricefetcher.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    @Query("SELECT ph FROM PriceHistory ph WHERE ph.symbol = :symbol ORDER BY ph.priceDate DESC")
    List<PriceHistory> findBySymbolOrderByDateDesc(@Param("symbol") String symbol);

    @Query(value = "SELECT * FROM price_history WHERE symbol = :symbol ORDER BY price_date DESC LIMIT :days",
            nativeQuery = true)
    List<PriceHistory> findRecentBySymbol(@Param("symbol") String symbol, @Param("days") int days);

    Optional<PriceHistory> findTopBySymbolOrderByRecordedAtDesc(String symbol);
}
