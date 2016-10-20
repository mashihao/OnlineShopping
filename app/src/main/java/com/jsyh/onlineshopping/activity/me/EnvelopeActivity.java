package com.jsyh.onlineshopping.activity.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.jsyh.onlineshopping.R;
import com.jsyh.onlineshopping.activity.BaseActivity;
import com.jsyh.onlineshopping.model.Bouns;
import com.jsyh.onlineshopping.model.BounsModel;
import com.jsyh.onlineshopping.presenter.EnvelopePresenter;
import com.jsyh.onlineshopping.utils.Utils;
import com.jsyh.onlineshopping.views.EnvelopeView;
import com.jsyh.shopping.uilibrary.adapter.listview.BaseAdapterHelper;
import com.jsyh.shopping.uilibrary.adapter.listview.QuickAdapter;

/**
 * Created by sks on 2015/10/12.
 * 选择红包界面
 */
public class EnvelopeActivity extends BaseActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener,EnvelopeView{
    TextView title;
    ImageView back;
    private Context context;
    private ListView listView;
    private PullRefreshLayout pullRefreshLayout;
    private QuickAdapter<Bouns> quickAdapter;
    private EnvelopePresenter ePresenter;
    private String total;
    private FrameLayout right;

    @Override
    public void initView() {
        super.initView();
        setContentView(R.layout.activity_mebouns);
        context = this;
        total = getIntent().getStringExtra("goodstotal");
        ePresenter = new EnvelopePresenter(this);
        title=(TextView)findViewById(R.id.title);
        back = (ImageView)findViewById(R.id.back);
        right = (FrameLayout) findViewById(R.id.right);
        ((TextView)findViewById(R.id.ensure)).setText("取消使用");
        title.setText("选择红包");
        back.setBackgroundResource(R.mipmap.ic_back);
        right.setOnClickListener(this);
        findViewById(R.id.fl_Left).setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listView);
        pullRefreshLayout = (PullRefreshLayout) findViewById(R.id.pullRefreshLayout);
        pullRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_Bitmap);
        pullRefreshLayout.setOnRefreshListener(onRefreshListener);
        quickAdapter = new QuickAdapter<Bouns>(context,R.layout.bouns_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, Bouns item) {
//                helper.setText(R.id.txtName,item.getType_name());
//                helper.setText(R.id.txtMini,"最低消费：" + item.getMin_goods_amount());
//                helper.setText(R.id.txtValue,"面值：" + item.getType_money());
                helper.setText(R.id.money,item.getType_money());
                helper.setText(R.id.name,item.getType_name());
                helper.setText(R.id.condition,"满" + item.getMin_goods_amount() + "元可用");
                String start = Utils.time2(context, Long.parseLong(item.getUse_start_date()));
                String end = Utils.time2(context, Long.parseLong(item.getUse_end_date()));
                helper.setText(R.id.time,"有效期：" + start + "至" + end);
            }
        };
        listView.setAdapter(quickAdapter);
        listView.setOnItemClickListener(this);
        ePresenter.request(context,total);
    }

    private PullRefreshLayout.OnRefreshListener onRefreshListener = new PullRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            pullRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    ePresenter.request(context,total);
                }
            });
        }

        @Override
        public void onMove(boolean ismove) {

        }
    };
    @Override
    public void result(BounsModel model) {
        pullRefreshLayout.setRefreshing(false);
        if(model.getCode().equals("1")){
            if(model.getData().size()>0){
                quickAdapter.clear();
                quickAdapter.addAll(model.getData());
            }
        }else{
            Utils.showToast(context, model.getMsg());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Bouns model = (Bouns)adapterView.getItemAtPosition(i);
        if(Double.valueOf(model.getMin_goods_amount()) <= Double.valueOf(total)){
            Intent intent = new Intent();
            Bundle bundle=new Bundle();
            bundle.putSerializable("envelope", model);
            intent.putExtras(bundle);
            setResult(CreateOrderActivity.ENVELOPE,intent);
            finish();
        }else
            Utils.showToast(context,"消费金额太低不可以使用此红包");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fl_Left:
                finish();
                break;
            case R.id.right:
                Bouns model = new Bouns();
                Intent intent = new Intent();
                Bundle bundle=new Bundle();
                bundle.putSerializable("envelope", model);
                intent.putExtras(bundle);
                setResult(CreateOrderActivity.CANCLE_ENVELOPE,intent);
                finish();
                break;
        }
    }
}
