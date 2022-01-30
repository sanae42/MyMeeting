package com.example.mymeeting;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mymeeting.bomb.Meeting;
import com.example.mymeeting.bomb._User;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

import static org.litepal.LitePalApplication.getContext;

public class MeetingActivity extends AppCompatActivity {

    //TODO:传参可以用传递class
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
        CardView meetingEditCardView = (CardView)findViewById(R.id.meeting_edit_cardview);
        if(meeting.getIfOriginator()==false){
            meetingEditCardView.setVisibility(View.GONE);
        }else if(meeting.getIfOriginator()==true){
            meetingEditCardView.setVisibility(View.VISIBLE);
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
                                                        Toast.makeText(getContext(), "参会成功，请手动刷新会议列表", Toast.LENGTH_SHORT).show();
                                                        //TODO：因为是adapter发起的跳转，所以不能用startActivityForResult，自然也不能返回结果让fragment刷新列表了
                                                        //返回主活动，刷新两个列表
//                                                        Intent intent = new Intent();
//                                                        setResult(RESULT_OK,intent);
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
                                                        Toast.makeText(getContext(), "退会成功，请手动刷新会议列表", Toast.LENGTH_SHORT).show();
                                                        //TODO：因为是adapter发起的跳转，所以不能用startActivityForResult，自然也不能返回结果让fragment刷新列表了
                                                        //返回主活动，刷新两个列表
//                                                        Intent intent = new Intent();
//                                                        setResult(RESULT_OK,intent);
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

