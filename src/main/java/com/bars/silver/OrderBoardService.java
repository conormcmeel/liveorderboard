package com.bars.silver;

import com.bars.silver.model.Order;
import com.bars.silver.model.OrderRequest;
import com.bars.silver.model.OrderSummary;
import com.bars.silver.repository.OrderRegistry;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OrderBoardService {

    private final OrderRegistry orderRegistry;

    public OrderBoardService(OrderRegistry orderRegistry) {
        this.orderRegistry = orderRegistry;
    }

    public Order registerOrder(OrderRequest request) {
        return orderRegistry.add(request);
    }

    public void cancelOrder(Long orderId) {
        orderRegistry.remove(orderId);
    }

    public List<OrderSummary> getBuyOrders() {
        return orderRegistry.getOrdersPrices().stream()
                .map(orderRegistry::getBuyOrdersByPrice)
                .filter(orders -> !orders.isEmpty())
                .map(OrderSummary::of)
                .sorted(Comparator.comparing((OrderSummary os) -> os.pricePerKg).reversed())
                .collect(Collectors.toList());
    }

    public List<OrderSummary> getSellOrders() {
        return orderRegistry.getOrdersPrices().stream()
                .map(orderRegistry::getSellOrdersByPrice)
                .filter(orders -> !orders.isEmpty())
                .map(OrderSummary::of)
                .sorted(Comparator.comparing(os -> os.pricePerKg))
                .collect(Collectors.toList());
    }
}