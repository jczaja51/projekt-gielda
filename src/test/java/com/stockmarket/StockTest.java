    package com.stockmarket;

    import org.junit.jupiter.api.Test;

    import static org.junit.jupiter.api.Assertions.*;

    class StockTest {

        @Test
        void shouldCreateStockWithCorrectValues() {
            Stock stock = new Stock("TSLA", "Tesla, Inc.", 250.30);
            assertEquals("TSLA", stock.getSymbol());
            assertEquals("Tesla, Inc.", stock.getName());
            assertEquals(250.30, stock.getInitialPrice());
        }

        @Test
        void shouldConvertSymbolToUpperCase() {
            Stock stock = new Stock("meta", "Meta Platforms, Inc.", 330.50);
            assertEquals("META", stock.getSymbol());
        }

        @Test
        void shouldThrowExceptionForEmptySymbol() {
            assertThrows(IllegalArgumentException.class, () -> new Stock("", "Apple Inc.", 180.10));
        }

        @Test
        void shouldThrowExceptionForEmptyName() {
            assertThrows(IllegalArgumentException.class, () -> new Stock("AAPL", "", 180.10));
        }

        @Test
        void shouldThrowExceptionForNonPositivePrice() {
            assertThrows(IllegalArgumentException.class, () -> new Stock("AMZN", "Amazon.com, Inc.", 0.0));
            assertThrows(IllegalArgumentException.class, () -> new Stock("AMZN", "Amazon.com, Inc.", -5.0));
        }

        @Test
        void equalsShouldReturnTrueForSameSymbol() {
            Stock s1 = new Stock("MSFT", "Microsoft Corporation", 410.0);
            Stock s2 = new Stock("MSFT", "Microsoft Corp. (different name)", 999.99);
            assertEquals(s1, s2);
        }

        @Test
        void equalsShouldReturnFalseForDifferentSymbols() {
            Stock s1 = new Stock("TSLA", "Tesla, Inc.", 250.30);
            Stock s2 = new Stock("META", "Meta Platforms, Inc.", 330.50);
            assertNotEquals(s1, s2);
        }

        @Test
        void equalsShouldHandleNullAndSelf() {
            Stock stock = new Stock("AAPL", "Apple Inc.", 180.10);
            assertNotEquals(stock, null);
            assertEquals(stock, stock);
        }

        @Test
        void hashCodeShouldBeEqualForSameSymbolRegardlessOfCase() {
            Stock s1 = new Stock("amzn", "Amazon.com, Inc.", 142.75);
            Stock s2 = new Stock("AMZN", "Amazon.com, Inc.", 142.75);
            assertEquals(s1.hashCode(), s2.hashCode());
        }

        @Test
        void toStringShouldContainSymbolAndName() {
            Stock stock = new Stock("TSLA", "Tesla, Inc.", 250.30);
            String output = stock.toString();
            assertTrue(output.contains("TSLA"));
            assertTrue(output.contains("Tesla"));
        }
    }