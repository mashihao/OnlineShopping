package com.jsyh.onlineshopping.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jsyh.onlineshopping.R;
import com.jsyh.onlineshopping.config.SPConfig;
import com.jsyh.onlineshopping.model.SearchModel;
import com.jsyh.onlineshopping.presenter.SearchPresenter;
import com.jsyh.onlineshopping.utils.DensityUtils;
import com.jsyh.onlineshopping.utils.KeyBoardUtils;
import com.jsyh.onlineshopping.utils.ObjectSerializer;
import com.jsyh.onlineshopping.utils.SPUtils;
import com.jsyh.onlineshopping.views.SearchView;
import com.jsyh.shopping.uilibrary.ClearEditText;
import com.jsyh.shopping.uilibrary.adapter.listview.BaseAdapterHelper;
import com.jsyh.shopping.uilibrary.adapter.listview.QuickAdapter;
import com.jsyh.shopping.uilibrary.tagview.TagGroup;
import com.jsyh.shopping.uilibrary.uiutils.ListViewUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索界面
 */
public class SearchActivity extends AppCompatActivity implements View.OnClickListener,
        TagGroup.OnTagClickListener,
        ClearEditText.SearchListener,
        SearchView, AdapterView.OnItemClickListener {



    private SearchPresenter mPresenter;


    private final int HOST = 0;
    private final int HISTORY = 1;
    private final int SEARCH = 2;
    private int  currentView = -1;

    // -----------------头部---------------------
    private ImageView       mBack;              //返回
    private ClearEditText   mClearEditText;     //搜索框
    private TextView        mSearAction;        //搜索

    //----------------- 热搜 -----------------
    private ViewStub        mHostViewStub;
    private View            mHostView;

    private TagGroup        mTagGroup;
    private List<String>    mHostDatas;




    //----------------- 历史 -----------------
    private ViewStub                    mHistoryViewStub;
    private View                        mHistoryView;

    private LinearLayout                mHistoryHostLayout;     //热搜
    //private LinearLayoutCompat  mHistorySearchLayout;     //历史列表
    private ListView                    mHistorySearchList;
    private QuickAdapter<String>        mHistoryAdapter;
    private Button                      mClearHistory;          //清空历史
    private ArrayList<String>           mHistoryDatas;          //历史数据



    //----------------- auto-complete ---------
    private ViewStub                    mAutoCompleteViewStub;
    private View                        mAutoCompleteView;
    private ListView                    mSearchListView;
    private QuickAdapter<SearchModel>   mSearchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_layout);
        mPresenter = new SearchPresenter(this);

        mHostDatas = new ArrayList<>();
        mHistoryDatas = new ArrayList<>();


        // ------------------- head
        mClearEditText = (ClearEditText) findViewById(R.id.cetSearch);
        mClearEditText.setSearchListener(this);

        mSearAction = (TextView) findViewById(R.id.tvSearAction);
        mSearAction.setOnClickListener(this);

        mBack = (ImageView) findViewById(R.id.ivBack);
        mBack.setOnClickListener(this);
        findViewById(R.id.rlback).setOnClickListener(this);

        //---------------------content
        mHostViewStub = (ViewStub) findViewById(R.id.vsHotsLayout);
        mHistoryViewStub = (ViewStub) findViewById(R.id.vsHistoryLayout);
        mAutoCompleteViewStub = (ViewStub) findViewById(R.id.vsAutoCompleteLayout);

        //-----------------------adapter
        mSearchAdapter = new QuickAdapter<SearchModel>(this,R.layout.search_autocomplete_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, SearchModel item) {

            }
        };

        mHistoryAdapter = new QuickAdapter<String>(this,R.layout.search_history_lv_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, String item) {
                helper.setText(R.id.tvHistoryItem, item);
            }
        };


        mPresenter.loadHistory(this);
    }

    @Override
    public void onTagClick(String tag) {
//        if (mHistoryView == null) {
//            mHistoryView = mHistoryViewStub.inflate();
//        }
        addHistorySearchWithDispatch(tag);
//        dispatchGoodsListActivity(tag);

    }



    @Override
    public void onHostTag(List<String> datas) {

        if (mHostDatas.isEmpty()) {

            switch (currentView) {
                case HOST:

                    if (datas != null && !datas.isEmpty()) {
                        mHostDatas.addAll(datas);

                        switchContentView(HOST);
                        mTagGroup.setTags(mHostDatas);
                    }
                    break;

                case HISTORY:
                    initHistoryHost(datas);
                    break;


            }
        }

    }

    @Override
    public void onHistory(List<String> datas) {

        if (datas != null && !datas.isEmpty()) {        //有历史搜索记录

            switchContentView(HISTORY);

            if (mHistoryDatas.isEmpty()) {
                mPresenter.loadHotsTag(this);
            } else {

                initHistoryHost(mHostDatas);
            }

            //----------历史
            if (mHistorySearchList.getAdapter() != null) {
                mHistoryAdapter.clear();
            }

            mHistoryDatas.clear();


            mHistoryDatas.addAll(datas);
            mHistoryAdapter.addAll(datas);
            mHistorySearchList.setAdapter(mHistoryAdapter);
            ListViewUtils.setListViewHeightBasedOnItems(mHistorySearchList);


        } else {
            // 无历史搜索记录
            if (mHostDatas.isEmpty()) {
                currentView = HOST;
                mPresenter.loadHotsTag(this);
            }else {
                switchContentView(HOST);
            }

        }

    }


    private void initHistoryHost(List<String> datas){

        mHistoryHostLayout.removeAllViews();


        final TextView host = new TextView(this);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(DensityUtils.dp2px(this, 5), 0, DensityUtils.dp2px(this, 5), 0);
        host.setLayoutParams(params1);
        host.setText("热搜");
        mHistoryHostLayout.addView(host);


        for (int i = 0; i < datas.size(); i++) {

            final String history = datas.get(i);
            if (TextUtils.isEmpty(history))continue;

            //-----------热搜
            final TextView host1 = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(DensityUtils.dp2px(this, 5), 0, DensityUtils.dp2px(this, 5), 0);
            host1.setLayoutParams(params);

            host1.setText(history);

            host1.setBackgroundResource(R.drawable.tab_group_selector);
            host1.setClickable(true);

            host1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    addHistorySearchWithDispatch(history);
                }
            });


            mHistoryHostLayout.addView(host1);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rlback:
            case R.id.ivBack:   //返回
                KeyBoardUtils.closeKeybord(mClearEditText, this);
                finish();

                break;

            case R.id.tvSearAction:     //搜索

                String searchKey = mClearEditText.getText().toString();
                addHistorySearchWithDispatch(searchKey);

                break;

            case R.id.btnClearHistory:          //清空历史

                SPUtils.remove(this, SPConfig.SEARCH_HISTORY_KEY);
                mHistoryDatas.clear();

                mPresenter.loadHistory(this);


                break;


        }


    }

    /**
     * 添加历史搜索
     * @param searchKey
     */
    private void addHistorySearchWithDispatch(String searchKey) {
        if (!TextUtils.isEmpty(searchKey)) {

            if (!mHistoryDatas.isEmpty()) {

                if (mHistoryDatas.contains(searchKey)) {
                    mHistoryDatas.remove(searchKey);
                }
            }
            //没有搜索内容
            mHistoryDatas.add(0, searchKey);
            try {

                SPUtils.put(this, SPConfig.SEARCH_HISTORY_KEY, ObjectSerializer.serialize(mHistoryDatas));
                dispatchGoodsListActivity(searchKey);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //如果文本改变的,停止当前的搜索



    }

    @Override
    public void afterTextChanged(Editable s) {

        String searchStr = s.toString();
        if (!TextUtils.isEmpty(searchStr)) {



//            switchContentView(SEARCH);
//            mPresenter.loadAutoComplete();

        }else {
            //清空的内容
            mPresenter.loadHistory(this);

        }


    }



    public void switchContentView(int layer) {
        currentView = layer;

        switch (layer) {
            case HOST:

                if (mHostView == null) {
                    //没有加载过 为null

                    mHostView = mHostViewStub.inflate();
                    mTagGroup = (TagGroup) mHostView.findViewById(R.id.tgHots);
                    mTagGroup.setOnTagClickListener(this);

                }else{
                    mHostView.setVisibility(View.VISIBLE);

                }
                if (mHistoryView != null && mHistoryView.getVisibility() != View.GONE) {
                    mHistoryView.setVisibility(View.GONE);
                }

                if (mAutoCompleteView != null && mAutoCompleteView.getVisibility() != View.GONE) {
                    mAutoCompleteView.setVisibility(View.GONE);
                }

                break;

            case HISTORY:
                if (mHistoryView == null) { //没有加载过 为null


                    mHistoryView = mHistoryViewStub.inflate();
                    mHistoryHostLayout = (LinearLayout) mHistoryView.findViewById(R.id.llHistoryHost);
                    mHistorySearchList = (ListView) mHistoryView.findViewById(R.id.lvHistory);
                    mHistorySearchList.setOnItemClickListener(this);

                    mClearHistory = (Button) mHistoryView.findViewById(R.id.btnClearHistory);
                    mClearHistory.setOnClickListener(this);

                }else{

                    mHistoryView.setVisibility(View.VISIBLE);
                    mClearHistory.setOnClickListener(this);
                }

                if (mHostView != null) {
                    mHostView.setVisibility(View.GONE);
                }

                if (mAutoCompleteView != null) {
                    mAutoCompleteView.setVisibility(View.GONE);
                }

                break;

            case SEARCH:

                if (mAutoCompleteView == null) {
                    mAutoCompleteView = mAutoCompleteViewStub.inflate();

                    mSearchListView = (ListView) mAutoCompleteView.findViewById(R.id.lvSearchResult);
                    mSearchListView.setAdapter(mSearchAdapter);

                }else {
                    if (mAutoCompleteView.getVisibility() == View.GONE) {

                        mAutoCompleteView.setVisibility(View.VISIBLE);

                    }
                }
                if (mHistoryView != null) {
                    mHistoryView.setVisibility(View.GONE);
                }
                if (mHostView != null) {
                    mHostView.setVisibility(View.GONE);
                }

                break;

        }

    }

    /**
     * 历史列表点击 事件
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        String keyword = mHistoryDatas.get(position);

//        dispatchGoodsListActivity(keyword);
        addHistorySearchWithDispatch(keyword);
    }


    /**
     * 跳转到   商品列表
     *
     * @param keyword
     */
    public void dispatchGoodsListActivity(@NonNull String keyword){
        Intent intent = new Intent(this, GoodsFilterActivity.class);

        Bundle extras = new Bundle();
        extras.putString("keyword", keyword);
        intent.putExtras(extras);

        startActivity(intent);
        finish();

    }


}
