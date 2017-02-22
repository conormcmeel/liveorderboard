package com.bars.silver.model;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static com.bars.silver.model.Order.OrderType.BUY;
import static com.bars.silver.model.Order.OrderType.SELL;
import static java.math.BigDecimal.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class OrderSummaryTest {

    private static final BigDecimal QUANTITY_ONE = ONE;
    private static final BigDecimal QUANTITY_TEN = TEN;
    private static final BigDecimal PRICE_ONE = ONE;
    private static final BigDecimal PRICE_TEN = TEN;
    private static final String USER_1 = "user1";
    private static final String USER_2 = "user2";

    @Test
    public void constructSummaryOfOrders() {
        Order order1 = Order.of(new OrderRequest(USER_1, QUANTITY_ONE, PRICE_ONE, SELL), 1L);
        Order order2 = Order.of(new OrderRequest(USER_2, QUANTITY_TEN, PRICE_ONE, SELL), 2L);

        OrderSummary orderSummary = OrderSummary.of(asList(order1, order2));

        assertThat(orderSummary.orderType).isEqualTo(SELL);
        assertThat(orderSummary.pricePerKg).isEqualTo(PRICE_ONE);
        assertThat(orderSummary.quantity).isEqualTo(new BigDecimal("11"));
    }

    @Test
    public void constructSummaryOfSingleOrder() {
        Order order = Order.of(new OrderRequest(USER_1, QUANTITY_ONE, PRICE_ONE, SELL), 1L);

        OrderSummary orderSummary = OrderSummary.of(Collections.singletonList(order));

        assertThat(orderSummary.orderType).isEqualTo(SELL);
        assertThat(orderSummary.pricePerKg).isEqualTo(PRICE_ONE);
        assertThat(orderSummary.quantity).isEqualTo(PRICE_ONE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionWhenConstructingSummaryOfEmptyOrderList() {
        OrderSummary.of(Collections.emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionWhenConstructingSummaryOfOrdersWithDifferentTypes() {
        Order order1 = Order.of(new OrderRequest(USER_1, QUANTITY_ONE, PRICE_ONE, SELL), 1L);
        Order order2 = Order.of(new OrderRequest(USER_2, QUANTITY_TEN, PRICE_ONE, BUY), 2L);

        OrderSummary.of(asList(order1, order2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionWhenConstructingSummaryOfOrdersWithDifferentPrice() {
        Order order1 = Order.of(new OrderRequest(USER_1, QUANTITY_ONE, PRICE_TEN, BUY), 1L);
        Order order2 = Order.of(new OrderRequest(USER_2, QUANTITY_TEN, PRICE_ONE, BUY), 2L);

        OrderSummary.of(asList(order1, order2));
    }
}
