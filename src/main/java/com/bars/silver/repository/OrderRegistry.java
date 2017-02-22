package com.bars.silver.repository;

import com.bars.silver.model.Order;
import com.bars.silver.model.OrderRequest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OrderRegistry {

    Order add(OrderRequest request);
    void remove(Long orderId);
    Set<BigDecimal> getOrdersPrices();
    List<Order> getBuyOrdersByPrice(BigDecimal price);
    List<Order> getSellOrdersByPrice(BigDecimal price);
}
