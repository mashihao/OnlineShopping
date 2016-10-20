package com.jsyh.onlineshopping.model;

import java.io.Serializable;

/**
 * Created by sks on 2015/10/22.
 */
public class SubmitOrder implements Serializable {

    private String order_sn;//订单编号
    private String money_paid;//实际付款费用

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }

    public String getMoney_paid() {
        return money_paid;
    }

    public void setMoney_paid(String money_paid) {
        this.money_paid = money_paid;
    }
}
