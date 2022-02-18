package com.example.mymeeting.userEdit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.example.mymeeting.R;
import com.example.mymeeting.calendar.CalendarActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class UserEditActivity extends AppCompatActivity {

    Toolbar toolbar;

    LinearLayout nickEditLiearlayout;
    LinearLayout passwordEditLiearlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);


        initView();
    }

    private void initView(){
        //        导航条
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //   设置actionbar（即toolbar）最左侧按钮显示状态和图标
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
            actionBar.setDisplayShowTitleEnabled(false);
        }


        nickEditLiearlayout = (LinearLayout)findViewById(R.id.nick_edit);
        nickEditLiearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_nick = new Intent();
                intent_nick.setClass(getApplicationContext(), UserNickEditActivity.class);
                startActivity(intent_nick);
            }
        });
        passwordEditLiearlayout = (LinearLayout)findViewById(R.id.password_edit);
        passwordEditLiearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_pwd = new Intent();
                intent_pwd.setClass(getApplicationContext(), UserPasswordEditActivity.class);
                startActivity(intent_pwd);
            }
        });

    }

    /**
     * 按键监听，此处即toolbar上按键
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}