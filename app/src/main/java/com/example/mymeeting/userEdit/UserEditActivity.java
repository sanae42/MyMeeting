package com.example.mymeeting.userEdit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.mymeeting.R;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class UserEditActivity extends AppCompatActivity {

    EaseTitleBar titleBar;

    LinearLayout nickEditLiearlayout;
    LinearLayout passwordEditLiearlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);



    }

    private void initView(){
        titleBar = findViewById(R.id.title_bar);
        //设置返回按钮的点击事件
        //TODO:点按无效
        titleBar.setOnBackPressListener(new EaseTitleBar.OnBackPressListener() {
            @Override
            public void onBackPress(View view) {
                finish();
            }
        });


        nickEditLiearlayout = (LinearLayout)findViewById(R.id.nick_edit);
        nickEditLiearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        passwordEditLiearlayout = (LinearLayout)findViewById(R.id.password_edit);
        passwordEditLiearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}