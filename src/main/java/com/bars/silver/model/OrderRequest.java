package com.bars.silver.model;

import java.math.BigDecimal;
import java.util.Objects;

import static com.bars.silver.model.Order.OrderType;

public final class OrderRequest {

    public final String userId;
    public final BigDecimal quantity;
    public final BigDecimal pricePerKg;
    public final OrderType orderType;

    public OrderRequest(String userId, BigDecimal quantity, BigDecimal pricePerKg, OrderType orderType) {
        if (userId == null) throw new IllegalArgumentException();
        if (quantity == null) throw new IllegalArgumentException();
        if (pricePerKg == null) throw new IllegalArgumentException();
        if (orderType == null) throw new IllegalArgumentException();
        this.userId = userId;
        this.quantity = quantity;
        this.pricePerKg = pricePerKg;
        this.orderType = orderType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderRequest that = (OrderRequest) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(quantity, that.quantity) &&
                Objects.equals(pricePerKg, that.pricePerKg) &&
                orderType == that.orderType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, quantity, pricePerKg, orderType);
    }
}
