package com.jsyh.onlineshopping.activity.me;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jsyh.onlineshopping.R;
import com.jsyh.onlineshopping.activity.BaseActivity;
import com.jsyh.onlineshopping.config.ConfigValue;
import com.jsyh.onlineshopping.config.SPConfig;
import com.jsyh.onlineshopping.http.BaseDelegate;
import com.jsyh.onlineshopping.http.ExceptionHelper;
import com.jsyh.onlineshopping.http.OkHttpClientManager;
import com.jsyh.onlineshopping.model.PayModel;
import com.jsyh.onlineshopping.utils.CheckUtil;
import com.jsyh.onlineshopping.utils.KeyBoardUtils;
import com.jsyh.onlineshopping.utils.SPUtils;
import com.jsyh.onlineshopping.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sks on 2015/9/17.
 * 注册
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener{
    private Context context;
    private EditText editUserName,editEmail,editPassword,editAgain;
    private String userName,password,e_mail,again;
    private TextView btnRegister;
    TextView title;
    ImageView back;
    FrameLayout left;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = this;
        title=(TextView)findViewById(R.id.title);
        back = (ImageView)findViewById(R.id.back);
        left = (FrameLayout)findViewById(R.id.fl_Left);
        back.setBackgroundResource(R.mipmap.ic_back);
        title.setText("注册");
        left.setOnClickListener(this);
        editUserName = (EditText) findViewById(R.id.editUserName);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editAgain = (EditText) findViewById(R.id.editAgain);
        btnRegister = (TextView) findViewById(R.id.btnRegister);
        editUserName.addTextChangedListener(textWatcher);
        editEmail.addTextChangedListener(textWatcher);
        editPassword.addTextChangedListener(textWatcher);
        editAgain.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            userName = editUserName.getText().toString().trim();
            password = editPassword.getText().toString().trim();
            e_mail = editEmail.getText().toString().trim();
            again = editAgain.getText().toString().trim();
            if(!userName.equals("") && !password.equals("") && !e_mail.equals("")
                    && !again.equals("")){
                btnRegister.setTextColor(getResources().getColor(R.color.white));
                btnRegister.setBackgroundResource(R.drawable.login_on_bg);
                btnRegister.setClickable(true);
                btnRegister.setOnClickListener(RegisterActivity.this);
            }else {
                btnRegister.setTextColor(getResources().getColor(R.color.gary_text));
                btnRegister.setBackgroundResource(R.drawable.login_off_bg);
                btnRegister.setClickable(false);
            }
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void afterTextChanged(Editable editable) {
        }
    };
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fl_Left:
                KeyBoardUtils.closeKeybord(this);
                break;
            case R.id.btnRegister:
                prepare();
                break;
        }
    }
    private void prepare(){
        if(!CheckUtil.isEmail(e_mail)){
            Utils.showToast(context,"请填写正确的邮箱");
            return;
        }
        if(!password.equals(again)){
            Utils.showToast(context,"两次填写的密码不一致");
            return;
        }
        getRequest();
    }
    private void getRequest(){
        Map<String, String> map = new HashMap<String, String>();
        map.put("api_token",Utils.AppMD5String(context,"user","register"));
        map.put("username",userName);
        map.put("passwd",password);
        map.put("email", e_mail);
        OkHttpClientManager.postAsyn(context, ConfigValue.APP_IP + "user/register",
                map, new BaseDelegate.ResultCallback<PayModel>() {
                    @Override
                    public void onError(com.squareup.okhttp.Request request, Object tag, Exception e) {
                        Utils.showToast(context, ExceptionHelper.getMessage(e,context));
                    }

                    @Override
                    public void onResponse(PayModel response, Object tag) {
                        if (response.getCode().equals("1")) {
                            String key = response.getData();
                            SPUtils.put(context, SPConfig.KEY, key);
                            ConfigValue.DATA_KEY = key;
                            KeyBoardUtils.closeKeybord(RegisterActivity.this);
                        }
                        Utils.showToast(context,response.getMsg());
                    }
                },true);
    }
}
