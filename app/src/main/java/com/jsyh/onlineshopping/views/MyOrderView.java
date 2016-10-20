package com.jsyh.onlineshopping.views;

import com.jsyh.onlineshopping.model.OrderModel;

/**
 * Created by sks on 2015/9/29.
 */
public interface MyOrderView {
    void error();
    void orderList(OrderModel model);
}
