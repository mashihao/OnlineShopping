package com.jsyh.onlineshopping.model;

import java.util.ArrayList;

/**
 * Created by Su on 2015/10/9.
 */
public class CartGoods {
    private String rec_id;//购物车列表唯一标识

    private String goods_id;//商品id

    private String goods_name;//商品名称

    private String goods_price;//商品价格

    private String goods_img;//商品图片

    private int number;//商品数量

    public ArrayList<String> getAttrvalue_id() {
        return attrvalue_id;
    }

    public void setAttrvalue_id(ArrayList<String> attrvalue_id) {
        this.attrvalue_id = attrvalue_id;
    }

    private ArrayList<String> attrvalue_id;//加入购物车时选择的商品属性

    public String getRec_id() {
        return rec_id;
    }

    public void setRec_id(String rec_id) {
        this.rec_id = rec_id;
    }


    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getGoods_price() {
        return goods_price;
    }

    public void setGoods_price(String goods_price) {
        this.goods_price = goods_price;
    }

    public String getGoods_img() {
        return goods_img;
    }

    public void setGoods_img(String goods_img) {
        this.goods_img = goods_img;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }


}
