package com.example.mymeeting.setting;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.example.mymeeting.MyService;
import com.example.mymeeting.R;
import com.example.mymeeting.userEdit.UserNickEditActivity;
import com.example.mymeeting.userEdit.UserPasswordEditActivity;

public class SettingActivity extends AppCompatActivity {

    Toolbar toolbar;

    Switch backgroundNotificationSwitch;

    //sp数据库 存放应用设置状态
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

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

        backgroundNotificationSwitch = (Switch) findViewById(R.id.background_notification);
        backgroundNotificationSwitch.setChecked(pref.getBoolean("backgroundNotification", true));

        backgroundNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor = pref.edit();
                editor.putBoolean("backgroundNotification", isChecked);
                editor.apply();

                if(isChecked == true){
                    Intent startIntent = new Intent(SettingActivity.this, MyService.class);
                    startService(startIntent);
                }else {
                    Intent stopIntent = new Intent(SettingActivity.this, MyService.class);
                    stopService(stopIntent);
                }
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