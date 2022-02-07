package com.example.mymeeting.calendar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
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
            c.setYear(m.getHostDate().getYear());
            c.setMonth(m.getHostDate().getMonth());
            c.setDay(m.getHostDate().getDay());
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
                getDataFromLitePal(calendar.getSelectedCalendar());
            }
        });

        getDataFromLitePal(calendar.getSelectedCalendar());


    }

    private void getDataFromLitePal(Calendar c){
        List<meetingItem> meetings = DataSupport.findAll(meetingItem.class);
        calendarMeetingList.clear();
        for(meetingItem m:meetings){
            if(m.getHostDate().getYear()==c.getYear() && m.getHostDate().getMonth()==c.getMonth() && m.getHostDate().getDay()==c.getDay()){
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