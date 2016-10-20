package com.jsyh.onlineshopping.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonParseException;
import com.jsyh.onlineshopping.R;
import com.jsyh.onlineshopping.adapter.goods.DrawerTypeAdapter;
import com.jsyh.onlineshopping.adapter.goods.GridAdapter;
import com.jsyh.onlineshopping.adapter.goods.LinearAdapter;
import com.jsyh.onlineshopping.config.SPConfig;
import com.jsyh.onlineshopping.fragment.FullScreanDialogFragment;
import com.jsyh.onlineshopping.model.FilterInfo;
import com.jsyh.onlineshopping.model.FilterInfoModel;
import com.jsyh.onlineshopping.model.GoodsInfo;
import com.jsyh.onlineshopping.model.GoodsInfoModel;
import com.jsyh.onlineshopping.presenter.GoodsFilterPresenter;
import com.jsyh.onlineshopping.utils.AppManager;
import com.jsyh.onlineshopping.utils.KeyBoardUtils;
import com.jsyh.onlineshopping.utils.SPUtils;
import com.jsyh.onlineshopping.views.GoodsFilterView;
import com.jsyh.shopping.uilibrary.CoordinatorLayoutFixup;
import com.jsyh.shopping.uilibrary.GoodsFilterTabView;
import com.jsyh.shopping.uilibrary.SimpleTextWatcher;
import com.jsyh.shopping.uilibrary.button.FloatingActionButton;
import com.jsyh.shopping.uilibrary.divider.DividerItemDecoration;
import com.jsyh.shopping.uilibrary.drawerlayout.DrawerLayoutFixup;
import com.jsyh.shopping.uilibrary.uiutils.ListViewUtils;
import com.jsyh.shopping.uilibrary.uiutils.ScreenUtils;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 根据条件 展示商品
 *
 * 1, 初始化过程 。p.loadLayoutModel() -> p.loadGoodsDatas()
 *
 * 2. 条件检索过程。 TabLayoutView 的回调 -> p.loadGoodsDatas()
 *
 */
public class GoodsFilterActivity extends AppCompatActivity implements View.OnClickListener, GoodsFilterView,
        GoodsFilterTabView.FilterTabListener,
        FullScreanDialogFragment.DialogListener {

    private boolean                 firstLoad = true;
    private GoodsFilterPresenter    mPresenter;

    private int         page = 1;
    private String      mKeyWord;               // 关键字
    private String      mId;                    //从分类跳转
    private int         mPageCount;             //总页数
    private boolean     isLoadMore = false;     //是否正在加载更多中



    private final int   LINEAR_MODEL = 0;     //线性布局
    private final int   GRID_MODEL = 1;       //表格布局
    private int         currModel = LINEAR_MODEL;

    private CoordinatorLayoutFixup mCoordinatorLayout;

    // ---------------- head layout -----------------
    private Toolbar             mToolbar;
    private ImageView           mBack;

    private EditText            mSearchEditText;            //搜索框



    private ImageView           mLayoutSwitch;        //RecyclerView的布局切换

    // ---------------------- Filter Tab Layout ----------
    private GoodsFilterTabView  mTabLayout;


    //------------------ content layout ---------------------
    private RecyclerView                    mGoodsShow;         //商品展示列表
    private RecyclerView.LayoutManager      mLinearManager;
    private DividerItemDecoration           mDivider;
    private RecyclerView.LayoutManager      mGridManager;

    private LinearAdapter                   mLinearAdapter;           //商品展示适配
    private GridAdapter                     mGridAdapter;           //商品展示适配

    private List<GoodsInfo>                 mGoodsDatas;        //商品数据

    private FloatingActionButton            mFab;






    // --------------- main drawer layout -----------
    private List<FilterInfo.TypeInfo>    mDrawerDatas;

    private Map<String, String>          mJsonFilterParems = new HashMap<>();        //筛选条件，参数

    private DrawerLayoutFixup            mMainDrawerLayout;
    private TextView                     mMainDrawerOk;
    private TextView                     mMainDrawerCancel;

    private ScrollView                   mMainFilterScrollView;

    private ListView                     mAllTypeListView;           //所有分类列表
    private DrawerTypeAdapter            mDrawerTypeAdapter;


    private TextInputLayout              mMaxTextInputLayout;
    private TextInputLayout              mMinTextInputLayout;


    private EditText                     mMaxPrice;              //输入的价格区间 最大
    private EditText                     mMinPrice;              //输入的价格区间 最小

    private RadioGroup                   mFreeRadioGroup;            //是否免费
    private RadioGroup                   mSaleRadioGroup;            //是促销

    private Button                       mClearAllSelect;            // 清空选项
    private float                        maxServer;
    private float                        minServer;


    private String                       mainType = "";           //首页跳转过来的分类类型，有new：新发现，hot：热销产品，best：店长推荐

    private TextView                     mEmpty;         //空布局

    private LinearLayout                 mNetworkErrorLayout;
    private Button                       mReloadNetwork;     //重新加载网络

    private String goodsType = "";          //筛选要用到的东西



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goods_filter_activity);

        AppManager.getAppManager().addActivity(this);
        mPresenter = new GoodsFilterPresenter(this);
        mGoodsDatas = new ArrayList<>();
        mEmpty = (TextView) findViewById(R.id.temptText);
        mNetworkErrorLayout = (LinearLayout) findViewById(R.id.networkError);
        mReloadNetwork = (Button) findViewById(R.id.btnReloadNetwork);
        mReloadNetwork.setOnClickListener(this);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mCoordinatorLayout = (CoordinatorLayoutFixup) findViewById(R.id.coordinatorLayout);


        mCoordinatorLayout.setTestListener(new CoordinatorLayoutFixup.TestListener() {
            @Override
            public void touchEvent() {
                mToolbar.requestLayout();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            if (!TextUtils.isEmpty(extras.getString("id"))) {

                mId = extras.getString("id");
            }else
            mId = "";

            mKeyWord = extras.getString("keyword");
            if (TextUtils.isEmpty(mKeyWord)) {
                mKeyWord = "";
//                throw new IllegalArgumentException("The keyword is must not null");
            }
            mainType = extras.getString("type");
            if (TextUtils.isEmpty(mainType)) {
                mainType = "";
            }
        }

        initHeadLayout();

        initContentLayout();

        intMainDawerLayout();


        mPresenter.loadLayoutModel(this, 0);


    }


    private void initHeadLayout() {

        mBack = (ImageView) findViewById(R.id.ivBack);
        mBack.setOnClickListener(this);
        findViewById(R.id.rlBack).setOnClickListener(this);

        mSearchEditText = (EditText) findViewById(R.id.etSearch);
        mSearchEditText.setText(mKeyWord);
        mSearchEditText.setOnClickListener(this);

        mLayoutSwitch = (ImageView) findViewById(R.id.ivLayoutSwitch);
        mLayoutSwitch.setOnClickListener(this);
        findViewById(R.id.rlLayoutSwitch).setOnClickListener(this);


        mTabLayout = (GoodsFilterTabView) findViewById(R.id.goodsFilterTabLayout);
        mTabLayout.setOnTabListener(this);
    }

    /**
     * 初始化内容布局
     */
    private void initContentLayout() {

        mLinearManager = new LinearLayoutManager(this);
        mDivider = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);

        mGridManager = new GridLayoutManager(this, 2);

        mGoodsShow = (RecyclerView) findViewById(R.id.rvGoodsList);
        mGoodsShow.addOnScrollListener(new GoodsShowRecyclerScrollListener());


        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(this);
        mLinearAdapter = new LinearAdapter(this, R.layout.goods_show_model_one_item, mGoodsDatas);
        mLinearAdapter.setView(this);


        mGridAdapter = new GridAdapter(this, R.layout.goods_show_model_two_item,mGoodsDatas);

    }

    /**
     * 实例化 main DrawerLayout
     */
    private void intMainDawerLayout() {

        mDrawerDatas = new ArrayList<>();

        mMainDrawerLayout = (DrawerLayoutFixup) findViewById(R.id.mainDrawerLayout);
        mMainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mMainDrawerLayout.setDrawerListener(new MainDrawerListener());


        mMainDrawerCancel = (TextView) findViewById(R.id.tvMainDrawerCancel);
        mMainDrawerCancel.setOnClickListener(this);

        mMainDrawerOk = (TextView) findViewById(R.id.tvMainDrawerOk);
        mMainDrawerOk.setOnClickListener(this);

        mMainFilterScrollView = (ScrollView) findViewById(R.id.svMainContent);

        mMinTextInputLayout = (TextInputLayout) findViewById(R.id.tilMinLayout);
        mMaxTextInputLayout = (TextInputLayout) findViewById(R.id.tilMaxLayout);


        mMaxPrice = mMaxTextInputLayout.getEditText();      //价格最大
        mMinPrice = mMinTextInputLayout.getEditText();       //价格最小

        mMinPrice.addTextChangedListener(new SimpleTextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        //小数点的位数超过 2 位
                        s = s.toString().subSequence(0,s.toString().indexOf(".") + 3);
                        mMinPrice.setText(s);
                        mMinPrice.setSelection(s.length());
                    }
                }

            }
        });
        mMaxPrice.addTextChangedListener(new SimpleTextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        //小数点的位数超过 2 位
                        s = s.toString().subSequence(0,s.toString().indexOf(".") + 3);
                        mMaxPrice.setText(s);
                        mMaxPrice.setSelection(s.length());
                    }
                }

            }
        });

        mAllTypeListView = (ListView) findViewById(R.id.lvAllType);
        mAllTypeListView.setOnItemClickListener(new MainDrawerTypeCheckListener());     // item 点击事件
        mDrawerTypeAdapter = new DrawerTypeAdapter(this, R.layout.goods_sub_filter_item, mDrawerDatas);


        mAllTypeListView.setAdapter(mDrawerTypeAdapter);

        mFreeRadioGroup = (RadioGroup) findViewById(R.id.rgFree);

        mSaleRadioGroup = (RadioGroup) findViewById(R.id.rgSale);

        mClearAllSelect = (Button) findViewById(R.id.btnClearAllSelect);
        mClearAllSelect.setOnClickListener(this);


    }



    FullScreanDialogFragment mSearchDialog;
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rlBack:
            case R.id.ivBack:       //返回
                finish();
                break;

            case R.id.etSearch:     //搜索框
                mSearchDialog = (FullScreanDialogFragment) getSupportFragmentManager().findFragmentByTag("searchInFilter");
                if (mSearchDialog == null){
                    mSearchDialog = new FullScreanDialogFragment();
                }else{
                    getSupportFragmentManager().beginTransaction().remove(mSearchDialog).commit();
                }

                mSearchDialog.setDialogListener(this);
                mSearchDialog.show(getSupportFragmentManager().beginTransaction(), "searchInFilter");


                break;

            case R.id.rlLayoutSwitch:
            case R.id.ivLayoutSwitch:       //布局切换

                if (mGoodsDatas.isEmpty())return;

                int position = 1;

                if (currModel == LINEAR_MODEL) {
                    position = ((LinearLayoutManager) mGoodsShow.getLayoutManager()).findFirstVisibleItemPosition();
                } else {

                    position = ((GridLayoutManager) mGoodsShow.getLayoutManager()).findFirstVisibleItemPosition();
                }

                mPresenter.loadLayoutModel(this, position);
                break;

            case R.id.tvMainDrawerCancel:   //关闭 主 DrawerLayout
                mMainDrawerLayout.closeDrawers();
                break;

            case R.id.tvMainDrawerOk:
                //确定筛选 内容
                okFilterCondition();
                break;

            case R.id.btnClearAllSelect:
                // main drawer 清空选项
                resetFilterSelect();
                break;

            case R.id.fab:      //浮动按钮，返回到顶部
                mGoodsShow.smoothScrollToPosition(0);
                break;

            case R.id.btnReloadNetwork:
                mPresenter.loadGoodsDatas(this,GoodsFilterPresenter.SEARCH_TYPE,
                        mKeyWord,mId,page+"",
                        mTabLayout.getCurrentFilterStr(),mJsonFilterParems,mainType);
                break;


        }

    }

    /**
     * 清空选择
     */
    private void resetFilterSelect() {
        mMinPrice.setText("");
        mMaxPrice.setText("");

        mSaleRadioGroup.clearCheck();
        mFreeRadioGroup.clearCheck();

        if (prePosition > -1) {

            mDrawerDatas.get(prePosition).setIsCheck(false);
            prePosition = -1;
            mDrawerTypeAdapter.notifyDataSetChanged();
        }

        mMainFilterScrollView.fullScroll(ScrollView.FOCUS_UP);


    }

    /**
     * 确定筛选条件
     */
    private void okFilterCondition() {

        String maxPrice = mMaxPrice.getText().toString().trim();
        String minPrice = mMinPrice.getText().toString().toString().trim();


        // 是否免运费 1免，0不免
        String isFreeValue = null;
        int isFreeId = mFreeRadioGroup.getCheckedRadioButtonId();
        if (isFreeId == R.id.rbFreeYes) {
            isFreeValue = "1";
        } else if (isFreeId == R.id.rbFreeNo){
            isFreeValue = "0";
        }


        // 产品是否促销 1是，0不是
        String isSaleValue = null;
        int isSaleId = mSaleRadioGroup.getCheckedRadioButtonId();
        if (isSaleId == R.id.rbSaleYes) {
            isSaleValue = "1";
        } else if (isSaleId == R.id.rbSaleNo){
            isSaleValue = "0";
        }


       mJsonFilterParems.clear();

        //价格都不为空
        if (!TextUtils.isEmpty(maxPrice) && !TextUtils.isEmpty(minPrice)) {

            float maxFloat = Math.max(Float.parseFloat(maxPrice), Float.parseFloat(minPrice));
            float minFloat = Math.min(Float.parseFloat(maxPrice), Float.parseFloat(minPrice));


            if (maxFloat >= maxServer ||maxFloat <= minServer  ) {
                maxFloat = maxServer;
            }
            if (minFloat >= maxServer || minFloat <= minServer ) {
                minFloat = minServer;
            }
            maxFloat = Math.max(maxFloat, minFloat);
            minFloat = Math.min(maxFloat, minFloat);

            mJsonFilterParems.put("price_range", minFloat + "-" + maxFloat);
            mMaxPrice.setText(maxFloat + "");
            mMinPrice.setText(minFloat + "");
        }

        //类型
        String classifyId = null;
        if (prePosition > -1) {

            classifyId = mDrawerDatas.get(prePosition).getCat_id();
        }

        //有选择了 类型
        if (!TextUtils.isEmpty(classifyId)) {

            mJsonFilterParems.put("classify", classifyId);
        }

        if (!TextUtils.isEmpty(isFreeValue)) {

            mJsonFilterParems.put("is_fare", isFreeValue);
        }

        if (!TextUtils.isEmpty(isSaleValue)) {

            mJsonFilterParems.put("is_promotion", isSaleValue);
        }


        resetAllDatas();

        //加载数据
        mPresenter.loadGoodsDatas(this, GoodsFilterPresenter.SEARCH_TYPE,
                mKeyWord,mId,page+"",
                mTabLayout.getCurrentFilterStr(),
                mJsonFilterParems,mainType);

        //关闭键盘
        if (mMinPrice.isFocused()){
            KeyBoardUtils.closeKeybord(mMinPrice,this);
        }
        if (mMinPrice.isFocusable()) {
            KeyBoardUtils.closeKeybord(mMaxPrice,this);
        }

        // 筛选图片选中状态
        mTabLayout.setFilterChecked(!mJsonFilterParems.isEmpty());
        //关闭 drawer
        mMainDrawerLayout.closeDrawers();

    }

    @Override
    public void onFilterListener() {
        mMainDrawerLayout.openDrawer(GravityCompat.END);
    }

    @Override
    public void onFilterCondition(String condition) {

        resetAllDatas();

        if (currModel == LINEAR_MODEL) {
            mLinearAdapter.notifyDataSetChanged();

        } else {
            mGridAdapter.notifyDataSetChanged();
        }

        mPresenter.loadGoodsDatas(this, GoodsFilterPresenter.SEARCH_TYPE,
                mKeyWord,mId,page+"", condition, mJsonFilterParems, mainType);

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && mMainDrawerLayout.isDrawerOpen(GravityCompat.END)) {

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mMainDrawerLayout.isDrawerOpen(GravityCompat.END)) {

            mMainDrawerLayout.closeDrawer(GravityCompat.END);

            return true;
        }

        return super.onKeyUp(keyCode, event);
    }


    //-----------------view layer callback --------

    @Override
    public void onLayoutSwitch(int model, int firstPosition) {

        if (firstLoad) {
            currModel = model;

            if (currModel == LINEAR_MODEL) {
                mLayoutSwitch.setImageResource(R.mipmap.ic_goods_grid_mode);
                mGoodsShow.addItemDecoration(mDivider);
                mGoodsShow.setLayoutManager(mLinearManager);
                mGoodsShow.setAdapter(mLinearAdapter);
            } else {
                mLayoutSwitch.setImageResource(R.mipmap.ic_goods_list_mode);
                mGoodsShow.setLayoutManager(mGridManager);
                mGoodsShow.setAdapter(mGridAdapter);
            }

            firstLoad = false;
            mFab.attachToRecyclerView(mGoodsShow);
//            mFab.hide();

            //初始化，第一次加载数据从这里开始
           mPresenter.loadGoodsDatas(this, GoodsFilterPresenter.SEARCH_TYPE,
                   mKeyWord,mId, page + "", mTabLayout.getCurrentFilterStr(),
                   mJsonFilterParems, mainType);

        } else {

            if (model == LINEAR_MODEL) {
                //切换到grid  模式
                currModel = GRID_MODEL;
                SPUtils.put(this, SPConfig.GOODS_SHOW_MODEL_KEY, GRID_MODEL);

                mLayoutSwitch.setImageResource(R.mipmap.ic_goods_list_mode);


                mGoodsShow.setLayoutManager(mGridManager);
                mGoodsShow.removeItemDecoration(mDivider);
                mGoodsShow.setAdapter(mGridAdapter);

                mGoodsShow.scrollToPosition(firstPosition);

            } else {
                currModel = LINEAR_MODEL;
                //切换到 linear 模式
                SPUtils.put(this, SPConfig.GOODS_SHOW_MODEL_KEY, LINEAR_MODEL);

                mLayoutSwitch.setImageResource(R.mipmap.ic_goods_grid_mode);

                mGoodsShow.addItemDecoration(mDivider);

                mGoodsShow.setLayoutManager(mLinearManager);
                mGoodsShow.setAdapter(mLinearAdapter);

                mGoodsShow.scrollToPosition(firstPosition);
            }
        }
    }

    @Override
    public void onFilterGoodsData(@Nullable GoodsInfoModel model, @Nullable Exception e) {

        mPresenter.dismiss();
        if (e != null) {

            if (e instanceof TimeoutException ) {

            }else if (e instanceof ConnectException) {
//                Toast.makeText(GoodsFilterActivity.this, "connect", Toast.LENGTH_SHORT).show();
                networkErrorTips();

            }else if (e instanceof JsonParseException) {
//                Toast.makeText(GoodsFilterActivity.this, "json parse", Toast.LENGTH_SHORT).show();
            }

            return;
        }

        if (model == null) {

            return;
        }

        if(model.getData().getGoods_type() != null)
            goodsType = model.getData().getGoods_type();

        if (model.getData().getGoods() == null ||  model.getData().getGoods().isEmpty()) {

            if (page <= 1) {

                //第一次，没有加载到数据
                mGoodsDatas.clear();
                int margen = (ScreenUtils.getScreenHeight(this) - ScreenUtils.getStatusHeight(this)) / 2;

                mGoodsShow.setVisibility(View.GONE);
                mNetworkErrorLayout.setVisibility(View.GONE);
                mEmpty.setVisibility(View.VISIBLE);

                CoordinatorLayoutFixup.LayoutParams layoutParams = (CoordinatorLayoutFixup.LayoutParams) mEmpty.getLayoutParams();
                layoutParams.setMargins(0, margen, 0, 0);
                mEmpty.setLayoutParams(layoutParams);

                mFab.hide(false);
                return;
            }else{
                Toast.makeText(GoodsFilterActivity.this, R.string.no_more, Toast.LENGTH_SHORT).show();
                return;
            }



        }

        mGoodsShow.setVisibility(View.VISIBLE);
        mEmpty.setVisibility(View.GONE);
        mNetworkErrorLayout.setVisibility(View.GONE);

        try {
            if (!TextUtils.isEmpty(model.getData().getPage_count())) {

                mPageCount = Integer.parseInt(model.getData().getPage_count());
                if (page <= mPageCount) {
                    page++;
                }
            }
        } catch (NumberFormatException e1) {
            e1.printStackTrace();
        }
//        mGoodsDatas.clear();
        mGoodsDatas.addAll(model.getData().getGoods());


        if (currModel == LINEAR_MODEL) {
            mLinearAdapter.notifyDataSetChanged();

        } else {
            mGridAdapter.notifyDataSetChanged();
        }

    }

    /**
     * 网络请求错误
     */
    private void networkErrorTips() {

        mEmpty.setVisibility(View.GONE);
        mGoodsShow.setVisibility(View.GONE);

        mNetworkErrorLayout.setVisibility(View.VISIBLE);

        int margen = (ScreenUtils.getScreenHeight(this) - ScreenUtils.getStatusHeight(this)) / 2 - mNetworkErrorLayout.getHeight() / 2;

        CoordinatorLayoutFixup.LayoutParams layoutParams = (CoordinatorLayoutFixup.LayoutParams) mNetworkErrorLayout.getLayoutParams();
        layoutParams.setMargins(0, margen, 0, 0);
        mNetworkErrorLayout.setLayoutParams(layoutParams);

        mFab.hide(false);

        Toast.makeText(GoodsFilterActivity.this, R.string.network_errot_toast, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDrawerData(@Nullable FilterInfoModel model, @Nullable Exception e) {
        if (e != null) {
            if (e instanceof ConnectException) {
                mMainDrawerLayout.closeDrawers();       //网络错误关闭
                networkErrorTips();
            }
            return;
        }


        if (model != null) {

            // 返回数据的格式是  大 -> 小
            String priceRange = model.getData().getPrice_range();
            final String[] splitPrices = priceRange.split("-");
            if (splitPrices.length>0){
                maxServer = Math.max(Float.parseFloat(splitPrices[1]), Float.parseFloat(splitPrices[0]));
                minServer = Math.min(Float.parseFloat(splitPrices[1]), Float.parseFloat(splitPrices[0]));
            }

            mMinTextInputLayout.setHint("最低价:" +minServer);
            mMaxTextInputLayout.setHint("最高价:" +maxServer);

            mDrawerDatas.clear();
            mDrawerTypeAdapter.clear();

            if (prePosition > -1) {
                model.getData().getClassify().get(prePosition).setIsCheck(true);
            }

            mDrawerDatas.addAll(model.getData().getClassify());
            mDrawerTypeAdapter.addAll(mDrawerDatas);

            ListViewUtils.setListViewHeightBasedOnItems(mAllTypeListView);

        }


    }



    @Override
    public void onDialogKeyword(String keyword) {
        mKeyWord = keyword;

        resetAllDatas();

        mPresenter.loadGoodsDatas(this, GoodsFilterPresenter.SEARCH_TYPE,
                mKeyWord,mId, page+"",mTabLayout.getCurrentFilterStr(),
                mJsonFilterParems, mainType);

        mSearchEditText.setText(mKeyWord);
    }

    /**
     * 所有数据归零
     */
    private void resetAllDatas() {
        page = 1;
        mPageCount = 0;

        mGoodsDatas.clear();

        mLinearAdapter.clear();
        mGridAdapter.clear();
    }


    /**
     * RecyclerView 的滚动监听事件
     */
    class GoodsShowRecyclerScrollListener extends RecyclerView.OnScrollListener{

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE: //滚动停止


                    switch (currModel) {
                        case LINEAR_MODEL:

                            int firstPosition1 = ((LinearLayoutManager) mLinearManager).findFirstVisibleItemPosition();
                            int lastPosition1 = ((LinearLayoutManager) mLinearManager).findLastCompletelyVisibleItemPosition();

                            loadMoreAction(firstPosition1,lastPosition1);

                            break;

                        case GRID_MODEL:

                            int firstPosition2 = ((LinearLayoutManager) mGridManager).findFirstVisibleItemPosition();
                            int lastPosition2 = ((LinearLayoutManager) mGridManager).findLastCompletelyVisibleItemPosition();
                            loadMoreAction(firstPosition2,lastPosition2);

                            break;


                    }

                    break;



            }

            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            mToolbar.requestLayout();

//            mToolbar.onTouchEvent(MotionEvent.obtain(10, 10, MotionEvent.ACTION_DOWN, 1f, 1f, 0));
            super.onScrolled(recyclerView, dx, dy);
        }
    }

    /**
     * 加载更多
     * @param firstPosition
     * @param lastPosition
     */
    public void loadMoreAction(int firstPosition, int lastPosition) {

        if (firstPosition == 0) {
            mFab.hide();
        }

        if (page <= mPageCount && lastPosition == mGoodsDatas.size() - 1) {
            //当前页 小于总页数，并且滑动到了末尾
            mPresenter.loadGoodsDatas(this, GoodsFilterPresenter.SEARCH_TYPE,
                    mKeyWord,mId, page+"",
                    mTabLayout.getCurrentFilterStr(),
                    mJsonFilterParems, mainType);


        }
    }


    /**
     * Main drawer 的监听
     */
    class MainDrawerListener implements DrawerLayout.DrawerListener {

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(View drawerView) {
            mMainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

            //drawer 打开的时候加载数据
            mPresenter.loadFilterDataForDrawer(GoodsFilterActivity.this,mKeyWord,goodsType,mId);
        }

        @Override
        public void onDrawerClosed(View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    }


    // main drawer listview listener

    int prePosition = -1;

    class MainDrawerTypeCheckListener implements AbsListView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (prePosition == position) return;

            if (prePosition > -1) {
                //上一个，变为 未选中状态
                mDrawerDatas.get(prePosition).setIsCheck(false);

            }

            if (prePosition != position) {
                //当前为选中状态
                mDrawerDatas.get(position).setIsCheck(true);

                prePosition = position;

                mDrawerTypeAdapter.notifyDataSetChanged();
            }


        }
    }


}
