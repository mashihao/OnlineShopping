package com.jsyh.onlineshopping.presenter;

import android.content.Context;

import com.jsyh.onlineshopping.config.ConfigValue;
import com.jsyh.onlineshopping.http.BaseDelegate;
import com.jsyh.onlineshopping.http.ExceptionHelper;
import com.jsyh.onlineshopping.http.OkHttpClientManager;
import com.jsyh.onlineshopping.model.AreaModel;
import com.jsyh.onlineshopping.utils.Utils;
import com.jsyh.onlineshopping.views.SelectAreaView;
import com.squareup.okhttp.Request;

import java.util.Map;

/**
 * Created by sks on 2015/10/9.
 */
public class SelectAreaPresenter extends BasePresenter {
    private SelectAreaView selectAreaView;
    public SelectAreaPresenter(SelectAreaView selectAreaView){
        this.selectAreaView = selectAreaView;
    }
    public void loadArea(final Context context,String id,String action){
        Map<String,String> params = getDefaultMD5Params("order", action);
        params.put("region_id",id);
        OkHttpClientManager.postAsyn(context, ConfigValue.APP_IP + "order/" +
                action, params, new BaseDelegate.ResultCallback<AreaModel>() {
            @Override
            public void onError(Request request, Object tag, Exception e) {
                Utils.showToast(context, ExceptionHelper.getMessage(e, context));
            }

            @Override
            public void onResponse(AreaModel response, Object tag) {
                selectAreaView.getArea(response);
            }
        },true);
    }
}
