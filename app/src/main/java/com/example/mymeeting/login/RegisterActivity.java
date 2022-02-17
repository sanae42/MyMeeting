package com.example.mymeeting.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class RegisterActivity extends BaseActivity {

    // 登录注册界面控件
    EditText Username;
    EditText Password;
    EditText Password2;
    CheckBox cbSelect;
    TextView agreementText;

    Button Sign_up;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //    初始化控件
        initiateView();

    }

    /**
     * 初始化控件
     */
    private void initiateView(){

        Username = (EditText)findViewById(R.id.et_login_name);
        Password = (EditText)findViewById(R.id.et_login_pwd);
        Password2 = (EditText)findViewById(R.id.et_login_pwd_confirm);
        Sign_up = (Button) findViewById(R.id.btn_login);
        Sign_up.setEnabled(false);
        Sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        cbSelect = (CheckBox) findViewById(R.id.cb_select);
        agreementText = (TextView)findViewById(R.id.tv_agreement);
        agreementText.setText(getSpannable());
        //让超链接起作用
        agreementText.setMovementMethod(LinkMovementMethod.getInstance());

        //选择框初始未选择
        cbSelect.setChecked(false);
        cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(cbSelect.isChecked()==true){
                    Sign_up.setEnabled(true);
                }else {
                    Sign_up.setEnabled(false);
                }
            }
        });
    }

    /**
     * 设置用户服务条款字体样式
     */
    private SpannableString getSpannable() {
        SpannableString spanStr = new SpannableString("同意《用户服务条款》");
        //设置下划线
        spanStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
//                Toast.makeText(getContext(), "点击了服务条款", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://gitee.com/sanae42/MyMeeting/blob/master/%E7%94%A8%E6%88%B7%E6%9C%8D%E5%8A%A1%E6%9D%A1%E6%AC%BE.md"));
                startActivity(intent);
            }
        }, 2, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        return spanStr;
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
        String password2 = Password2.getText().toString();
        if(username.length()==0 || password.length()==0 || password2.length()==0){
            Toast.makeText(getContext(), "用户名或密码为空，请重新输入", Toast.LENGTH_SHORT).show();
            return;
        }
        //用户名必须长度小于15，为了与环信的群组id相区别，
        // 因为当前((EMConversation)item).isGroup()和((EMConversation)item).getType()都不能判断一个会话是群组还是私聊，因此只能用id长度判断
        if(isLetterDigit(username)==false || username.length()>=15){
            Toast.makeText(getContext(), "用户名不符合规范 用户名应该由长度小于15的字母和数字序列组成", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.equals(password2) == false){
            Toast.makeText(getContext(), "两次密码输入不一致", Toast.LENGTH_SHORT).show();
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



}