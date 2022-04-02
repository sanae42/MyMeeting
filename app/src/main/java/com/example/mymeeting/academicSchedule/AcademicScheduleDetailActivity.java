package com.example.mymeeting.academicSchedule;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mymeeting.R;
import com.example.mymeeting.bomb.Schedule;
import com.example.mymeeting.db.meetingItem;
import com.example.mymeeting.db.noteItem;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import static org.litepal.LitePalApplication.getContext;

public class AcademicScheduleDetailActivity extends AppCompatActivity {

    final String TAG = "AcademicSchDetail";

    //传参可以用传递class
    Schedule schedule;

    TextView date_textview;
    TextView length_textview;
    TextView speaker_textview;
    TextView detail_textview;

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
        speaker_textview.setText(schedule.getSpeaker().getNick());
        detail_textview.setText(schedule.getContent());
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