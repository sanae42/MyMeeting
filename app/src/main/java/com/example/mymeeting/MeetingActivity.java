package com.example.mymeeting;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MeetingActivity extends AppCompatActivity {


//    public static final String MEETING_NAME = "fruit_name";
//
//    public static final String MEETING_IMAGE_ID = "fruit_image_id";
//
//    public static final String OBJECT_ID = "";
//
//    public static final String TEXT = "";

//    public static final meetingItem MEETING_ITEM = null;

    //TODO:传参可以用传递class
    meetingItem meeting;

//    TODO：状态栏透明化暂未实现

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

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        if(meeting.getIfParticipant()==false){
            fab.setImageResource(R.drawable.attend);
        }else if(meeting.getIfParticipant()==true){
            fab.setImageResource(R.drawable.leave);
        }
        //TODO：设置通过点按fab参加和退出会议，修改bomb成功后退出到主活动并刷新
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(meeting.getIfParticipant()==false){

                }else if(meeting.getIfParticipant()==true){

                }
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
}
