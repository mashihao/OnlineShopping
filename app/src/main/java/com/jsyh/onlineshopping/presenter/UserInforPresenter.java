package com.jsyh.onlineshopping.presenter;

import android.content.Context;

import com.jsyh.onlineshopping.config.ConfigValue;
import com.jsyh.onlineshopping.http.BaseDelegate;
import com.jsyh.onlineshopping.http.ExceptionHelper;
import com.jsyh.onlineshopping.http.OkHttpClientManager;
import com.jsyh.onlineshopping.model.UserInforModel;
import com.jsyh.onlineshopping.utils.Utils;
import com.jsyh.onlineshopping.views.UserInforView;
import com.squareup.okhttp.Request;

import java.util.Map;

/**
 * Created by sks on 2015/9/29.
 * 用户信息网络请求
 */
public class UserInforPresenter extends BasePresenter {
    private UserInforView userInforView;
    public UserInforPresenter(UserInforView userInforView){
        this.userInforView = userInforView;
    }
    public void loadInfor(final Context context){
        initLoadDialog(context);
        mLoadingDialog.show();
        Map<String,String> params = getDefaultMD5Params("user","infomation");
        params.put("key", ConfigValue.DATA_KEY);
        OkHttpClientManager.postAsyn(context, ConfigValue.APP_IP + "user/userinfo", params,
                new BaseDelegate.ResultCallback<UserInforModel>() {
                    @Override
                    public void onError(Request request, Object tag, Exception e) {
                        Utils.showToast(context, ExceptionHelper.getMessage(e,context));
                        mLoadingDialog.dismiss();
                    }

                    @Override
                    public void onResponse(UserInforModel response, Object tag) {
                        mLoadingDialog.dismiss();
                        userInforView.inforData(response);
                    }
                },true);
    }
}
