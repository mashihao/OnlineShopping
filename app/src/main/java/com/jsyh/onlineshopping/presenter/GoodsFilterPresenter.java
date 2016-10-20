package com.jsyh.onlineshopping.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.jsyh.onlineshopping.config.ConfigValue;
import com.jsyh.onlineshopping.config.SPConfig;
import com.jsyh.onlineshopping.http.BaseDelegate;
import com.jsyh.onlineshopping.http.OkHttpClientManager;
import com.jsyh.onlineshopping.model.FilterInfoModel;
import com.jsyh.onlineshopping.model.GoodsInfoModel;
import com.jsyh.onlineshopping.utils.SPUtils;
import com.jsyh.onlineshopping.utils.Utils;
import com.jsyh.onlineshopping.views.GoodsFilterView;
import com.squareup.okhttp.Request;

import java.net.ConnectException;
import java.util.Map;

/**
 * 商品筛选 P
 */
public class GoodsFilterPresenter extends BasePresenter{


    public static final String SEARCH_TYPE = "search";

    private GoodsFilterView mView;

    private Gson  mGson = new Gson();



    public GoodsFilterPresenter(GoodsFilterView mView) {
        this.mView = mView;
    }

    /**
     *
     * @param type
     *          需要获取的类型（type=search）
     *
     * @param keyword
     *          要查询的关键词
     *
     * @param page
     *         当前页
     *
     * @param order
     *          排序的类型
     *          0综合排序
     *          1销量排序
     *          2价格由低到高
     *          3价格又高到低
     *          4人气，
     *          （默认为0）
     *
     * @param filterParams
     *          用户帅选要求（数组）
     * @param maintype
     *          首页跳转过来的分类类型，有new：新发现，hot：热销产品，best：店长推荐
     */
    public void loadGoodsDatas(Context context,@NonNull String type , @NonNull String keyword ,
                               @Nullable String id,
                               @NonNull String page,
                               @Nullable String order, @Nullable Map<String,String> filterParams,
                               @Nullable String maintype ) {

        int networkAvailable = Utils.isNetworkAvailable(context);
        if (Utils.NO_NETWORK_STATE == networkAvailable) {

            mView.onFilterGoodsData(null ,new ConnectException());
            return;
        }

        Map<String, String> params = getDefaultMD5Params("first", "index");

        if (params == null) return;

        params.put("type", type);
        params.put("page", page);        //当前页码
        params.put("keyword", keyword);
        if (!TextUtils.isEmpty(id)) {

            params.put("cat_id", id);
        }
//        params.put("c", maintype);
        if (!TextUtils.isEmpty(maintype)) {

            params.put("c", maintype);
        }

        if (!TextUtils.isEmpty(order)) {
            params.put("order", order);
        }

        if (filterParams != null && !filterParams.isEmpty()) {
            params.put("filtrate", mGson.toJson(filterParams));

        }

        if (mLoadingDialog == null) {
            initLoadDialog(context);
        }

        mLoadingDialog.show();

        OkHttpClientManager.postAsyn(context, ConfigValue.CATEGORY_FILTER, params, new BaseDelegate.ResultCallback<GoodsInfoModel>() {
            @Override
            public void onError(Request request, Object tag, Exception e) {
                mView.onFilterGoodsData(null, e);
            }

            @Override
            public void onResponse(GoodsInfoModel response, Object tag) {
                mView.onFilterGoodsData(response, null);

            }
        });


    }

    /**
     * 本地保存的 用户配置。
     * @param context
     * @return
     */
    public void loadLayoutModel(Context context,int firstPosition) {
        mView.onLayoutSwitch((Integer) SPUtils.get(context, SPConfig.GOODS_SHOW_MODEL_KEY, firstPosition), firstPosition);
    }


    /**
     *加载 侧滑数据
     * @param keyword
     */
    public void loadFilterDataForDrawer(Context context , @NonNull String keyword ,String goods_type,String id) {

        if (Utils.NO_NETWORK_STATE == Utils.isNetworkAvailable(context)) {
            mView.onDrawerData(null,new ConnectException());

            return;
        }

        Map<String, String> params = getDefaultMD5Params("first", "index");
        if (params == null) return;

        params.put("keyword", keyword);
        params.put("type", "fitrate");
        params.put("goods_type",goods_type);
        params.put("classify_id",id);


        OkHttpClientManager.postAsyn(context, ConfigValue.CATEGORY_FILTER, params, new BaseDelegate.ResultCallback<FilterInfoModel>() {
            @Override
            public void onError(Request request, Object tag, Exception e) {

                mView.onDrawerData(null, e);
            }

            @Override
            public void onResponse(FilterInfoModel response, Object tag) {
                mView.onDrawerData(response, null);
            }
        }, "drawer" + keyword);


    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
