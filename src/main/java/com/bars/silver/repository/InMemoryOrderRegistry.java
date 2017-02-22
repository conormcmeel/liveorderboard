package com.bars.silver.repository;

import com.bars.silver.exceptions.OrderNotFoundException;
import com.bars.silver.model.Order;
import com.bars.silver.model.OrderRequest;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static com.bars.silver.model.Order.OrderType.BUY;
import static com.bars.silver.model.Order.OrderType.SELL;
import static com.bars.silver.model.Order.of;
import static java.util.Optional.ofNullable;

public class InMemoryOrderRegistry implements OrderRegistry {

    private final Map<BigDecimal, List<Order>> ordersPerPrice;
    private final Map<Long, Order> ordersPerId;

    public InMemoryOrderRegistry() {
        this.ordersPerPrice = new ConcurrentHashMap<>();
        this.ordersPerId = new ConcurrentHashMap<>();
    }

    public Order add(OrderRequest request) {
        Order order = of(request, UUID.randomUUID().getLeastSignificantBits());
        ordersPerPrice.putIfAbsent(order.pricePerKg, new CopyOnWriteArrayList<>());
        ordersPerPrice.get(order.pricePerKg).add(order);
        ordersPerId.put(order.orderId, order);
        return order;
    }

    public void remove(Long orderId) {
        Order order = ofNullable(ordersPerId.remove(orderId)).orElseThrow(OrderNotFoundException::new);
        ordersPerPrice.get(order.pricePerKg).remove(order);
        if(ordersPerPrice.get(order.pricePerKg).isEmpty()) {
            ordersPerPrice.remove(order.pricePerKg);
        }
    }

    public Set<BigDecimal> getOrdersPrices() {
        return ordersPerPrice.keySet();
    }

    public List<Order> getBuyOrdersByPrice(BigDecimal price) {
        return ordersPerPrice.getOrDefault(price, Collections.emptyList()).stream()
                .filter(order -> order.orderType == BUY)
                .collect(Collectors.toList());
    }

    public List<Order> getSellOrdersByPrice(BigDecimal price) {
        return ordersPerPrice.getOrDefault(price, Collections.emptyList()).stream()
                .filter(order -> order.orderType == SELL)
                .collect(Collectors.toList());
    }
}
