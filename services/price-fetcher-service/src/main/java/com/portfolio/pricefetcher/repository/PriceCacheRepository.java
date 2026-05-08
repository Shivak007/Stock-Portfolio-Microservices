package com.portfolio.pricefetcher.repository;

import com.portfolio.pricefetcher.entity.PriceCache;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PriceCacheRepository extends JpaRepository<PriceCache, Long> {
    Optional<PriceCache> findBySymbol(String symbol);
    boolean existsBySymbol(String symbol);
}
