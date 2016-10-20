package com.jsyh.onlineshopping.model;

/**
 * Created by sks on 2015/9/21.
 */
public class UserInfor {

    private String address;

    private String image;

    private String nick_name;

    private String sex;

    private String email;

    private String user_id;

    private String integration;//积分

    private String user_money;//余额

    private String attention;//关注

    private String cart_num;//购物车产品数量

    private String pay;//待付款数量

    private String shipping;//待收货

    private String shipping_send;//待发货

    private String birthday;//生日

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getIntegration() {
        return integration;
    }

    public void setIntegration(String integration) {
        this.integration = integration;
    }

    public String getUser_money() {
        return user_money;
    }

    public void setUser_money(String user_money) {
        this.user_money = user_money;
    }

    public String getAttention() {
        return attention;
    }

    public void setAttention(String attention) {
        this.attention = attention;
    }

    public String getCart_num() {
        return cart_num;
    }

    public void setCart_num(String cart_num) {
        this.cart_num = cart_num;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public String getShipping() {
        return shipping;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

    public String getShipping_send() {
        return shipping_send;
    }

    public void setShipping_send(String shipping_send) {
        this.shipping_send = shipping_send;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

}
