package com.jsyh.onlineshopping.activity.me;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jsyh.onlineshopping.R;
import com.jsyh.onlineshopping.activity.BaseActivity;
import com.jsyh.onlineshopping.utils.KeyBoardUtils;

/**
 * Created by sks on 2015/9/24.
 * 意见反馈
 */
public class FeedbackActivity extends BaseActivity implements View.OnClickListener{

    TextView title;
    ImageView back;
    private TextView btnEnsure;
    private EditText editIdea;
    private String content;
    @Override
    public void initView() {
        super.initView();
        setContentView(R.layout.activity_feedback);
        title=(TextView)findViewById(R.id.title);
        back = (ImageView)findViewById(R.id.back);
        back.setBackgroundResource(R.mipmap.ic_back);
        title.setText("意见反馈");
        findViewById(R.id.fl_Left).setOnClickListener(this);
        btnEnsure = (TextView) findViewById(R.id.btnSure);
        editIdea = (EditText) findViewById(R.id.editContent);
        editIdea.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            content = editIdea.getText().toString().trim();
            if(!content.equals("")){
                btnEnsure.setTextColor(getResources().getColor(R.color.white));
                btnEnsure.setBackgroundResource(R.drawable.login_on_bg);
                btnEnsure.setClickable(true);
                btnEnsure.setOnClickListener(FeedbackActivity.this);
            } else{
                btnEnsure.setTextColor(getResources().getColor(R.color.gary_text));
                btnEnsure.setBackgroundResource(R.drawable.login_off_bg);
                btnEnsure.setClickable(false);
            }
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
        }
    }
}
