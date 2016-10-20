package com.jsyh.onlineshopping.activity.me;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.jsyh.onlineshopping.R;
import com.jsyh.onlineshopping.activity.BaseActivity;
import com.jsyh.onlineshopping.alipay.PayResult;
import com.jsyh.onlineshopping.model.PayModel;
import com.jsyh.onlineshopping.presenter.PayPresenter;
import com.jsyh.onlineshopping.utils.Utils;
import com.jsyh.onlineshopping.views.PayView;

/**
 * Created by sks on 2015/10/22.
 *
 * 支付界面
 */
public class PayActivity extends BaseActivity implements View.OnClickListener,PayView {

    TextView title;
    ImageView back;
    TextView right;
    private Context context;
    private TextView txtMoney;
    private PayPresenter presenter;
    private String type;
    private String orderNumber;
    private String orderMoney;
    private int flag = 0;

    private static final int SDK_PAY_FLAG = 1;

    private static final int SDK_CHECK_FLAG = 2;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);

                    // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                    String resultInfo = payResult.getResult();

                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        finish();
                        Utils.showToast(context, "支付成功");
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Utils.showToast(context,"支付结果确认中");
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Utils.showToast(context, "支付失败");
                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    if (!(boolean) msg.obj)
                        Utils.showToast(context, "检查结果为：" + msg.obj);
                    else {
                        type = "4";
                        presenter.setPay(context, type, orderNumber);
                    }
                    break;
                }
                default:
                    break;
            }
        };
    };
    @Override
    public void initView() {
        super.initView();
        setContentView(R.layout.activity_pay);
        context = this;
        flag = getIntent().getIntExtra("class", 0);
        orderNumber = getIntent().getStringExtra("ordernumber");
        orderMoney = getIntent().getStringExtra("ordermoney");
        presenter = new PayPresenter(this);
        title=(TextView)findViewById(R.id.title);
        back = (ImageView)findViewById(R.id.back);
        right = (TextView) findViewById(R.id.ensure);
        if (flag == 0){
            right.setText("订单中心");
            right.setOnClickListener(this);
        } else
            right.setVisibility(View.GONE);
        findViewById(R.id.fl_Left).setOnClickListener(this);
        back.setBackgroundResource(R.mipmap.ic_back);
        title.setText("收银台");
        txtMoney = (TextView) findViewById(R.id.txtMoney);
        txtMoney.setText(orderMoney + "元");
        findViewById(R.id.rlBalance).setOnClickListener(this);
        findViewById(R.id.rlAlipay).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fl_Left:
                finish();
                break;
            case R.id.ensure:
                Intent intent = new Intent(context,MyOrderActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.rlBalance:
                Utils.showDialog(this, "提示", "确定使用您的账户余额支付吗？", "", "", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.txtDialogCancel:
                                Utils.dismissDialog();
                                break;
                            case R.id.txtDialogSure:
                                type = "1";
                                presenter.setPay(context, type, orderNumber);
                                Utils.dismissDialog();
                                break;
                        }
                    }
                });
                break;
            case R.id.rlAlipay:
//                check();
                type = "4";
                presenter.setPay(context, type, orderNumber);
                break;
        }
    }

    //请求支付接口返回
    @Override
    public void result(PayModel model) {
        if (model.getCode().equals("1")){
            if(type.equals("4") && !model.getData().equals("")){
                pay(model.getData());
            }else
                finish();
        }
        Utils.showToast(context,model.getMsg());
    }

    public void pay(final String payInfo) {

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(PayActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * check whether the device has authentication alipay account.
     * 查询终端设备是否存在支付宝认证账户
     *
     */
    public void check() {
        Runnable checkRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask payTask = new PayTask(PayActivity.this);
                // 调用查询接口，获取查询结果
                boolean isExist = payTask.checkAccountIfExist();

                Message msg = new Message();
                msg.what = SDK_CHECK_FLAG;
                msg.obj = isExist;
                mHandler.sendMessage(msg);
            }
        };

        Thread checkThread = new Thread(checkRunnable);
        checkThread.start();

    }
}
