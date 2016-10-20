package com.jsyh.onlineshopping.views;

import com.jsyh.onlineshopping.model.BaseModel;
import com.jsyh.onlineshopping.model.CartGoodsModel;

/**
 * Created by Pisces on 2015/10/9.
 */
public interface CartGoodsView {
    void getCartGoodsList(CartGoodsModel cartGoodsModel);

    void alterCartGoodsNumber(BaseModel baseModel);

    void deleteCartGoods(BaseModel baseModel);
}
