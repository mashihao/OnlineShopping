package com.jsyh.onlineshopping.activity.me;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jsyh.onlineshopping.R;
import com.jsyh.onlineshopping.activity.BaseActivity;
import com.jsyh.shopping.uilibrary.ClearEditText;

/**
 * Created by sks on 2015/9/21.
 */
public class NicknameActivity extends BaseActivity implements View.OnClickListener{
    TextView title;
    ImageView back;
    TextView ensure;
    private ClearEditText txtNickname;
//    private RelativeLayout rlClear;
    @Override
    public void initView() {
        super.initView();
        setContentView(R.layout.activity_nickname);
        title=(TextView)findViewById(R.id.title);
        back = (ImageView)findViewById(R.id.back);
        ensure = (TextView) findViewById(R.id.ensure);
        back.setBackgroundResource(R.mipmap.ic_back);
        ensure.setText("确定");
        title.setText("修改昵称");
        findViewById(R.id.fl_Left).setOnClickListener(this);
        ensure.setOnClickListener(this);
        txtNickname = (ClearEditText) findViewById(R.id.txtNickname);
//        rlClear = (RelativeLayout) findViewById(R.id.rlClear);
//        rlClear.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fl_Left:
                finish();
                break;
            case R.id.ensure:
                Intent it = new Intent();
                it.putExtra("nickname",txtNickname.getText().toString().trim());
                setResult(MeAccountActivity.INTENT_NICKNAME, it);
                finish();
                break;
        }
    }
}
