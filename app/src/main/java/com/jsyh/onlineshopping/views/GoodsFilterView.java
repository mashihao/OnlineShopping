package com.jsyh.onlineshopping.views;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.jsyh.onlineshopping.model.FilterInfoModel;
import com.jsyh.onlineshopping.model.GoodsInfoModel;

/**
 * 商品筛选 V
 */
public interface GoodsFilterView {

    /**
     * 0 : linear model
     * 1 : gride model
     * @param model
     */
    void onLayoutSwitch(int model,int firstPosition);

    /**
     * 筛选数据的回调
     * @param model
     * @param e
     */
    void onFilterGoodsData(@Nullable GoodsInfoModel model ,@Nullable Exception e);


    void onDrawerData(@Nullable FilterInfoModel model, @Nullable Exception e);



}
