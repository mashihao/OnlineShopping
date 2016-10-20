package com.jsyh.onlineshopping.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liang on 2015/9/17.
 * <p/>
 * 商品信息 model
 */
public class GoodsInfoModel2 extends BaseModel {

    public GoodsInfo data;

    public class GoodsInfo {

        public String goods_name;
        public String goods_sn;
        public String is_real; //是否为实物1是0否
        public String[] album;
        public String content;
        public String coupon_price;
        public String shop_price;
        public String total_price;
        public String is_shipping;
        public ArrayList<Params> param;
        public String sales;
        public List<Attribute> attribute;
        public int is_attention;//是否已经关注
    }

    public class Attribute {
        public String attr_name;
        public String attr_id;
        public List<Attr_Value> attr_value;

        public class Attr_Value {
            public String attr_value_id;
            public String attr_value_name;
            public String attr_value_price;
        }
    }

    public class Params implements Serializable {
        /**
         * "attr_name_id": "189",
         * "attr_name": "屏幕大小",
         * "attr_value_id": "216",
         * "attr_value": "2.0英寸"
         */
        public String attr_name_id;
        public String attr_name;
        public String attr_value_id;
        public String attr_value;
    }

}
