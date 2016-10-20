package com.jsyh.onlineshopping.presenter;

import android.content.Context;
import android.util.Log;

import com.jsyh.onlineshopping.config.ConfigValue;
import com.jsyh.onlineshopping.config.SPConfig;
import com.jsyh.onlineshopping.http.BaseDelegate;
import com.jsyh.onlineshopping.http.ExceptionHelper;
import com.jsyh.onlineshopping.http.OkHttpClientManager;
import com.jsyh.onlineshopping.model.HotSearchModel;
import com.jsyh.onlineshopping.utils.ObjectSerializer;
import com.jsyh.onlineshopping.utils.SPUtils;
import com.jsyh.onlineshopping.views.SearchView;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 搜索  layer
 */
public class SearchPresenter extends BasePresenter{



    private SearchView mSearchView;

    public SearchPresenter(SearchView mSearchView) {
        this.mSearchView = mSearchView;
    }

    public void loadHotsTag(final Context context) {

        Map<String, String> params = getDefaultMD5Params("first", "keywords");

        if (params == null) return;

        OkHttpClientManager.postAsyn(context, ConfigValue.HOT_SEARCH, params, new BaseDelegate.ResultCallback<HotSearchModel>() {
            @Override
            public void onError(Request request, Object tag, Exception e) {
                ExceptionHelper.getMessage(e, context);
            }

            @Override
            public void onResponse(HotSearchModel response, Object tag) {
                if (response != null) {
                    mSearchView.onHostTag(response.getData());

                }
            }
        });

    }





    /**
     * 加载历史搜索
     * @param context
     */
    public void loadHistory(Context context) {
//        ArrayList<String> sets = new ArrayList<>();

        /*sets.add(new SearchHistoryModel("诺基亚"));
        sets.add( new SearchHistoryModel("摩托罗拉"));
        sets.add(new SearchHistoryModel("苹果"));*/
//        sets.add("诺基亚");
//        sets.add("摩托罗拉");
//        sets.add("苹果");


        try {

            String o = (String) SPUtils.get(context, SPConfig.SEARCH_HISTORY_KEY, ObjectSerializer.serialize(new ArrayList<String>()));

            ArrayList<String> datas = (ArrayList<String>) ObjectSerializer.deserialize(o);
            mSearchView.onHistory(datas);


        } catch (IOException e) {
            mSearchView.onHistory(null);
            e.printStackTrace();
        }




    }


    public void loadAutoComplete() {

        Log.d("tag", "load  data .....");


    }





}
