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
    TextView Forgetpassword;
    Button Sign_in;
    Button Sign_up;

    ProgressDialog progressDialog;

    String appkey = "de0d0d10141439f301fc9d139da66920";

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
        //        导航条
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //   设置actionbar（即toolbar）最左侧按钮显示状态和图标
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        Username = (EditText)findViewById(R.id.Username);
        Password = (EditText)findViewById(R.id.Password);
        Forgetpassword = (TextView) findViewById(R.id.Forgetpassword);
        Sign_in = (Button) findViewById(R.id.Sign_in);
        Sign_up = (Button) findViewById(R.id.Sign_up);

        Sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
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
     * 判断密码是否只含字母数字
     */
    public boolean isLetterDigit(String str) {
        String regex = "^[a-z0-9A-Z]+$";
        return str.matches(regex);
    }

    /**
     * 环信注册调用，环信注册成功后则调用bombSignup注册bomb
     */
    public void signup(){
        String username = Username.getText().toString();
        String password = Password.getText().toString();
        if(username.length()==0 || password.length()==0){
            Toast.makeText(getContext(), "用户名或密码为空，请重新输入", Toast.LENGTH_SHORT).show();
            return;
        }
        //用户名必须长度小于15，为了与环信的群组id相区别，
        // 因为当前((EMConversation)item).isGroup()和((EMConversation)item).getType()都不能判断一个会话是群组还是私聊，因此只能用id长度判断
        if(isLetterDigit(username)==false || username.length()>=15){
            Toast.makeText(getContext(), "用户名不符合规范 用户名应该由长度小于15的字母和数字序列组成", Toast.LENGTH_SHORT).show();
            return;
        }

        //展示进度条
        showProgress();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    //环信注册密码是“1”
                    EMClient.getInstance().createAccount(username, "1");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //环信注册成功，调用bombSignup注册bomb
                            bombSignup();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            /**
                             * 关于错误码可以参考环信官方api详细说明
                             * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1_e_m_error.html
                             */
                            int errorCode = e.getErrorCode();
                            String message = e.getMessage();
                            switch (errorCode) {
                                // 网络错误
                                case EMError.NETWORK_ERROR:
                                    Toast.makeText(getContext(), "网络错误 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                    break;
                                // 用户已存在
                                case EMError.USER_ALREADY_EXIST:
                                    Toast.makeText(getContext(), "用户已存在 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                    break;
                                // 参数不合法，一般情况是username 使用了uuid导致，不能使用uuid注册
                                case EMError.USER_ILLEGAL_ARGUMENT:
                                    Toast.makeText(getContext(), "参数不合法，一般情况是username 使用了uuid导致，不能使用uuid注册 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                    break;
                                // 服务器未知错误
                                case EMError.SERVER_UNKNOWN_ERROR:
                                    Toast.makeText(getContext(), "服务器未知错误 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                    break;
                                case EMError.USER_REG_FAILED:
                                    Toast.makeText(getContext(), "账户注册失败 code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    Toast.makeText(getContext(), "ml_sign_up_failed code: " + errorCode + ", message:" + message, Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * bomb注册调用
     */
    public void bombSignup(){
        String username = Username.getText().toString();
        String password = Password.getText().toString();

        new Thread(){
            @Override
            public void run() {
                Bmob.initialize(getContext(),appkey);
                final _User user = new _User();
                user.setUsername(username);
                user.setPassword(password);

                user.signUp(new SaveListener<_User>() {
                    @Override
                    public void done(_User user, BmobException e) {
                        if (e == null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "bomb注册成功", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent();
                                    intent.putExtra("login",true);
                                    setResult(RESULT_OK,intent);
                                    finish();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "bomb注册失败 "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        }.start();
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
                Bmob.initialize(getContext(),appkey);
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
                                    Intent intent = new Intent();
                                    intent.putExtra("login",true);
                                    setResult(RESULT_OK,intent);
                                    finish();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "登录失败 "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent();
                                    intent.putExtra("login",false);
                                    setResult(RESULT_OK,intent);
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