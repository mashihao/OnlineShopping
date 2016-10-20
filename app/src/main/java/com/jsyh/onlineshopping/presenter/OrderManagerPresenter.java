package com.jsyh.onlineshopping.presenter;

import android.content.Context;

import com.jsyh.onlineshopping.config.ConfigValue;
import com.jsyh.onlineshopping.http.BaseDelegate;
import com.jsyh.onlineshopping.http.ExceptionHelper;
import com.jsyh.onlineshopping.http.OkHttpClientManager;
import com.jsyh.onlineshopping.model.BaseModel;
import com.jsyh.onlineshopping.utils.Utils;
import com.jsyh.onlineshopping.views.OrderManagerView;
import com.squareup.okhttp.Request;

import java.util.Map;

/**
 * Created by sks on 2015/10/21.
 * 对订单的状态修改请求：如取消，确认
 */
public class OrderManagerPresenter extends BasePresenter {
    private OrderManagerView orderManagerView;
    private String url = ConfigValue.APP_IP;

    public OrderManagerPresenter(OrderManagerView orderManagerView){
        this.orderManagerView = orderManagerView;
    }

    //取消订单
    public void cancelOrder(final Context context,String orderId){
        Map<String,String> params = getDefaultMD5Params("order","qorder");
        params.put("key",ConfigValue.DATA_KEY);
        params.put("order_id",orderId);
        OkHttpClientManager.postAsyn(context, url + "order/qorder", params, new BaseDelegate.ResultCallback<BaseModel>() {
            @Override
            public void onError(Request request, Object tag, Exception e) {
                Utils.showToast(context, ExceptionHelper.getMessage(e, context));
            }

            @Override
            public void onResponse(BaseModel response, Object tag) {
                orderManagerView.result(response);
            }
        });
    }
    //确认订单
    public void sureOrder(final Context context,String orderId){
        Map<String,String> params = getDefaultMD5Params("order","received");
        params.put("key",ConfigValue.DATA_KEY);
        params.put("order_id",orderId);
        OkHttpClientManager.postAsyn(context, url + "order/received", params, new BaseDelegate.ResultCallback<BaseModel>() {
            @Override
            public void onError(Request request, Object tag, Exception e) {
                Utils.showToast(context, ExceptionHelper.getMessage(e,context));
            }

            @Override
            public void onResponse(BaseModel response, Object tag) {
                orderManagerView.result(response);
            }
        });
    }
    //退货
    public void returnOrder(final Context context,String orderId){
        Map<String,String> params = getDefaultMD5Params("order","back_goods");
        params.put("key",ConfigValue.DATA_KEY);
        params.put("order_id",orderId);
        OkHttpClientManager.postAsyn(context, url + "order/back_goods", params, new BaseDelegate.ResultCallback<BaseModel>() {
            @Override
            public void onError(Request request, Object tag, Exception e) {
                Utils.showToast(context, ExceptionHelper.getMessage(e,context));
            }

            @Override
            public void onResponse(BaseModel response, Object tag) {
                orderManagerView.result(response);
            }
        });
    }
    //放回购物车
    public void cartOrder(final Context context,String orderId){
        Map<String,String> params = getDefaultMD5Params("order","recart");
        params.put("key",ConfigValue.DATA_KEY);
        params.put("order_id",orderId);
        OkHttpClientManager.postAsyn(context, url + "order/recart", params, new BaseDelegate.ResultCallback<BaseModel>() {
            @Override
            public void onError(Request request, Object tag, Exception e) {
                Utils.showToast(context, ExceptionHelper.getMessage(e,context));
            }

            @Override
            public void onResponse(BaseModel response, Object tag) {
                orderManagerView.result(response);
            }
        });
    }
}
