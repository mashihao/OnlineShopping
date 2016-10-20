package com.jsyh.onlineshopping.presenter;

import android.content.Context;

import com.jsyh.onlineshopping.config.ConfigValue;
import com.jsyh.onlineshopping.http.BaseDelegate;
import com.jsyh.onlineshopping.http.ExceptionHelper;
import com.jsyh.onlineshopping.http.OkHttpClientManager;
import com.jsyh.onlineshopping.model.PayModel;
import com.jsyh.onlineshopping.utils.Utils;
import com.jsyh.onlineshopping.views.PayView;
import com.squareup.okhttp.Request;

import java.util.Map;

/**
 * Created by sks on 2015/10/28.
 * 充值请求
 */
public class RechargePresenter extends BasePresenter {

    private PayView payView;

    public RechargePresenter(PayView payView){
        this.payView = payView;
    }

    public void request(final Context context,String money){
        initLoadDialog(context);
        mLoadingDialog.show();
        Map<String, String> params = getDefaultMD5Params("user", "recharge");
        params.put("key", ConfigValue.DATA_KEY);
        params.put("price",money);
        OkHttpClientManager.postAsyn(context, ConfigValue.APP_IP + "user/recharge",
                params, new BaseDelegate.ResultCallback<PayModel>() {
                    @Override
                    public void onError(Request request, Object tag, Exception e) {
                        mLoadingDialog.dismiss();
                        Utils.showToast(context, ExceptionHelper.getMessage(e,context));
                    }

                    @Override
                    public void onResponse(PayModel response, Object tag) {
                        mLoadingDialog.dismiss();
                        payView.result(response);
                    }
                },true);
    }
}
