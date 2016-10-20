package com.jsyh.onlineshopping.activity.me;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jsyh.onlineshopping.activity.BaseActivity;
import com.jsyh.onlineshopping.config.ConfigValue;
import com.jsyh.onlineshopping.config.SPConfig;
import com.jsyh.onlineshopping.model.LoginModel;
import com.jsyh.onlineshopping.model.UserInforModel;
import com.jsyh.onlineshopping.presenter.LoginPresenter;
import com.jsyh.onlineshopping.presenter.UserInforPresenter;
import com.jsyh.onlineshopping.utils.KeyBoardUtils;
import com.jsyh.onlineshopping.utils.SPUtils;
import com.jsyh.onlineshopping.utils.Utils;
import com.jsyh.onlineshopping.views.LoginView;
import com.jsyh.onlineshopping.views.UserInforView;
import com.jsyh.shopping.uilibrary.R;

/**
 * Created by Administrator on 2015/8/31.
 * 登陆
 */
public class LoginActivity extends BaseActivity implements LoginView,View.OnClickListener ,
        UserInforView{
    private LoginPresenter loginPresenter;
    private Context context;
    private EditText editPassword;
    private EditText editUserName;
    private CheckBox chBoxHint;
    private TextView btnLogin;
    private String name;
    private String password;
    private UserInforPresenter uPresenter;
    TextView title;
    ImageView back;
    @Override
     public void initView() {
        super.initView();
        setContentView(R.layout.activity_login);
        context = this;
        uPresenter = new UserInforPresenter(this);
        title=(TextView)findViewById(R.id.title);
        back = (ImageView)findViewById(R.id.back);
        back.setBackgroundResource(R.mipmap.ic_back);
        title.setText("登录");
        findViewById(R.id.fl_Left).setOnClickListener(this);
        loginPresenter=new LoginPresenter(this);
        btnLogin = (TextView) findViewById(R.id.btnLogin);
        findViewById(R.id.btnRegister).setOnClickListener(this);
        findViewById(R.id.btnForget).setOnClickListener(this);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editUserName = (EditText) findViewById(R.id.editUserName);
        chBoxHint = (CheckBox) findViewById(R.id.chBoxHint);
        chBoxHint.setOnClickListener(this);
        editUserName.addTextChangedListener(textWatcher);
        editPassword.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            name = editUserName.getText().toString().trim();
            password = editPassword.getText().toString().trim();
            if(!name.equals("") && !password.equals("")) {
                btnLogin.setTextColor(getResources().getColor(R.color.white));
                btnLogin.setBackgroundResource(R.drawable.login_on_bg);
                btnLogin.setClickable(true);
                btnLogin.setOnClickListener(LoginActivity.this);
            } else{
                btnLogin.setTextColor(getResources().getColor(R.color.gary_text));
                btnLogin.setBackgroundResource(R.drawable.login_off_bg);
                btnLogin.setClickable(false);
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
            case R.id.chBoxHint:
                if (chBoxHint.isChecked())
                    editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else
                    editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                break;
            case R.id.btnLogin:
                loginPresenter.loadLogin(context,name,password);
                break;
            case R.id.btnRegister:
                loginPresenter.toRegister();
                break;
            case R.id.btnForget:
                loginPresenter.toForget();
                break;
        }
    }

    @Override
    public void to_register() {
        Intent intent=new Intent(this,RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void to_forget() {
        Intent intent=new Intent(this,ForgetActivity.class);
        startActivity(intent);
    }

    @Override
    public void to_main() {
        finish();
    }

    @Override
    public void login(LoginModel response) {
        if (response.getCode().equals("1")) {
            String key = response.getData().getKey();
            SPUtils.put(context, SPConfig.KEY, key);
            ConfigValue.DATA_KEY = key;
            uPresenter.loadInfor(context);
        }
        Utils.showToast(context, response.getMsg());
    }

    @Override
    public void inforData(UserInforModel response) {
        if (response.getCode().equals("1")) {
            ConfigValue.uInfor = response.getData().get(0);
            String num = ConfigValue.uInfor.getCart_num() == null?"0":ConfigValue.uInfor.getCart_num();
            Intent mIntent = new Intent(ConfigValue.ACTION_ALTER_CARTGOODS_NUMS);
            mIntent.putExtra("cartgoodsnum", num);
            //发送广播
            sendBroadcast(mIntent);
            loginPresenter.toLogin();
        }
    }
}
