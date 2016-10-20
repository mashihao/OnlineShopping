package com.jsyh.onlineshopping.presenter;

import android.content.Context;

import com.jsyh.onlineshopping.config.ConfigValue;
import com.jsyh.onlineshopping.http.BaseDelegate;
import com.jsyh.onlineshopping.http.ExceptionHelper;
import com.jsyh.onlineshopping.http.OkHttpClientManager;
import com.jsyh.onlineshopping.model.DistributionModel;
import com.jsyh.onlineshopping.utils.Utils;
import com.jsyh.onlineshopping.views.DistributionView;
import com.squareup.okhttp.Request;

import java.util.Map;

/**
 * Created by sks on 2015/10/10.
 * 获取配送方式网络请求
 */
public class DistributionPresenter extends BasePresenter {

    private DistributionView distributionView;
    public DistributionPresenter(DistributionView distributionView){
        this.distributionView = distributionView;
    }
    public void loadDistributionList(final Context context){
        initLoadDialog(context);
        mLoadingDialog.show();
        Map<String, String> params = getDefaultMD5Params("order", "delivery");
        params.put("key", ConfigValue.DATA_KEY);
        OkHttpClientManager.postAsyn(context, ConfigValue.APP_IP + "order/delivery",
                params, new BaseDelegate.ResultCallback<DistributionModel>() {
                    @Override
                    public void onError(Request request, Object tag, Exception e) {
                        mLoadingDialog.dismiss();
                        Utils.showToast(context, ExceptionHelper.getMessage(e, context));
                    }

                    @Override
                    public void onResponse(DistributionModel response, Object tag) {
                        mLoadingDialog.dismiss();
                        distributionView.getData(response);
                    }
                },true);
    }
}
