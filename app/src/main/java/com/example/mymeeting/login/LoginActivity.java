package com.example.mymeeting.login;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymeeting.R;
import com.example.mymeeting.activityCollector.ActivityCollector;
import com.example.mymeeting.activityCollector.BaseActivity;
import com.example.mymeeting.bomb._User;
import com.google.android.material.snackbar.Snackbar;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import static org.litepal.LitePalApplication.getContext;

public class LoginActivity extends BaseActivity {

    // 登录注册界面控件
    EditText Username;
    EditText Password;
//    TextView Forgetpassword;
    Button Sign_in;
    TextView Sign_up;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //    初始化控件
        initiateView();

    }

    /**
     * 初始化控件
     */
    private void initiateView(){
//        //        导航条
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        //   设置actionbar（即toolbar）最左侧按钮显示状态和图标
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
//            actionBar.setDisplayShowTitleEnabled(false);
//        }

        Username = (EditText)findViewById(R.id.et_login_name);
        Password = (EditText)findViewById(R.id.et_login_pwd);
//        Forgetpassword = (TextView) findViewById(R.id.Forgetpassword);
        Sign_in = (Button) findViewById(R.id.btn_login);
        Sign_up = (TextView) findViewById(R.id.tv_login_register);

        Sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
        Sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }



    /**
     * 登录调用
     */
    public void login(){
        String username = Username.getText().toString();
        String password = Password.getText().toString();
        if(username.length()==0 || password.length()==0){
            Toast.makeText(getContext(), "用户名或密码为空，请重新输入", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress();
        new Thread(){
            @Override
            public void run() {
                final _User user = new _User();
                user.setUsername(username);
                user.setPassword(password);

                user.login(new SaveListener<_User>() {
                    @Override
                    public void done(_User user, BmobException e) {
                        if (e == null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    _User u = BmobUser.getCurrentUser(_User.class);
                                    Toast.makeText(getContext(), "用户"+u.getUsername()+"登录成功", Toast.LENGTH_SHORT).show();

//                                    Intent intent = new Intent();
//                                    intent.putExtra("login",true);
//                                    setResult(RESULT_OK,intent);
//                                    finish();
                                    //这里采用广播通知主活动刷新，活动管理器退出到主活动
                                    Intent intent_broadcast = new Intent("com.example.mymeeting.REFRESH_DATA");
                                    sendBroadcast(intent_broadcast, "com.example.mymeeting.REFRESH_DATA");
                                    ActivityCollector.backToMainActivity();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "登录失败 "+ e.getMessage(), Toast.LENGTH_SHORT).show();

//                                    Intent intent = new Intent();
//                                    intent.putExtra("login",false);
//                                    setResult(RESULT_OK,intent);
                                    //这里采用广播通知主活动刷新，活动管理器退出到主活动
                                    Intent intent_broadcast = new Intent("com.example.mymeeting.REFRESH_DATA");
                                    sendBroadcast(intent_broadcast, "com.example.mymeeting.REFRESH_DATA");
                                    ActivityCollector.backToMainActivity();
                                }
                            });
                        }
                    }
                });
            }
        }.start();
    }

    /**
     * 展示进度条
     */
    public  void showProgress(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("正在获取服务器数据");
        progressDialog.setMessage("请稍后...");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    /**
     * 按键监听，此处即toolbar上按键
     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                finish();
//                break;
//        }
//        return true;
//    }



}