package com.jsyh.onlineshopping.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jsyh.onlineshopping.activity.me.LoginActivity;

/**
 * Created by Administrator on 2015/8/31.
 */
public  class BaseActivity extends AppCompatActivity{
    protected Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        initData();
        initView();
        initTitle();


    }
    public void initData(){

    }
    public void initView(){

    }
    public void initTitle(){

    }

    public void itLogin(Context context){
        Intent itLogin = new Intent(context, LoginActivity.class);
        startActivity(itLogin);
    }
}
