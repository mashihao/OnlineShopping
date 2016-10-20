package com.jsyh.onlineshopping.activity.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jsyh.onlineshopping.R;
import com.jsyh.onlineshopping.activity.BaseActivity;
import com.jsyh.onlineshopping.model.Distribution;
import com.jsyh.onlineshopping.model.DistributionModel;
import com.jsyh.onlineshopping.presenter.DistributionPresenter;
import com.jsyh.onlineshopping.utils.Utils;
import com.jsyh.onlineshopping.views.DistributionView;
import com.jsyh.shopping.uilibrary.adapter.listview.BaseAdapterHelper;
import com.jsyh.shopping.uilibrary.adapter.listview.QuickAdapter;

/**
 * Created by sks on 2015/10/10.
 * 配送方式
 */
public class DistributionActivity extends BaseActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener,DistributionView{
    TextView title;
    ImageView back;
    private Context context;
    private ListView listView;
    private QuickAdapter<Distribution> quickAdapter;
    private DistributionPresenter dPresenter;
    @Override
    public void initView() {
        super.initView();
        setContentView(R.layout.activity_selectarea);
        context = this;
        dPresenter = new DistributionPresenter(this);
        title=(TextView)findViewById(R.id.title);
        back = (ImageView)findViewById(R.id.back);
        title.setText("选择配送方式");
        back.setBackgroundResource(R.mipmap.ic_back);
        findViewById(R.id.fl_Left).setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listView);
        quickAdapter = new QuickAdapter<Distribution>(context,R.layout.distribution_item) {
            @Override
            protected void convert(BaseAdapterHelper helper, Distribution item) {
                helper.setText(R.id.txtName,item.getShipping_name());
//                helper.setText(R.id.txtFee,item.getShipping_desc());
            }
        };
        listView.setAdapter(quickAdapter);
        listView.setOnItemClickListener(this);
        dPresenter.loadDistributionList(context);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Distribution model = (Distribution)adapterView.getItemAtPosition(i);
        Intent intent = new Intent();
        Bundle bundle=new Bundle();
        bundle.putSerializable("distribution", model);
        intent.putExtras(bundle);
        setResult(CreateOrderActivity.DISTRIBUTION,intent);
        finish();
    }

    @Override
    public void getData(DistributionModel model) {
        if(model.getCode().equals("1")){
            if(model.getData().size()>0){
                quickAdapter.clear();
                quickAdapter.addAll(model.getData());
            }
        }else
            Utils.showToast(context,model.getMsg());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fl_Left:
                finish();
                break;
        }
    }
}
