package com.stockmarket;

import com.stockmarket.logic.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderMatchingConsistencyTest {

    @Test
    void buyAndSellWithCrossingPricesCanBeMatched() {
        OrderComparator comparator = new OrderComparator(new MarketData());

        Order buy = new Order("AAA", OrderType.BUY, 10, 110);
        Order sell = new Order("AAA", OrderType.SELL, 10, 100);

        assertTrue(
                comparator.compare(buy, sell) < 0,
                "Crossing prices should allow matching"
        );
    }

    @Test
    void comparatorDoesNotDecideAboutMatchability() {
        OrderComparator comparator = new OrderComparator(new MarketData());

        Order buy = new Order("AAA", OrderType.BUY, 10, 90);
        Order sell = new Order("AAA", OrderType.SELL, 10, 110);

        int result = comparator.compare(buy, sell);

        assertTrue(
                result > 0 || result < 0,
                "Comparator must define a strict ordering even for non-matchable orders"
        );
    }
        @Test
    void buyAndSellAtSamePriceHaveDeterministicOrder() {
        OrderComparator comparator = new OrderComparator(new MarketData());

        Order buy = new Order("AAA", OrderType.BUY, 10, 100);
        Order sell = new Order("AAA", OrderType.SELL, 10, 100);

        int result = comparator.compare(buy, sell);

        assertNotEquals(
                0,
                result,
                "Matching engine must define deterministic order"
        );
    }

    @Test
    void ordersWithDifferentSymbolsAreNeverMatched() {
        OrderComparator comparator = new OrderComparator(new MarketData());

        Order buy = new Order("AAA", OrderType.BUY, 10, 120);
        Order sell = new Order("BBB", OrderType.SELL, 10, 100);

        assertNotEquals(
                0,
                comparator.compare(buy, sell),
                "Orders for different symbols should not be matched"
        );
    }

    @Test
    void buyOrdersArePrioritizedWithinSameSymbolBeforeSellOrders() {
        OrderComparator comparator = new OrderComparator(new MarketData());

        Order buy = new Order("AAA", OrderType.BUY, 10, 100);
        Order sell = new Order("AAA", OrderType.SELL, 10, 100);

        assertTrue(
                comparator.compare(buy, sell) < 0,
                "BUY orders should be evaluated first for matching"
        );
    }
}