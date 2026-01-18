package com.stockmarket;

import com.stockmarket.logic.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderPriorityEdgeCaseTest {

    @Test
    void comparatorReturnsZeroForIdenticalOrders() {
        OrderComparator comparator = new OrderComparator(new MarketData());

        Order a = new Order("AAA", OrderType.BUY, 10, 100);
        Order b = a;

        assertEquals(
                0,
                comparator.compare(a, b),
                "Comparator should return 0 for the same object"
        );
    }

    @Test
    void ordersWithDifferentSymbolsAreNotComparableByPriceOnly() {
        OrderComparator comparator = new OrderComparator(new MarketData());

        Order a = new Order("AAA", OrderType.BUY, 10, 100);
        Order b = new Order("BBB", OrderType.BUY, 10, 110);

        int result = comparator.compare(a, b);

        assertNotEquals(
                0,
                result,
                "Orders with different symbols should not be treated as equal"
        );
    }

    @Test
    void buyAndSellWithSamePriceAndTimeHaveDeterministicOrder() {
        OrderComparator comparator = new OrderComparator(new MarketData());

        Order buy = new Order("AAA", OrderType.BUY, 10, 100);
        Order sell = new Order("AAA", OrderType.SELL, 10, 100);

        int result = comparator.compare(buy, sell);

        assertTrue(
                result != 0,
                "Comparator must define a strict order even in extreme tie cases"
        );
    }

    @Test
    void comparatorIsAntisymmetric() {
        OrderComparator comparator = new OrderComparator(new MarketData());

        Order a = new Order("AAA", OrderType.BUY, 10, 100);
        Order b = new Order("AAA", OrderType.BUY, 10, 105);

        int ab = comparator.compare(a, b);
        int ba = comparator.compare(b, a);

        assertEquals(
                -ab,
                ba,
                "Comparator must be antisymmetric: compare(a,b) == -compare(b,a)"
        );
    }

    @Test
    void comparatorIsTransitiveForBuyOrders() {
        OrderComparator comparator = new OrderComparator(new MarketData());

        Order low = new Order("AAA", OrderType.BUY, 10, 100);
        Order mid = new Order("AAA", OrderType.BUY, 10, 105);
        Order high = new Order("AAA", OrderType.BUY, 10, 110);

        assertTrue(comparator.compare(high, mid) < 0);
        assertTrue(comparator.compare(mid, low) < 0);
        assertTrue(comparator.compare(high, low) < 0,
                "Comparator must be transitive");
    }

    @Test
    void comparatorNeverThrowsForValidOrders() {
        OrderComparator comparator = new OrderComparator(new MarketData());

        Order buy = new Order("AAA", OrderType.BUY, 1, 1);
        Order sell = new Order("AAA", OrderType.SELL, 1, 1);

        assertDoesNotThrow(() -> comparator.compare(buy, sell));
        assertDoesNotThrow(() -> comparator.compare(sell, buy));
    }
}