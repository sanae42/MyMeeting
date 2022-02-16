package com.example.mymeeting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mymeeting.activityCollector.BaseActivity;
import com.example.mymeeting.allParticipants.AllParticipantsActivity;
import com.example.mymeeting.bomb.Meeting;
import com.example.mymeeting.bomb._User;
import com.example.mymeeting.chat.ConversationActivity;
import com.example.mymeeting.db.meetingItem;
import com.example.mymeeting.group.MeetingGroupActivity;
import com.example.mymeeting.map.MapActivity;
import com.example.mymeeting.note.AllNoteActivity;
import com.example.mymeeting.sign.MeetingSignActivity;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

import static org.litepal.LitePalApplication.getContext;

public class MeetingActivity extends BaseActivity {

    //主活动通过adapter传递的meetingItem
    meetingItem meeting;

    //加载进度条
    ProgressDialog progressDialog;

//    TODO：状态栏透明化暂未实现

    private String appkey = "de0d0d10141439f301fc9d139da66920";

    private static final String TAG = "EditMeetingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);

        //从跳转的活动得到传值
        Intent intent = getIntent();
        meeting = (meetingItem)intent.getSerializableExtra("meeting_item");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ImageView fruitImageView = (ImageView) findViewById(R.id.fruit_image_view);
        TextView fruitContentText = (TextView) findViewById(R.id.fruit_content_text);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbar.setTitle(meeting.getName()+""+meeting.getObjectId());
        Glide.with(this).load(meeting.getImageId()).into(fruitImageView);

        String fruitContent = generateFruitContent(meeting.getComtent());
//        if(meeting.getIfOriginator()==true)fruitContent.concat(" 是申请者");
        if(meeting.getIfOriginator()==true)fruitContent="sdfgfdsdfghgfd";
        if(meeting.getIfParticipant()==true)fruitContent="++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";
        fruitContentText.setText(fruitContent);

        //悬浮按钮，根据参会状态不同设置不同样式
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        //未参会时不显示fab
        if(BmobUser.isLogin()==false){
            fab.setVisibility(View.GONE);
        }
        if(meeting.getIfParticipant()==false){
            fab.setImageResource(R.drawable.attend);
        }else if(meeting.getIfParticipant()==true){
            fab.setImageResource(R.drawable.leave);
        }
        //悬浮按钮监听，根据参会状态不同设置点按参会/退会
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(meeting.getIfParticipant()==false){
                    //没参会的时候，申请参会
                    attendMeeting();
                }else if(meeting.getIfParticipant()==true){
                    //参会的时候，申请取消参会
                    if(meeting.getIfOriginator()==false){
                        exitMeeting();
                    }else if(meeting.getIfOriginator()==true){
                        //会议建立者不可以退出会议
                        Toast.makeText(getContext(), "您是会议建立者，不可以退出会议", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //会议设置CardView，根据会议建立者状态不同设置显示和隐藏
        View meetingEditCardView = (View) findViewById(R.id.meeting_edit_cardview);
        if(meeting.getIfOriginator()==false){
            meetingEditCardView.setVisibility(View.GONE);
        }else if(meeting.getIfOriginator()==true){
            meetingEditCardView.setVisibility(View.VISIBLE);
        }

        //会议功能CardView，根据会议参加者状态不同设置显示和隐藏
        View meetingFunctionCardView = (View) findViewById(R.id.meeting_function_cardview);
        if(meeting.getIfParticipant()==false){
            meetingFunctionCardView.setVisibility(View.GONE);
        }else if(meeting.getIfParticipant()==true){
            meetingFunctionCardView.setVisibility(View.VISIBLE);
        }

        //会议功能之———全部参会者
        LinearLayout allParticipants = (LinearLayout)findViewById(R.id.all_participants);
        allParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), AllParticipantsActivity.class);
                intent.putExtra("meeting",meeting);
                startActivity(intent);
            }
        });

        //会议功能之———会议导航
        LinearLayout meetingMap = (LinearLayout)findViewById(R.id.meeting_map);
        meetingMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });

        //会议功能之———会议布告板
        LinearLayout meetingNotification = (LinearLayout)findViewById(R.id.meeting_notification);
        meetingNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MeetingGroupActivity.class);
                intent.putExtra("meeting",meeting);
                startActivity(intent);
            }
        });

        //会议功能之———会议详情
        LinearLayout meetingDetail = (LinearLayout)findViewById(R.id.meeting_detail);
        meetingDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //会议功能之———会议笔记
        LinearLayout meetingNote = (LinearLayout)findViewById(R.id.meeting_note);
        meetingNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), AllNoteActivity.class);
                intent.putExtra("type","meeting");
                intent.putExtra("meeting",meeting);
                startActivity(intent);
            }
        });

        //会议功能之———会议签到
        LinearLayout meetingSign = (LinearLayout)findViewById(R.id.meeting_sign);
        meetingSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MeetingSignActivity.class);
//                intent.putExtra("type","meeting");
                intent.putExtra("meeting",meeting);
                startActivity(intent);
            }
        });

        //会议功能之———会议群组
        LinearLayout meetingGroup = (LinearLayout)findViewById(R.id.meeting_group);
        meetingGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //展示进度条
                showProgress();
                easeLoginThenGotoGroup();
            }
        });

        //会议功能之———编辑会议
        LinearLayout editThisMeeting = (LinearLayout)findViewById(R.id.edit_this_meeting);
        editThisMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), EditMeetingActivity.class);
                intent.putExtra("type","edit");
                intent.putExtra("meeting",meeting);
                //监听返回
                startActivityForResult(intent,1);
            }
        });

        //会议功能之———删除会议
        LinearLayout deleteThisMeeting = (LinearLayout)findViewById(R.id.delete_this_meeting);
        deleteThisMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMeeting();
            }
        });

    }

    /**
     * 登录环信并进入群聊,调用gotoGroup
     */
    private void easeLoginThenGotoGroup(){
        String username = BmobUser.getCurrentUser().getUsername();
        String password = "1";
        //已经登录
        if (EMClient.getInstance().isLoggedInBefore()){
            //进入群聊
            gotoGroup();
        }else {
            //没有登录，开始登录
            EMClient.getInstance().login(username, password, new EMCallBack() {
                /**
                 * 登陆成功的回调
                 */
                @Override
                public void onSuccess() {
                    //进入群聊
                    gotoGroup();
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
                            //取消展示进度条
                            progressDialog.dismiss();
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
    }

    /**
     * 登录环信并加入群聊,调用joinGroup
     */
    private void easeLoginThenJoinGroup(String groupId){
        String username = BmobUser.getCurrentUser().getUsername();
        String password = "1";
        //已经登录
        if (EMClient.getInstance().isLoggedInBefore()){
            //加入群聊
            joinGroup(groupId);
        }else {
            //没有登录，开始登录
            EMClient.getInstance().login(username, password, new EMCallBack() {
                /**
                 * 登陆成功的回调
                 */
                @Override
                public void onSuccess() {
                    //加入群聊
                    joinGroup(groupId);
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
    }

    /**
     * 进入群聊
     */
    private void gotoGroup(){
        //取消展示进度条
        progressDialog.dismiss();

        String groupId = meeting.getGroupId();
        if(groupId==null || groupId.equals("")){
            Toast.makeText(getContext(), "因为某些原因会议群组未成功创建", Toast.LENGTH_LONG).show();
            return;
        }

        // 跳转到聊天界面，开始聊天
        Intent intent = new Intent(MeetingActivity.this, ConversationActivity.class);
        // EaseUI封装的聊天界面需要这两个参数，聊天者的username，以及聊天类型，单聊还是群聊
        intent.putExtra("conversationId", groupId);
        intent.putExtra("chatType", EMMessage.ChatType.GroupChat);
        //优先漫游
//        intent.putExtra("isRoaming", true);
        startActivity(intent);
    }

    /**
     * 加入群聊
     */
    private void joinGroup(String groupId){
        if(groupId==null || groupId.equals("")){
            Toast.makeText(getContext(), "因为某些原因会议群组未成功创建", Toast.LENGTH_LONG).show();
            return;
        }
        try {

            EMClient.getInstance().groupManager().joinGroup(groupId);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //群组加入成功
                    Toast.makeText(getContext(), "会议群组加入成功" , Toast.LENGTH_SHORT).show();
                }
            });
        } catch (HyphenateException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //群组加入失败
                    int errorCode = e.getErrorCode();
                    String message = e.getMessage();
                    Toast.makeText(getContext(), "会议群组加入失败" +errorCode+" "+message, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "会议群组加入失败: "+errorCode+" "+message);
                }
            });
        }

    }

    /**
     * 删除会议
     */
    private void deleteMeeting(){
        new AlertDialog.Builder(this)
                .setTitle("删除会议")//设置标题
                .setMessage("是否删除这个会议?")//提示消息
                .setIcon(R.mipmap.ic_launcher)//设置图标
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    //点击确定按钮执行的事件
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //展示进度条
                        showProgress();
                        Bmob.initialize(getContext(),appkey);
                        Meeting m = new Meeting();
                        //通过设置state字段实现删除，实际上会议仍然保留在线上数据库
                        m.setState("delete");
                        m.update(meeting.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Log.d(TAG, "删除会议成功");
                                    Toast.makeText(getContext(), "删除会议成功", Toast.LENGTH_SHORT).show();
                                    //回到主活动刷新列表
                                    Intent intent = new Intent();
                                    setResult(RESULT_OK,intent);
                                    finish();
                                } else {
                                    //取消展示进度条
                                    progressDialog.dismiss();
                                    Log.d(TAG, "删除会议失败"+e.getMessage());
                                    Toast.makeText(getContext(), "删除会议失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    //点击取消按钮执行的事件
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create()//创建对话框
                .show();//显示对话框

    }

    /**
     * 接收活动返回结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                //监听到编辑活动返回的结果，编辑现有会议成功，再返回主活动，让主活动刷新list，相当于间接实现了在EditMeetingActivity编辑会议成功刷新主活动list
                if(resultCode==RESULT_OK){
                    Intent intent = new Intent();
                    setResult(RESULT_OK,intent);
                    finish();
                }
                break;
            default:
        }
    }

    //TODO：参会和退会这里，两个表添加relation的顺序要好好研究一下，防止一个表加上了一个表没加上的情况造成的不良影响，或者说如果真的出现这种情况，后面再回退或者重新申请？
    /**
     * 参加会议调用
     */
    private void attendMeeting(){
        //展示进度条
        showProgress();
        //初始化bomb
        Bmob.initialize(getContext(),appkey);

        //先在_User的attendingMeeting表里添加会议
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BmobRelation relation = new BmobRelation();
                Meeting m = new Meeting();
                m.setObjectId(meeting.getObjectId());
                relation.add(m);
                _User u = new _User();
                u.setObjectId(BmobUser.getCurrentUser().getObjectId());
                u.setAttendingMeeting(relation);
                u.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            //_User的attendingMeeting表里添加会议成功
                            Log.d(TAG, "第一步在_User的attendingMeeting表里添加会议成功");
                            //接下来在Meeting的participant表里添加用户
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    BmobRelation relation = new BmobRelation();
                                    _User u = new _User();
                                    u.setObjectId(BmobUser.getCurrentUser().getObjectId());
                                    relation.add(u);
                                    Meeting m = new Meeting();
                                    m.setObjectId(meeting.getObjectId());
                                    m.setParticipant(relation);
                                    m.update(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if(e==null){
                                                //Meeting的participant表里添加用户也成功了
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Log.d(TAG, "参会成功");
                                                        Toast.makeText(getContext(), "参会成功", Toast.LENGTH_SHORT).show();

                                                        //加入会议群组（非创建者）
                                                        new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                easeLoginThenJoinGroup(meeting.getGroupId());
                                                            }
                                                        }).start();

                                                        //返回主活动，刷新两个列表
                                                        //加入/退出会议成功，因为在adapter里强制转换mContext为MainActivity，使用startActivityForResult，可以不用手动刷新了
                                                        Intent intent = new Intent();
                                                        setResult(RESULT_OK,intent);
                                                        finish();
                                                    }
                                                });
                                            }else{
                                                //Meeting的participant表里添加用户失败
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //取消展示进度条
                                                        progressDialog.dismiss();
                                                        Log.d(TAG, "参会失败，第二步在Meeting的participant表里添加用户失败了"+e.getMessage());
                                                        Toast.makeText(getContext(), "参会失败："+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            });
                        }else{
                            //_User的attendingMeeting表里添加会议失败
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //取消展示进度条
                                    progressDialog.dismiss();
                                    Log.d(TAG, "参会失败，第一步在_User的attendingMeeting表里添加会议就失败了"+e.getMessage());
                                    Toast.makeText(getContext(), "参会失败："+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });

            }
        });
    }

    //TODO：参会和退会这里，两个表添加relation的顺序要好好研究一下，防止一个表加上了一个表没加上的情况造成的不良影响，或者说如果真的出现这种情况，后面再回退或者重新申请？
    /**
     * 退出会议调用
     */
    private void exitMeeting(){
        //展示进度条
        showProgress();
        //初始化bomb
        Bmob.initialize(getContext(),appkey);

        //先在_User的attendingMeeting表里移除会议
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BmobRelation relation = new BmobRelation();
                Meeting m = new Meeting();
                m.setObjectId(meeting.getObjectId());
                relation.remove(m);
                _User u = new _User();
                u.setObjectId(BmobUser.getCurrentUser().getObjectId());
                u.setAttendingMeeting(relation);
                u.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            //_User的attendingMeeting表里移除会议成功
                            Log.d(TAG, "第一步在_User的attendingMeeting表里移除会议成功");
                            //接下来在Meeting的participant表里移除用户
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    BmobRelation relation = new BmobRelation();
                                    _User u = new _User();
                                    u.setObjectId(BmobUser.getCurrentUser().getObjectId());
                                    relation.remove(u);
                                    Meeting m = new Meeting();
                                    m.setObjectId(meeting.getObjectId());
                                    m.setParticipant(relation);
                                    m.update(new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if(e==null){
                                                //Meeting的participant表里移除用户也成功了
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Log.d(TAG, "退出成功");
                                                        Toast.makeText(getContext(), "退会成功", Toast.LENGTH_SHORT).show();
                                                        //返回主活动，刷新两个列表
                                                        //加入/退出会议成功，因为在adapter里强制转换mContext为MainActivity，使用startActivityForResult，可以不用手动刷新了
                                                        Intent intent = new Intent();
                                                        setResult(RESULT_OK,intent);
                                                        finish();
                                                    }
                                                });
                                            }else{
                                                //Meeting的participant表里添加用户失败
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //取消展示进度条
                                                        progressDialog.dismiss();
                                                        Log.d(TAG, "退会失败，第二步在Meeting的participant表里移除用户失败了"+e.getMessage());
                                                        Toast.makeText(getContext(), "参会失败："+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            });
                        }else{
                            //_User的attendingMeeting表里添加会议失败
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //取消展示进度条
                                    progressDialog.dismiss();
                                    Log.d(TAG, "退会失败，第一步在_User的attendingMeeting表里移除会议就失败了"+e.getMessage());
                                    Toast.makeText(getContext(), "退会失败："+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });

            }
        });
    }



    private String generateFruitContent(String text) {
        StringBuilder fruitContent = new StringBuilder();
        fruitContent.append(text);
//        for (int i = 0; i < 500; i++) {
//            fruitContent.append(fruitName);
//        }
        return fruitContent.toString();
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

