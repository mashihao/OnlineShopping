package com.jsyh.onlineshopping.activity.me;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.jsyh.onlineshopping.R;
import com.jsyh.onlineshopping.activity.BaseActivity;
import com.jsyh.onlineshopping.config.ConfigValue;
import com.jsyh.onlineshopping.model.BaseModel;
import com.jsyh.onlineshopping.model.Order;
import com.jsyh.onlineshopping.model.OrderGoods;
import com.jsyh.onlineshopping.model.OrderModel;
import com.jsyh.onlineshopping.presenter.MyOrderPresenter;
import com.jsyh.onlineshopping.presenter.OrderManagerPresenter;
import com.jsyh.onlineshopping.utils.Utils;
import com.jsyh.onlineshopping.views.MyOrderView;
import com.jsyh.onlineshopping.views.OrderManagerView;
import com.jsyh.shopping.uilibrary.adapter.listview.BaseAdapterHelper;
import com.jsyh.shopping.uilibrary.adapter.listview.QuickAdapter;
import com.jsyh.shopping.uilibrary.uiutils.ListViewUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by sks on 2015/10/11.
 * 待收货、待发货、待付款
 */
public class TypeOrderActivity extends BaseActivity implements View.OnClickListener,
        MyOrderView,OrderManagerView {
    private int type;
    private Context context;
    TextView title;
    ImageView back;
    private ListView listView;
    private QuickAdapter<Order> quickAdapter;
    private MyOrderPresenter presenter;
    private PullRefreshLayout pullRefreshLayout;
    private String action;
    private OrderManagerPresenter omPresenter;
    @Override
    public void initView() {
        super.initView();
        setContentView(R.layout.activity_myorder);
        context = this;
        omPresenter = new OrderManagerPresenter(this);
        type = getIntent().getIntExtra("type",0);
        presenter = new MyOrderPresenter(this);
        title=(TextView)findViewById(R.id.title);
        back = (ImageView)findViewById(R.id.back);
        back.setBackgroundResource(R.mipmap.ic_back);
        findViewById(R.id.fl_Left).setOnClickListener(this);
        switch (type){
            case 0:
                action = "obligation";
                title.setText("待付款");
                break;
            case 1:
                action = "send_goods";
                title.setText("待发货");
                break;
            case 2:
                action = "reciv_goods";
                title.setText("待收货");
                break;
            case 3:
                action = "order_sucess";
                title.setText("已完成");
                break;
        }
        pullRefreshLayout = (PullRefreshLayout) findViewById(R.id.pullRefreshLayout);
        pullRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_Bitmap);
        pullRefreshLayout.setOnRefreshListener(onRefreshListener);
        listView = (ListView) findViewById(R.id.listView);
        quickAdapter = new QuickAdapter<Order>(context,R.layout.myorder_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, final Order item) {
                ListView view = helper.getView(R.id.goodListView);
                QuickAdapter<OrderGoods> quickAdapter1 = new QuickAdapter<OrderGoods>(context,
                        R.layout.order_good_item) {
                    @Override
                    protected void convert(BaseAdapterHelper helper, OrderGoods item) {
                        Picasso.with(context).load(ConfigValue.IMG_IP
                                + item.getGoods_thumb()).error(R.mipmap.ic_launcher)
                                .into((ImageView) helper.getView(R.id.imgGoods));
                        helper.setText(R.id.txtTitle, item.getGoods_name());
                        helper.setText(R.id.txtPrice, "￥" + item.getGoods_price());
                        helper.setText(R.id.txtNumber,"数量：" + item.getGoods_number());
                    }
                };
                quickAdapter1.addAll(item.getGoods());
                view.setAdapter(quickAdapter1);
                ListViewUtils.setListViewHeightBasedOnItems(view);
                helper.setText(R.id.txtAmount, "￥" + item.getTotal());
                TextView txtState = helper.getView(R.id.txtState);
                TextView txtButton = helper.getView(R.id.txtButton);
                switch (type){
                    case 0:
                        txtState.setText("等待付款");
//                        txtState.setTextColor(getResources().getColor(R.color.text_red_color));
                        txtButton.setText("去付款");
//                        txtButton.setTextColor(getResources().getColor(R.color.text_red_color));
//                        txtButton.setBackgroundResource(R.drawable.text_radian_bg);
                        break;
                    case 1:
                        txtState.setText("待发货");
//                        txtState.setTextColor(getResources().getColor(R.color.text_red_color));
                        txtButton.setText("申请退款");
                        txtButton.setVisibility(View.INVISIBLE);
//                        txtButton.setTextColor(getResources().getColor(R.color.text_red_color));
//                        txtButton.setBackgroundResource(R.drawable.text_radian_bg);
                        break;
                    case 2:
                        txtState.setText("待收货");
//                        txtState.setTextColor(getResources().getColor(R.color.result_points));
                        txtButton.setText("确认收货");
//                        txtButton.setTextColor(getResources().getColor(R.color.result_points));
//                        txtButton.setBackgroundResource(R.drawable.text_radian_bg2);
                        break;
                    case 3:
                        txtState.setText("已完成");
                        txtButton.setVisibility(View.INVISIBLE);
                        break;
                }
                txtButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (type){
                            case 0:
                                //支付
                                Intent intent = new Intent(context,PayActivity.class);
                                intent.putExtra("class",1);
                                intent.putExtra("ordernumber",item.getOrder_sn());
                                intent.putExtra("ordermoney",item.getTotal());
                                startActivity(intent);
                                break;
                            case 1:
                                omPresenter.returnOrder(context,item.getOrder_id());
                                break;
                            case 2:
                                Utils.showDialog(TypeOrderActivity.this, "提示", "是否确认收货？", "", "", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        switch (view.getId()) {
                                            case R.id.txtDialogCancel:
                                                Utils.dismissDialog();
                                                break;
                                            case R.id.txtDialogSure:
                                                //确认收货
                                                omPresenter.sureOrder(context, item.getOrder_id());
                                                Utils.dismissDialog();
                                                break;
                                        }
                                    }
                                });
                                break;
                        }
                    }
                });
            }
        };
        listView.setAdapter(quickAdapter);

    }

    private PullRefreshLayout.OnRefreshListener onRefreshListener = new PullRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            pullRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    presenter.loadOrder(context,"order",action);
                }
            });
        }

        @Override
        public void onMove(boolean ismove) {

        }
    };

    //订单列表数据
    @Override
    public void orderList(OrderModel model) {
        pullRefreshLayout.setRefreshing(false);
        quickAdapter.clear();
        if(model.getCode().equals("1")){
            if(model.getData().size()>0) {
                quickAdapter.addAll(model.getData());
            }else
                Utils.showToast(context, "没有订单");
        }else if (model.getCode().equals("-220"))
            itLogin(this);
        Utils.showToast(context,model.getMsg());
    }

    @Override
    public void error() {
        pullRefreshLayout.setRefreshing(false);
    }

    //对订单操作的返回
    @Override
    public void result(BaseModel model) {
        if(model.getCode().equals("1")){
            onResume();
        }
        Utils.showToast(context,model.getMsg());
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.loadOrder(context, "order", action);
    }

    @Override
    public void onClick(View view) {
        finish();
    }
}
