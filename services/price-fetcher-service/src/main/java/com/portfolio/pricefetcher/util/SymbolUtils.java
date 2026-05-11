package com.portfolio.pricefetcher.util;

public final class SymbolUtils {

    private SymbolUtils() {}

    public static String normalize(String symbol) {

        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Symbol must not be blank");
        }

        return symbol.trim().toUpperCase();
    }
}