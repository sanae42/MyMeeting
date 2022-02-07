package com.example.mymeeting;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mymeeting.activityCollector.ActivityCollector;
import com.example.mymeeting.activityCollector.BaseActivity;
import com.example.mymeeting.bomb.Meeting;
import com.example.mymeeting.bomb._User;
import com.example.mymeeting.db.meetingItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.haibin.calendarview.CalendarView;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static org.litepal.LitePalApplication.getContext;

public class EditMeetingActivity extends BaseActivity {

    private String appkey = "de0d0d10141439f301fc9d139da66920";

    private static final String TAG = "EditMeetingActivity";

    //操作类型：new：新建  edit：编辑
    private String editType;

    //要编辑的会议
    private meetingItem meetingToEdit;

    EditText nameEditText;
    TextInputEditText introductionEditText;
    NiceSpinner typeSpinner;

    EditText organizerEditText;
    TextInputEditText contentEditText;
    NiceSpinner locationSpinner;

    EditText lengthEditText;
    CalendarView hostTimeCalendar;
    TimePicker hostTimePicker;

    FloatingActionButton saveFab;
    FloatingActionButton quickSaveFab; //

    //加载进度条
    ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_meeting);

        initiateView();

        //根据操作类型设置显示填写内容
        Intent intent = getIntent();
        editType = intent.getStringExtra("type");
        if(editType.equals("new")){

        }
        else if(editType.equals("edit")){
            meetingToEdit = (meetingItem)intent.getSerializableExtra("meeting");

            nameEditText.setText(meetingToEdit.getName());
            introductionEditText.setText(meetingToEdit.getIntroduction());
            typeSpinner.setSelectedIndex(0);   //后续再用内联函数之类的方式实现，懒得写了，就选0了

            organizerEditText.setText(meetingToEdit.getOrganizer());
            contentEditText.setText(meetingToEdit.getComtent());
            locationSpinner.setSelectedIndex(0);   //后续再用内联函数之类的方式实现，懒得写了，就选0了

            lengthEditText.setText(meetingToEdit.getLength());
            hostTimeCalendar.scrollToCalendar(meetingToEdit.getHostDate().getYear()+1900, meetingToEdit.getHostDate().getMonth()+1,meetingToEdit.getHostDate().getDate());
            hostTimePicker.setMinute(meetingToEdit.getHostDate().getMinutes());
            hostTimePicker.setHour(meetingToEdit.getHostDate().getHours());

        }
//        meetingItem defaultMeetingItem = new meetingItem();
//        meeting = (meetingItem)intent.getSerializableExtra("meeting_item");




    }

    /**
     * 初始化控件
     */
    private void initiateView() {
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

        nameEditText = (EditText)findViewById(R.id.nameEditText);
        introductionEditText = (TextInputEditText)findViewById(R.id.introductionEditText);
        typeSpinner = (NiceSpinner)findViewById(R.id.typeSpinner);
        ArrayList<String> dataset_type = new ArrayList<>(Arrays.asList("兴趣社团会议", "学生职能社团会议", "学术研讨会议"));
        typeSpinner.attachDataSource(dataset_type);

        organizerEditText = (EditText)findViewById(R.id.organizerEditText);
        contentEditText = (TextInputEditText)findViewById(R.id.contentEditText);
        locationSpinner = (NiceSpinner)findViewById(R.id.locationSpinner);
        ArrayList<String> dataset_loc = new ArrayList<>(Arrays.asList("sy101", "sy102", "sy103","sy104", "sy105", "sy106"));
        locationSpinner.attachDataSource(dataset_loc);

        lengthEditText = (EditText)findViewById(R.id.lengthEditText);
        hostTimeCalendar = (CalendarView)findViewById(R.id.hostTimeCalendar);
//        Log.d(TAG, "日历选中："+hostTimeCalendar.getSelectedCalendar().getYear()+" "+hostTimeCalendar.getSelectedCalendar().getMonth()+" "+hostTimeCalendar.getSelectedCalendar().getDay());
        hostTimePicker = (TimePicker)findViewById(R.id.hostTimePicker);

        //        悬浮按钮
        saveFab = (FloatingActionButton)findViewById(R.id.saveFab);
        quickSaveFab = (FloatingActionButton)findViewById(R.id.quickSaveFab); //
        saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "要提交会议吗", Snackbar.LENGTH_SHORT)
                        .setAction("是的", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(BmobUser.isLogin()==false){
                                    Toast.makeText(getContext(), "请在登录后再操作", Toast.LENGTH_SHORT).show();
                                }
                                else saveMeeting();
                            }
                        }).show();
            }
        });

    }

    /**
     * 保存提交会议
     */
    public void saveMeeting(){
        String name = nameEditText.getText().toString();
        String introduction = introductionEditText.getText().toString();
        String type = typeSpinner.getText().toString();

        String organizer = organizerEditText.getText().toString();
        String content = contentEditText.getText().toString();
        String location = locationSpinner.getText().toString();

        String length = lengthEditText.getText().toString();
        Date hDate = new Date();
//        hDate = hostTimeCalendar.getSelectedCalendar().
//        hDate.setYear(hostTimeCalendar.getSelectedCalendar().getYear());
//        hDate.setMonth(hostTimeCalendar.getSelectedCalendar().getMonth());
//        hDate.setDate(hostTimeCalendar.getSelectedCalendar().getDay());
        hDate.setTime(hostTimeCalendar.getSelectedCalendar().getTimeInMillis());
        hDate.setHours(hostTimePicker.getCurrentHour());
        hDate.setMinutes(hostTimePicker.getCurrentMinute());
        hDate.setSeconds(0);
        Log.d(TAG, "日历选中："+ hDate.toString());
        BmobDate hostDate = new BmobDate(hDate);
        BmobDate registrationDate= new BmobDate(new Date());

        Log.d(TAG, "测试获取输入框内容："+hostTimeCalendar);

        Bmob.initialize(getContext(),appkey);
        Meeting meeting = new Meeting();
        meeting.setName(name);
        meeting.setComtent(content);
        meeting.setIntroduction(introduction);
        meeting.setLength(length);
        meeting.setLocation(location);
        meeting.setOrganizer(organizer);
        meeting.setHostDate(hostDate);
        meeting.setRegistrationDate(registrationDate);

        meeting.setOriginator(BmobUser.getCurrentUser(_User.class));

        //根据操作类型分情况
        if(editType.equals("new")){
            //新建会议state为“normal”
            meeting.setState("normal");

            BmobRelation relation = new BmobRelation();
            relation.add(BmobUser.getCurrentUser(_User.class));
            meeting.setParticipant(relation);
        }
        else if(editType.equals("edit")){}


        //展示进度条
        showProgress();

        if(editType.equals("new")){
            meeting.save(new SaveListener<String>() {
                @Override
                public void done(String objectId, BmobException e) {
                    //TODO: ****_User表中有attendingMeeting的版本
                    if(e==null){
                        Log.d(TAG, "创建会议成功，返回objectId为："+objectId);
                        Toast.makeText(getContext(), "创建会议成功，返回objectId为："+objectId, Toast.LENGTH_SHORT).show();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                BmobRelation relation = new BmobRelation();
                                Meeting m = new Meeting();
                                m.setObjectId(objectId);
                                relation.add(m);
                                _User u = new _User();
                                u.setObjectId(BmobUser.getCurrentUser().getObjectId());
                                u.setAttendingMeeting(relation);
                                u.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if(e==null){
                                            Log.d(TAG, "会议和当前用户参会绑定成功");
                                            //返回主活动，刷新两个列表
                                            Intent intent = new Intent();
                                            setResult(RESULT_OK,intent);
                                            finish();
                                        }else{
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //取消展示进度条
                                                    progressDialog.dismiss();
                                                }
                                            });
                                            Log.d(TAG, "会议和当前用户参会绑定失败"+e.getMessage());
                                        }
                                    }
                                });

                            }
                        });

                    }else{
                        Log.d(TAG, "创建会议失败：" + e.getMessage());
                        Toast.makeText(getContext(), "创建会议失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else if(editType.equals("edit")){
            meeting.update(meetingToEdit.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "创建编辑成功");
                                Toast.makeText(getContext(), "创建编辑成功", Toast.LENGTH_SHORT).show();
                                //TODO : (已过时)这里应该提醒主活动list刷新，但因为不是主活动跳转的活动，没法用监听活动返回来实现; 但可以先返回MeetingActivity，在监听，再返回主活动，间接实现
                                //返回主活动，刷新两个列表
//                                Intent intent = new Intent();
//                                setResult(RESULT_OK,intent);
                                //TODO : 这里采用广播通知主活动刷新，活动管理器退出到主活动
                                Intent intent_broadcast = new Intent("com.example.mymeeting.REFRESH_DATA");
                                sendBroadcast(intent_broadcast, "com.example.mymeeting.REFRESH_DATA");
                                //取消展示进度条
                                progressDialog.dismiss();
                                ActivityCollector.backToMainActivity();

//                                // 自定义广播测试  失败
//                                Intent intent_broadcast = new Intent("com.example.mymeeting.refreshData1");
////                                intent_broadcast.setComponent(new ComponentName("com.example.mymeeting","com.example.mymeeting.broadcast.MyReceiver"));
//                                sendBroadcast(intent_broadcast);

//                                finish();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "创建编辑失败");
                                Toast.makeText(getContext(), "创建编辑失败", Toast.LENGTH_SHORT).show();
                                //取消展示进度条
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            });
        }


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