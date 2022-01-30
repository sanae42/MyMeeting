package com.example.mymeeting;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mymeeting.bomb.Meeting;
import com.example.mymeeting.bomb._User;
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

public class EditMeetingActivity extends AppCompatActivity {

    private String appkey = "de0d0d10141439f301fc9d139da66920";

    private static final String TAG = "EditMeetingActivity";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_meeting);

        Intent intent = getIntent();
        if(intent.getStringExtra("type").equals("new")){

        }
        else if(intent.getStringExtra("type").equals("edit")){

        }
//        meetingItem defaultMeetingItem = new meetingItem();
//        meeting = (meetingItem)intent.getSerializableExtra("meeting_item");

        initiateView();
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
        hDate.setYear(hostTimeCalendar.getCurYear());
        hDate.setMonth(hostTimeCalendar.getCurMonth());
        hDate.setDate(hostTimeCalendar.getCurDay());
        hDate.setHours(hostTimePicker.getCurrentHour());
        hDate.setMinutes(hostTimePicker.getCurrentMinute());
        hDate.setSeconds(0);
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

        _User user = BmobUser.getCurrentUser(_User.class);
        BmobRelation relation = new BmobRelation();
        relation.add(user);
        meeting.setParticipant(relation);

        //展示进度条
        showProgress();

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