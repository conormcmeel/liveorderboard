package com.bars.silver;

import com.bars.silver.exceptions.OrderNotFoundException;
import com.bars.silver.model.Order;
import com.bars.silver.model.OrderRequest;
import com.bars.silver.model.OrderSummary;
import com.bars.silver.repository.InMemoryOrderRegistry;
import com.bars.silver.repository.OrderRegistry;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.bars.silver.model.Order.OrderType.BUY;
import static com.bars.silver.model.Order.OrderType.SELL;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;

public class OrderBoardServiceTest {

    private static final BigDecimal QUANTITY_ONE = ONE;
    private static final BigDecimal QUANTITY_TEN = TEN;
    private static final BigDecimal PRICE_ONE = ONE;
    private static final BigDecimal PRICE_ZERO = ZERO;
    private static final BigDecimal PRICE_TEN = TEN;
    private static final String USER_1 = "user1";
    private static final String USER_2 = "user2";

    private OrderBoardService orderBoardService;

    @Before
    public void setUp() {
        OrderRegistry orderRegistry = new InMemoryOrderRegistry();
        orderBoardService = new OrderBoardService(orderRegistry);
    }

    @Test
    public void registerNewOrder() {
        OrderRequest orderRequest = new OrderRequest(USER_1, QUANTITY_ONE, PRICE_ONE, SELL);

        Order order = orderBoardService.registerOrder(orderRequest);

        assertThat(order.orderId).isNotNull();
        assertThat(order.userId).isEqualTo(USER_1);
        assertThat(order.quantity).isEqualTo(QUANTITY_ONE);
        assertThat(order.pricePerKg).isEqualTo(PRICE_ONE);
        assertThat(order.orderType).isEqualTo(SELL);
    }

    @Test
    public void buyOrdersInOrder() {
        orderBoardService.registerOrder(new OrderRequest(USER_1, QUANTITY_ONE, PRICE_ONE, BUY));
        orderBoardService.registerOrder(new OrderRequest(USER_2, QUANTITY_TEN, PRICE_TEN, BUY));

        List<OrderSummary> buyOrders = orderBoardService.getBuyOrders();

        assertThat(buyOrders).hasSize(2);
        assertThat(buyOrders.get(0).quantity).isEqualTo(QUANTITY_TEN);
        assertThat(buyOrders.get(0).pricePerKg).isEqualTo(PRICE_TEN);
        assertThat(buyOrders.get(0).orderType).isEqualTo(BUY);
        assertThat(buyOrders.get(1).quantity).isEqualTo(QUANTITY_ONE);
        assertThat(buyOrders.get(1).pricePerKg).isEqualTo(PRICE_ONE);
        assertThat(buyOrders.get(1).orderType).isEqualTo(BUY);
    }

    @Test
    public void buyOrdersAreGroupedByPrice() {
        orderBoardService.registerOrder(new OrderRequest(USER_1, QUANTITY_ONE, PRICE_TEN, BUY));
        orderBoardService.registerOrder(new OrderRequest(USER_2, QUANTITY_TEN, PRICE_TEN, BUY));
        orderBoardService.registerOrder(new OrderRequest(USER_2, QUANTITY_TEN, PRICE_ONE, BUY));
        BigDecimal cumulatedQuantityForPriceTen = new BigDecimal("11");

        List<OrderSummary> buyOrders = orderBoardService.getBuyOrders();

        assertThat(buyOrders).hasSize(2);
        assertThat(buyOrders.get(0).quantity).isEqualTo(cumulatedQuantityForPriceTen);
    }

    @Test
    public void emptySummaryListForNoBuyOrders() {
        orderBoardService.registerOrder(new OrderRequest(USER_1, QUANTITY_ONE, PRICE_TEN, SELL));
        orderBoardService.registerOrder(new OrderRequest(USER_2, QUANTITY_TEN, PRICE_TEN, SELL));
        orderBoardService.registerOrder(new OrderRequest(USER_2, QUANTITY_TEN, PRICE_ONE, SELL));

        List<OrderSummary> buyOrders = orderBoardService.getBuyOrders();

        assertThat(buyOrders).isEmpty();
    }

    @Test
    public void sellOrdersInOrder() {
        orderBoardService.registerOrder(new OrderRequest(USER_1, QUANTITY_ONE, PRICE_ONE, SELL));
        orderBoardService.registerOrder(new OrderRequest(USER_2, QUANTITY_TEN, PRICE_TEN, SELL));

        List<OrderSummary> sellOrders = orderBoardService.getSellOrders();

        assertThat(sellOrders).hasSize(2);
        assertThat(sellOrders.get(0).quantity).isEqualTo(QUANTITY_ONE);
        assertThat(sellOrders.get(0).pricePerKg).isEqualTo(PRICE_ONE);
        assertThat(sellOrders.get(0).orderType).isEqualTo(SELL);
        assertThat(sellOrders.get(1).quantity).isEqualTo(QUANTITY_TEN);
        assertThat(sellOrders.get(1).pricePerKg).isEqualTo(PRICE_TEN);
        assertThat(sellOrders.get(1).orderType).isEqualTo(SELL);
    }

    @Test
    public void sellOrdersAreGroupedByPrice() {
        orderBoardService.registerOrder(new OrderRequest(USER_1, QUANTITY_TEN, PRICE_ONE, SELL));
        orderBoardService.registerOrder(new OrderRequest(USER_2, QUANTITY_TEN, PRICE_ONE, SELL));
        orderBoardService.registerOrder(new OrderRequest(USER_2, QUANTITY_TEN, PRICE_TEN, SELL));
        BigDecimal accumulatedQuantityForPriceTen = new BigDecimal("20");

        List<OrderSummary> sellOrders = orderBoardService.getSellOrders();

        assertThat(sellOrders).hasSize(2);
        assertThat(sellOrders.get(0).quantity).isEqualTo(accumulatedQuantityForPriceTen);
    }

    @Test
    public void getEmptySummaryListForNoSellOrders() {
        orderBoardService.registerOrder(new OrderRequest(USER_1, QUANTITY_ONE, PRICE_TEN, BUY));
        orderBoardService.registerOrder(new OrderRequest(USER_2, QUANTITY_TEN, PRICE_TEN, BUY));
        orderBoardService.registerOrder(new OrderRequest(USER_2, QUANTITY_TEN, PRICE_ONE, BUY));

        List<OrderSummary> sellOrders = orderBoardService.getSellOrders();

        assertThat(sellOrders).isEmpty();
    }

    @Test(expected = OrderNotFoundException.class)
    public void throwExceptionWhenCancellingNonExistentOrder() {
        Long dummyOrderId = 1234L;

        orderBoardService.cancelOrder(dummyOrderId);
    }

    @Test
    public void cancelOrder() {
        Order order = orderBoardService.registerOrder(new OrderRequest(USER_1, QUANTITY_ONE, PRICE_TEN, BUY));

        orderBoardService.cancelOrder(order.orderId);

        assertThat(orderBoardService.getBuyOrders()).isEmpty();
    }
}
