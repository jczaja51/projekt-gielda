package com.stockmarket;

import com.stockmarket.logic.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderPriorityTest {

    @Test
    void higherBuyPriceHasHigherPriorityThanLowerBuyPrice() {
        OrderComparator comparator = new OrderComparator(new MarketData());

        Order low = new Order("AAA", OrderType.BUY, 10, 100);
        Order high = new Order("AAA", OrderType.BUY, 10, 105);

        assertTrue(
                comparator.compare(high, low) < 0,
                "BUY with higher limit should have higher priority"
        );
    }

    @Test
    void lowerSellPriceHasHigherPriorityThanHigherSellPrice() {
        OrderComparator comparator = new OrderComparator(new MarketData());

        Order high = new Order("AAA", OrderType.SELL, 10, 120);
        Order low = new Order("AAA", OrderType.SELL, 10, 110);

        assertTrue(
                comparator.compare(low, high) < 0,
                "SELL with lower limit should have higher priority"
        );
    }

    @Test
    void priceIsMoreImportantThanCreationTime() {
        OrderComparator comparator = new OrderComparator(new MarketData());

        Order worsePriceEarlier =
                new Order("AAA", OrderType.BUY, 10, 100);
        Order betterPriceLater =
                new Order("AAA", OrderType.BUY, 10, 105);

        assertTrue(
                comparator.compare(betterPriceLater, worsePriceEarlier) < 0,
                "Better price should win even if order was created later"
        );
    }

    @Test
    void earlierOrderWinsWhenPriceIsEqual() throws InterruptedException {
        OrderComparator comparator = new OrderComparator(new MarketData());

        Order first = new Order("AAA", OrderType.BUY, 10, 100);

        Thread.sleep(1);

        Order second = new Order("AAA", OrderType.BUY, 10, 100);

        assertTrue(
                comparator.compare(first, second) < 0,
                "When prices are equal, earlier order should have priority"
        );
    }

    @Test
    void buyOrdersHaveHigherPriorityThanSellOrdersAtSamePrice() {
        OrderComparator comparator = new OrderComparator(new MarketData());

        Order buy =
                new Order("AAA", OrderType.BUY, 10, 100);
        Order sell =
                new Order("AAA", OrderType.SELL, 10, 100);

        assertTrue(
                comparator.compare(buy, sell) < 0,
                "BUY orders should be prioritized over SELL at the same price"
        );
    }
    @Test
    void priorityQueuePollsOrdersInComparatorOrder() throws InterruptedException {
        MarketData md = new MarketData();
        OrderComparator comparator = new OrderComparator(md);

        java.util.PriorityQueue<Order> pq = new java.util.PriorityQueue<>(comparator);

        Order low = new Order("AAA", OrderType.BUY, 1, 100);
        Thread.sleep(1);
        Order high = new Order("AAA", OrderType.BUY, 1, 105);
        Thread.sleep(1);
        Order mid = new Order("AAA", OrderType.BUY, 1, 102);

        pq.add(low);
        pq.add(high);
        pq.add(mid);

        assertSame(high, pq.poll(), "Highest BUY price should come first");
        assertSame(mid, pq.poll(), "Next best price should come second");
        assertSame(low, pq.poll(), "Lowest price should come last");
    }
}