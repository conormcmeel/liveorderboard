package com.bars.silver.model;

import java.math.BigDecimal;
import java.util.Objects;

public final class Order {

    public enum OrderType {BUY, SELL}

    public final Long orderId;
    public final String userId;
    public final BigDecimal quantity;
    public final BigDecimal pricePerKg;
    public final OrderType orderType;

    private Order(Long orderId, String userId, BigDecimal quantity, BigDecimal pricePerKg, OrderType orderType) {
        this.orderId = orderId;
        this.userId = userId;
        this.quantity = quantity;
        this.pricePerKg = pricePerKg;
        this.orderType = orderType;
    }

    public static Order of(OrderRequest request, Long orderId) {
        return new Order(orderId, request.userId, request.quantity, request.pricePerKg, request.orderType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId) &&
                Objects.equals(userId, order.userId) &&
                Objects.equals(quantity, order.quantity) &&
                Objects.equals(pricePerKg, order.pricePerKg) &&
                orderType == order.orderType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, userId, quantity, pricePerKg, orderType);
    }
}