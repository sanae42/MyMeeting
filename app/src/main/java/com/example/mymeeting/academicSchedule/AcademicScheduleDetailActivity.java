package com.example.mymeeting.academicSchedule;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mymeeting.R;
import com.example.mymeeting.allParticipants.AllParticipantsActivity;
import com.example.mymeeting.bomb.Schedule;
import com.example.mymeeting.chat.ConversationActivity;
import com.example.mymeeting.db.meetingItem;
import com.example.mymeeting.db.noteItem;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import cn.bmob.v3.BmobUser;

import static org.litepal.LitePalApplication.getContext;

public class AcademicScheduleDetailActivity extends AppCompatActivity {

    final String TAG = "AcademicSchDetail";

    //传参可以用传递class
    Schedule schedule;

    TextView date_textview;
    TextView length_textview;
    TextView speaker_textview;
    TextView detail_textview;

    PopupWindow popupWindow;

    // 环信登录进度条弹出框
    private ProgressDialog mDialog;
//    FloatingActionButton saveFab;
////    FloatingActionButton editFab;
//
//    //    TextView titleShow;
//    EditText titleEdit;
//    TextView detail;
//    //    TextView contentShow;
//    TextInputEditText contentEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academic_schedule_detail);

        //从跳转的活动得到传值
        Intent intent = getIntent();
        schedule = (Schedule) intent.getSerializableExtra("schedule");
        Toast.makeText(getContext(), schedule.getTitle(), Toast.LENGTH_SHORT).show();

        initiateView();
    }

    //初始化布局
    private void initiateView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ImageView imageView = (ImageView) findViewById(R.id.fruit_image_view);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbar.setTitle(schedule.getTitle());
        Glide.with(this).load(R.mipmap.schedule_background).into(imageView);

        date_textview = (TextView) findViewById(R.id.date_textview);
        length_textview = (TextView) findViewById(R.id.length_textview);
        speaker_textview = (TextView) findViewById(R.id.speaker_textview);
        detail_textview = (TextView) findViewById(R.id.detail_textview);

        date_textview.setText(schedule.getDate());
        length_textview.setText(schedule.getStart()+" - "+schedule.getEnd());
        if(schedule.getSpeaker()!=null){
            //获取主讲人信息失败
            speaker_textview.setText("");
            speaker_textview.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            speaker_textview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initPopupWindow(v);
                }
            });
        }

        detail_textview.setText(schedule.getContent());
    }

    private void initPopupWindow(View view) {
        if(popupWindow == null){
            View popupView = LayoutInflater.from(AcademicScheduleDetailActivity.this).inflate(R.layout.item_popup,null);
            Button button1 = popupView.findViewById(R.id.btn_1);
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //取消展示popupWindow
                    popupWindow.dismiss();

                    Toast.makeText(getContext(), "点按事件", Toast.LENGTH_SHORT).show();
                    if (EMClient.getInstance().isLoggedInBefore()) {
                        // 如果已经登录跳转界面
                        // 跳转到聊天界面，开始聊天
                        Intent intent = new Intent(AcademicScheduleDetailActivity.this, ConversationActivity.class);
                        // EaseUI封装的聊天界面需要这两个参数，聊天者的username，以及聊天类型，单聊还是群聊
                        intent.putExtra("conversationId", schedule.getSpeaker().getUsername());
                        intent.putExtra("chatType", EMMessage.ChatType.Chat);
                        //优先漫游
                        intent.putExtra("isRoaming", true);
                        startActivity(intent);
                    }else {
                        // 如果未登录进行登录
                        easeLoginThenGoToConversation(schedule.getSpeaker().getUsername());
                    }
                }
            });
            Button button2 = popupView.findViewById(R.id.btn_2);
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //取消展示popupWindow
                    popupWindow.dismiss();
                    final AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(AcademicScheduleDetailActivity.this);
                    alterDiaglog.setIcon(R.drawable.ic_baseline_person_24);//图标
                    alterDiaglog.setTitle("个人信息");//文字
                    String information = "";
                    information = "昵称 ： "+schedule.getSpeaker().getNick()+"\n";
                    information += "用户名 ： "+schedule.getSpeaker().getUsername()+"\n";
                    information += "学校 ： "+schedule.getSpeaker().getSchool()+"\n";
                    information += "注册时间 ： "+schedule.getSpeaker().getCreatedAt()+"\n";
                    information += "个人简介 ： "+schedule.getSpeaker().getIntroduction()+"\n";
                    alterDiaglog.setMessage(information);//提示消息
                    alterDiaglog.show();

                }
            });
            // 构造函数关联
            popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
        }
        // 展示popup
        popupWindow.showAsDropDown(view);
    }

    /**
     * 环信登录并跳转聊天界面
     */
    private void easeLoginThenGoToConversation(String userId){
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("正在登陆，请稍后...");
        mDialog.show();

        String username = BmobUser.getCurrentUser().getUsername();
        String password = BmobUser.getCurrentUser().getObjectId();

        EMClient.getInstance().login(username, "1", new EMCallBack() {
            /**
             * 登陆成功的回调
             */
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 登录成功跳转界面
                        // 跳转到聊天界面，开始聊天
                        Intent intent = new Intent(AcademicScheduleDetailActivity.this, ConversationActivity.class);
                        // EaseUI封装的聊天界面需要这两个参数，聊天者的username，以及聊天类型，单聊还是群聊
                        intent.putExtra("conversationId", userId);
                        //“chatType”——聊天类型，整型，分别为单聊（1）、群聊（2）和聊天室（3）；
                        //TODO:不可以使用EMMessage.ChatType.xxx，否则会出群聊不能看到其他用户消息的问题
                        intent.putExtra("chatType", 1);
                        //优先漫游
                        intent.putExtra("isRoaming", true);
                        startActivity(intent);
                        mDialog.dismiss();
                    }
                });
            }

            /**
             * 登陆错误的回调
             * @param i
             * @param s
             */
            @Override
            public void onError(final int i, final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDialog.dismiss();
                        /**
                         * 关于错误码可以参考官方api详细说明
                         * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1_e_m_error.html
                         */
                        switch (i) {
                            // 网络异常 2
                            case EMError.NETWORK_ERROR:
                                Toast.makeText(getContext(), "网络错误 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 无效的用户名 101
                            case EMError.INVALID_USER_NAME:
                                Toast.makeText(getContext(), "无效的用户名 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 无效的密码 102
                            case EMError.INVALID_PASSWORD:
                                Toast.makeText(getContext(), "无效的密码 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 用户认证失败，用户名或密码错误 202
                            case EMError.USER_AUTHENTICATION_FAILED:
                                Toast.makeText(getContext(), "用户认证失败，用户名或密码错误 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 用户不存在 204
                            case EMError.USER_NOT_FOUND:
                                Toast.makeText(getContext(), "用户不存在 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 无法访问到服务器 300
                            case EMError.SERVER_NOT_REACHABLE:
                                Toast.makeText(getContext(), "无法访问到服务器 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 等待服务器响应超时 301
                            case EMError.SERVER_TIMEOUT:
                                Toast.makeText(getContext(), "等待服务器响应超时 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 服务器繁忙 302
                            case EMError.SERVER_BUSY:
                                Toast.makeText(getContext(), "服务器繁忙 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 未知 Server 异常 303 一般断网会出现这个错误
                            case EMError.SERVER_UNKNOWN_ERROR:
                                Toast.makeText(getContext(), "未知的服务器异常 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Toast.makeText(getContext(), "ml_sign_in_failed code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    //    设置菜单栏左侧返回键
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}