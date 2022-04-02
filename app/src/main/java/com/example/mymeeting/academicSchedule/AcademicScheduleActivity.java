package com.example.mymeeting.academicSchedule;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mymeeting.R;
import com.example.mymeeting.activityCollector.BaseActivity;
import com.example.mymeeting.bomb.GroupMessage;
import com.example.mymeeting.bomb.Meeting;
import com.example.mymeeting.bomb.Schedule;
import com.example.mymeeting.bomb._User;
import com.example.mymeeting.db.meetingItem;
import com.example.mymeeting.group.Msg;
import com.example.mymeeting.group.MsgAdapter;
import com.example.mymeeting.note.AllNoteListAdapter;
import com.example.mymeeting.note.EditNoteActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import static org.litepal.LitePalApplication.getContext;



public class AcademicScheduleActivity extends BaseActivity {

    private static final String TAG = "AcademicScheduleAct";

    //传参传递的会议class
    meetingItem meeting;

    Toolbar toolbar;

    private List<Schedule> scheduleList = new ArrayList<Schedule>();

//    private EditText inputText;
//
//    private Button send;

    private RecyclerView recyclerView;

    //进度条
    ProgressDialog progressDialog;

    //列表适配器
    private ScheduleListAdapter adapter;

    EditText start_edittext;
    EditText end_edittext;
    EditText title_edittext;
    EditText date_edittext;
    EditText content_edittext;
    Button submit_btn;

    CardView new_schedule_cardview;

    //比较两条日程日期的比较器
    Comparator<Schedule> comparator = new Comparator<Schedule>() {
        @Override
        public int compare(Schedule o1, Schedule o2) {
            int i1 = (Integer.parseInt(o1.getYear())-1900)*367*32 + (Integer.parseInt(o1.getMonth()))*32 + Integer.parseInt(o1.getDay());
            int i2 = (Integer.parseInt(o2.getYear())-1900)*367*32 + (Integer.parseInt(o2.getMonth()))*32 + Integer.parseInt(o2.getDay());
            if(i1 > i2)return 1;
            else return -1;
        }
    } ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academic_schedule);

        //从跳转的活动得到传值
        Intent intent = getIntent();
        meeting = (meetingItem)intent.getSerializableExtra("meeting");

        //    初始化控件
        initiateView();

        // 初始化数据
        getScheduleFromBomb();

    }

    private void initiateView() {
//        inputText = (EditText) findViewById(R.id.input_text);
//        send = (Button) findViewById(R.id.send);
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

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ScheduleListAdapter(scheduleList);
        recyclerView.setAdapter(adapter);
        //添加recyclerView项目监听器
        adapter.setOnClickListener(new ScheduleListAdapter.OnClickListener() {
            @Override
            public void onClick(View itemView, int position) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), AcademicScheduleDetailActivity.class);
                intent.putExtra("schedule",scheduleList.get(position));
                //监听返回
                startActivityForResult(intent,1);
            }

            @Override
            public void onLongClick(View itemView, int position) {

            }
        });

        start_edittext = (EditText) findViewById(R.id.start_edittext);
        end_edittext = (EditText) findViewById(R.id.end_edittext);
        title_edittext = (EditText) findViewById(R.id.title_edittext);
        date_edittext = (EditText) findViewById(R.id.date_edittext);
        content_edittext = (EditText) findViewById(R.id.content_edittext);
        submit_btn = (Button) findViewById(R.id.submit_btn);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitScheduleToBomb();
            }
        });

        new_schedule_cardview = (CardView)findViewById(R.id.new_schedule_cardview);
        if(meeting.getIfOriginator()==true){
            new_schedule_cardview.setVisibility(View.VISIBLE);
        }else {
            new_schedule_cardview.setVisibility(View.GONE);
        }
    }



    private void submitScheduleToBomb(){


        String title = title_edittext.getText().toString();
        String content = content_edittext.getText().toString();
        String date = date_edittext.getText().toString();
        String start = start_edittext.getText().toString();
        String end = end_edittext.getText().toString();

        //

        //展示进度条
        showProgress();

        Schedule schedule = new Schedule();
        schedule.setDate(date);
        schedule.setTitle(title);
        schedule.setContent(content);
        schedule.setStart(start);
        schedule.setEnd(end);
        Meeting m = new Meeting();
        m.setObjectId(meeting.getObjectId());
        schedule.setMeeting(m);
        schedule.setSender(BmobUser.getCurrentUser(_User.class));
        schedule.setState("normal");
        schedule.setType("normal");

        new Thread(new Runnable() {
            @Override
            public void run() {
                schedule.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e==null){
                            Log.d(TAG, "上传日程信息成功");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //取消展示进度条
                                    progressDialog.dismiss();
                                    //获取服务器数据刷新列表
                                    getScheduleFromBomb();
                                }
                            });
                        }else{
                            Log.d(TAG, "上传日程信息失败：" + e.getMessage());
                            Toast.makeText(getContext(), "上传日程信息失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //取消展示进度条
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }
                });

            }
        }).start();

    }

    private void getScheduleFromBomb(){
        //展示进度条
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                BmobQuery<Schedule> bmobQuery = new BmobQuery<>();
                //搜索条件
                Meeting m = new Meeting();
                m.setObjectId(meeting.getObjectId());
                bmobQuery.addWhereEqualTo("meeting", m);
                bmobQuery.findObjects(new FindListener<Schedule>() {
                    @Override
                    public void done(List<Schedule> list, BmobException e) {
                        if(e==null){
                            Log.d(TAG, "获取学术会议日程数据成功，list长度："+list.size());

                            //没有内容就退出此方法
                            if(list.size()==0) {
                                //取消展示进度条
                                progressDialog.dismiss();
                                return;
                            }

                            //清空列表
                            scheduleList.clear();
                            for(Schedule s:list){

                                //加入list
                                scheduleList.add(s);

                            }

                            //刷新recyclerView布局
                            Collections.sort(scheduleList,comparator);
                            adapter.notifyDataSetChanged();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //取消展示进度条
                                    progressDialog.dismiss();
                                }
                            });
                        }else{
                            Log.d(TAG, "获取学术会议日程数据失败：" + e.getMessage());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //取消展示进度条
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }



    /**
     * 展示进度条
     */
    public  void showProgress(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("正在和服务器同步数据");
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
