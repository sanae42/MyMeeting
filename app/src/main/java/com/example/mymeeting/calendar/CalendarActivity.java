package com.example.mymeeting.calendar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mymeeting.R;
import com.example.mymeeting.activityCollector.BaseActivity;
import com.example.mymeeting.db.meetingItem;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class CalendarActivity extends BaseActivity {

    CalendarView calendar;

    RelativeLayout meeting_list_layout;
    RelativeLayout no_meeting_layout;
    TextView no_meeting_textview;

    private CalendarMeetingListAdapter adapter;

    private static final String TAG = "CalendarActivity";

    //recyclerview内容
    private List<meetingItem> calendarMeetingList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        //        recyclerview设置
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        //        适配器设置，设置显示1列
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CalendarMeetingListAdapter(calendarMeetingList);
        recyclerView.setAdapter(adapter);



        calendar = (CalendarView)findViewById(R.id.calendar);

        List<meetingItem> meetings = DataSupport.findAll(meetingItem.class);

        for(meetingItem m:meetings){
            Calendar c = new Calendar();
            c.setYear(m.getHostDate().getYear()+1900);
            c.setMonth(m.getHostDate().getMonth()+1);
            c.setDay(m.getHostDate().getDate());
            Log.d(TAG, "全部日期0："+m.getHostDate().toString());
            Log.d(TAG, "全部本地会议日期1："+m.getHostDate().getYear()+" "+m.getHostDate().getMonth()+" "+m.getHostDate().getDay());
            Log.d(TAG, "全部本地数据库会议日期2："+c.getYear()+" "+c.getMonth()+" "+c.getDay());
            calendar.addSchemeDate(c);
        }

        meeting_list_layout = (RelativeLayout)findViewById(R.id.meeting_list_layout);
        no_meeting_layout = (RelativeLayout)findViewById(R.id.no_meeting_layout);
        no_meeting_textview = (TextView)findViewById(R.id.no_meeting_textview);

        calendar.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(Calendar calendar) {

            }

            @Override
            public void onCalendarSelect(Calendar c, boolean isClick) {
//                Log.d(TAG, "日历选中："+calendar.getSelectedCalendar().getYear()+" "+calendar.getSelectedCalendar().getMonth()+" "+calendar.getSelectedCalendar().getDay());
                getDataFromLitePal(calendar.getSelectedCalendar());
            }
        });

        getDataFromLitePal(calendar.getSelectedCalendar());


    }

    private void getDataFromLitePal(Calendar c){
        List<meetingItem> meetings = DataSupport.findAll(meetingItem.class);
        calendarMeetingList.clear();
        for(meetingItem m:meetings){
            if((m.getHostDate().getYear()+1900)==c.getYear() && (m.getHostDate().getMonth()+1)==c.getMonth() && m.getHostDate().getDate()==c.getDay()){
                Log.d(TAG, "日历选中："+calendar.getSelectedCalendar().getYear()+" "+calendar.getSelectedCalendar().getMonth()+" "+calendar.getSelectedCalendar().getDay());
                Log.d(TAG, "日历选中2："+m.getHostDate().getYear()+" "+m.getHostDate().getMonth()+" "+m.getHostDate().getDay());
                calendarMeetingList.add(m);
            }
        }
        if(calendarMeetingList.size() > 0){
            meeting_list_layout.setVisibility(View.VISIBLE);
            no_meeting_layout.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
        else if(calendarMeetingList.size() == 0) {
            meeting_list_layout.setVisibility(View.GONE);
            no_meeting_layout.setVisibility(View.VISIBLE);
            no_meeting_textview.setText(c.getYear()+"年"+c.getMonth()+"月"+c.getDay()+"日"+"这一天没有会议哦");
        }
    }

}