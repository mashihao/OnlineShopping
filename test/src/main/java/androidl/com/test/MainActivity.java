package androidl.com.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private RadioButton rbHome;
    private RadioButton rbCategory;
    private RadioButton rbShoppingCart;
    private RadioButton rbMe;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dealBottomButtonsClickEvent();
    }
    private void dealBottomButtonsClickEvent() {

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        rbHome = (RadioButton) findViewById(R.id.rbHome);
        rbCategory = (RadioButton) findViewById(R.id.rbCategory);
        rbShoppingCart = (RadioButton) findViewById(R.id.rbShoppingCart);
        rbMe = (RadioButton) findViewById(R.id.rbMe);
        radioGroup.setOnCheckedChangeListener(this);
    }

    //修改状态  当点击  主页下面的4个按钮的时候,修改主页的界面效果
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {
            case R.id.rbHome:
                Toast.makeText(this, "home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rbCategory:
                Toast.makeText(this, "category", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rbShoppingCart:
                Toast.makeText(this, "cart", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rbMe:
                Toast.makeText(this, "me", Toast.LENGTH_SHORT).show();
               break;
            default:
                break;
        }

    }
}
