package com.jsyh.onlineshopping.presenter;

import android.content.Context;

import com.jsyh.onlineshopping.config.ConfigValue;
import com.jsyh.onlineshopping.http.BaseDelegate;
import com.jsyh.onlineshopping.http.ExceptionHelper;
import com.jsyh.onlineshopping.http.OkHttpClientManager;
import com.jsyh.onlineshopping.model.LoginModel;
import com.jsyh.onlineshopping.utils.Utils;
import com.jsyh.onlineshopping.views.LoginView;
import com.squareup.okhttp.Request;

import java.util.Map;

/**
 * Created by Administrator on 2015/8/31.
 */
public class LoginPresenter extends BasePresenter{
private LoginView loginView;
    protected Context context;// 上下文
//    protected RequestQueue mQueue;// 网络请求队列
    public LoginPresenter(LoginView loginView) {
//        this.mQueue = Volley.newRequestQueue(context);
        this.loginView=loginView;
    }
    public void toLogin() {
        loginView.to_main();
    }
    public void toRegister() {
        loginView.to_register();
    }
    public void toForget(){loginView.to_forget();}
    public void loadLogin(final Context context,String name,String password){
        Map<String,String> params = getDefaultMD5Params("user", "passwd");
        params.put("user",name);
        params.put("passwd", password);
        OkHttpClientManager.postAsyn(context, ConfigValue.APP_IP
                + "user/login", params, new BaseDelegate.ResultCallback<LoginModel>() {
            @Override
            public void onError(Request request, Object tag, Exception e) {
                Utils.showToast(context, ExceptionHelper.getMessage(e,context));
            }

            @Override
            public void onResponse(LoginModel response, Object tag) {
                loginView.login(response);
            }
        },true);
    }
}
