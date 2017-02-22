package com.bars.silver.model;

import org.junit.Test;

import static com.bars.silver.model.Order.OrderType.BUY;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;

public class OrderRequestTest {

    public static final String USER_1 = "userId1";

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionWhenCreatingOrderRequestWithoutUserId() {
        new OrderRequest(null, ONE, TEN, BUY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionWhenCreatingOrderRequestWithoutQuantity() {
        new OrderRequest(USER_1, null, TEN, BUY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionWhenCreatingOrderRequestWithoutPricePerKilo() {
        new OrderRequest(USER_1, ONE, null, BUY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionWhenCreatingOrderRequestWithoutType() {
        new OrderRequest(USER_1, ONE, TEN, null);
    }
}
