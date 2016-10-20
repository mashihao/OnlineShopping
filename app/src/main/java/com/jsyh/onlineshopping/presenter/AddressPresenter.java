package com.jsyh.onlineshopping.presenter;

import android.content.Context;

import com.jsyh.onlineshopping.config.ConfigValue;
import com.jsyh.onlineshopping.http.BaseDelegate;
import com.jsyh.onlineshopping.http.ExceptionHelper;
import com.jsyh.onlineshopping.http.OkHttpClientManager;
import com.jsyh.onlineshopping.model.AddressModel;
import com.jsyh.onlineshopping.utils.Utils;
import com.jsyh.onlineshopping.views.AddressView;
import com.squareup.okhttp.Request;

import java.util.Map;

/**
 * Created by sks on 2015/9/24.
 * 地址列表网络请求
 */
public class AddressPresenter extends BasePresenter {
    private AddressView addressView;
    public AddressPresenter(AddressView addressView){
        this.addressView = addressView;
    }
    public void setAddressData(final Context context){
        initLoadDialog(context);
        mLoadingDialog.show();
        Map<String, String> params = getDefaultMD5Params("goods", "addresslist");
        params.put("key", ConfigValue.DATA_KEY);
        OkHttpClientManager.postAsyn(context, ConfigValue.APP_IP + "goods/addresslist",
                params, new BaseDelegate.ResultCallback<AddressModel>() {
                    @Override
                    public void onError(Request request, Object tag, Exception e) {
                        mLoadingDialog.dismiss();
                        Utils.showToast(context, ExceptionHelper.getMessage(e, context));
                    }

                    @Override
                    public void onResponse(AddressModel response, Object tag) {
                        mLoadingDialog.dismiss();
                        addressView.getAddressList(response);
                    }
                },true);
    }
}
