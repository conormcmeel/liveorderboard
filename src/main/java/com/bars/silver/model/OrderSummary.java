package com.bars.silver.model;

import com.bars.silver.model.Order.OrderType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public final class OrderSummary {

    public final BigDecimal quantity;
    public final BigDecimal pricePerKg;
    public final OrderType orderType;

    private OrderSummary(BigDecimal quantity, BigDecimal pricePerKg, OrderType orderType) {
        this.quantity = quantity;
        this.pricePerKg = pricePerKg;
        this.orderType = orderType;
    }

    public static OrderSummary of(List<Order> orders) {
        validateOrders(orders);
        BigDecimal quantity = orders.stream().map(order -> order.quantity)
                .reduce(BigDecimal::add).orElseThrow(IllegalArgumentException::new);
        return new OrderSummary(quantity, orders.get(0).pricePerKg, orders.get(0).orderType);
    }

    private static void validateOrders(List<Order> orders) {
        long distinctPrices = orders.stream().map(order -> order.pricePerKg).distinct().count();
        long distinctTypes = orders.stream().map(order -> order.orderType).distinct().count();
        if (distinctPrices != 1 || distinctTypes != 1) throw new IllegalArgumentException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderSummary that = (OrderSummary) o;
        return Objects.equals(quantity, that.quantity) &&
                Objects.equals(pricePerKg, that.pricePerKg) &&
                orderType == that.orderType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(quantity, pricePerKg, orderType);
    }
}
