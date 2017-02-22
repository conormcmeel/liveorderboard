package com.bars.silver.repository;

import com.bars.silver.exceptions.OrderNotFoundException;
import com.bars.silver.model.Order;
import com.bars.silver.model.OrderRequest;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static com.bars.silver.model.Order.OrderType.BUY;
import static com.bars.silver.model.Order.OrderType.SELL;
import static java.math.BigDecimal.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryOrderRegistryTest {

    private static final BigDecimal QUANTITY_ONE = ONE;
    private static final BigDecimal PRICE_ONE = ONE;
    private static final BigDecimal PRICE_ZERO = ZERO;
    private static final BigDecimal PRICE_TEN = TEN;
    private static final String USER_1 = "user1";
    private static final String USER_2 = "user2";

    private OrderRegistry orderRegistry;

    @Before
    public void setUp() {
        orderRegistry = new InMemoryOrderRegistry();
    }

    @Test
    public void emptyRegistryContainsNoPrices() {
        assertThat(orderRegistry.getOrdersPrices()).isEmpty();
    }

    @Test
    public void registryRepresentOrdersPrices() {
        orderRegistry.add(new OrderRequest(USER_1, QUANTITY_ONE, PRICE_ONE, SELL));
        orderRegistry.add(new OrderRequest(USER_2, QUANTITY_ONE, PRICE_TEN, BUY));

        assertThat(orderRegistry.getOrdersPrices()).contains(PRICE_ONE, PRICE_TEN);
    }

    @Test
    public void registryReturnsBuyOrdersByPrice() {
        Order orderOne = orderRegistry.add(new OrderRequest(USER_1, QUANTITY_ONE, PRICE_ONE, BUY));
        Order orderTen = orderRegistry.add(new OrderRequest(USER_2, QUANTITY_ONE, PRICE_TEN, BUY));
        Order sellOrderTen = orderRegistry.add(new OrderRequest(USER_2, QUANTITY_ONE, PRICE_TEN, SELL));

        orderRegistry.getBuyOrdersByPrice(PRICE_TEN).containsAll(singleton(orderTen));
    }

    @Test
    public void registryReturnsEmptyListWhenNoBuyOrdersForGivenPrice() {
        Order orderOne = orderRegistry.add(new OrderRequest(USER_1, QUANTITY_ONE, PRICE_ONE, BUY));
        Order orderTen = orderRegistry.add(new OrderRequest(USER_2, QUANTITY_ONE, PRICE_TEN, BUY));

        assertThat(orderRegistry.getBuyOrdersByPrice(PRICE_ZERO)).isEmpty();
    }

    @Test
    public void registryReturnsMultipleBuyOrdersForGivenPrice() {
        Order orderOne1 = orderRegistry.add(new OrderRequest(USER_1, QUANTITY_ONE, PRICE_ONE, BUY));
        Order orderOne2 = orderRegistry.add(new OrderRequest(USER_2, QUANTITY_ONE, PRICE_ONE, BUY));

        assertThat(orderRegistry.getBuyOrdersByPrice(PRICE_ONE)).containsAll(asList(orderOne1, orderOne2));
    }

    @Test
    public void registryReturnsSellOrdersByPrice() {
        Order orderOne = orderRegistry.add(new OrderRequest(USER_1, QUANTITY_ONE, PRICE_ONE, SELL));
        Order orderTen = orderRegistry.add(new OrderRequest(USER_2, QUANTITY_ONE, PRICE_TEN, SELL));
        Order sellOrderTen = orderRegistry.add(new OrderRequest(USER_2, QUANTITY_ONE, PRICE_TEN, BUY));

        orderRegistry.getBuyOrdersByPrice(PRICE_TEN).containsAll(singleton(sellOrderTen));
    }

    @Test
    public void registryReturnsEmptyListWhenNoSellOrdersForGivenPrice() {
        Order orderOne = orderRegistry.add(new OrderRequest(USER_1, QUANTITY_ONE, PRICE_ONE, SELL));
        Order orderTen = orderRegistry.add(new OrderRequest(USER_2, QUANTITY_ONE, PRICE_TEN, SELL));

        assertThat(orderRegistry.getSellOrdersByPrice(PRICE_ZERO)).isEmpty();
    }

    @Test
    public void registryReturnsMultipleSellOrdersForGivenPrice() {
        Order orderOne1 = orderRegistry.add(new OrderRequest(USER_1, QUANTITY_ONE, PRICE_ONE, SELL));
        Order orderOne2 = orderRegistry.add(new OrderRequest(USER_2, QUANTITY_ONE, PRICE_ONE, SELL));

        assertThat(orderRegistry.getSellOrdersByPrice(PRICE_ONE)).containsAll(asList(orderOne1, orderOne2));
    }

    @Test
    public void registryRemoveOrders() {
        Order orderOne = orderRegistry.add(new OrderRequest(USER_1, QUANTITY_ONE, PRICE_ONE, SELL));
        Order orderTen = orderRegistry.add(new OrderRequest(USER_2, QUANTITY_ONE, PRICE_TEN, SELL));

        orderRegistry.remove(orderTen.orderId);

        assertThat(orderRegistry.getOrdersPrices()).doesNotContain(orderTen.pricePerKg);
        assertThat(orderRegistry.getSellOrdersByPrice(orderTen.pricePerKg)).isEmpty();
    }

    @Test(expected = OrderNotFoundException.class)
    public void registryThrowExceptionWhenRemoveNonExistingOrder() {
        long dummyOrderId = 4567L;

        orderRegistry.remove(dummyOrderId);
    }
}
