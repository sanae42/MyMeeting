package com.example.mymeeting.note;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mymeeting.R;
import com.example.mymeeting.activityCollector.BaseActivity;
import com.example.mymeeting.db.meetingItem;
import com.example.mymeeting.db.noteItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditNoteActivity extends BaseActivity {

    final String TAG = "EditNoteActivity";

    //操作类型：new：新建笔记，不传，不能新建  meeting：展示当前会议笔记，可以新建当前会议笔记
    //new：新建笔记，传参meeting
    //edit：编辑笔记，传参note
    //show：展示笔记，传参note
    private String editType;

    //传参可以用传递class
    meetingItem meeting;
    noteItem note;

    FloatingActionButton saveFab;
    FloatingActionButton editFab;

    TextView titleShow;
    EditText titleEdit;
    TextView detail;
    TextView contentShow;
    TextInputEditText contentEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        //从跳转的活动得到传值
        Intent intent = getIntent();
        editType = intent.getStringExtra("type");
        if(editType.equals("new")){
            meeting = (meetingItem) intent.getSerializableExtra("meeting");
        }
        else if(editType.equals("edit")) {
            note = (noteItem) intent.getSerializableExtra("note");
        }
        else if(editType.equals("show")){
            note = (noteItem) intent.getSerializableExtra("note");
        }

        initiateView();

    }

    //初始化布局
    private void initiateView(){
        titleShow = (TextView) findViewById(R.id.title_show);
        titleEdit = (EditText) findViewById(R.id.title_edit);
        detail = (TextView) findViewById(R.id.detail);
        contentShow = (TextView) findViewById(R.id.content_show);
        contentEdit = (TextInputEditText) findViewById(R.id.contentEditText);

        if(editType.equals("new")){
            titleShow.setVisibility(View.GONE);
            contentShow.setVisibility(View.GONE);
            titleEdit.setText("会议"+meeting.getName()+"的笔记");
            detail.setText("会议名："+meeting.getName()+"/n会议id"+meeting.getObjectId());
        }
        else if(editType.equals("edit")) {

        }
        else if(editType.equals("show")){

        }

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

        saveFab= (FloatingActionButton) findViewById(R.id.save_fab);
        editFab= (FloatingActionButton) findViewById(R.id.edit_fab);
        saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
            }
        });
        editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
            }
        });
        if(editType.equals("new")){
            editFab.setVisibility(View.GONE);
            saveFab.setVisibility(View.VISIBLE);
        }
        else if(editType.equals("edit")) {

        }
        else if(editType.equals("show")){

        }



//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                noteItem n = new noteItem();
//                n.setContent("id字段可以不写，LitePal会自己生成，LitePal不支持自定义主键。so，App数据中的id无法保存。也就是这样，如果后台返回数据中有id字段并且业务逻辑中需要用到id，要添加一个字段代替id，");
//                n.setCreateDate(new Date());
//                n.setUpdateDate(new Date());
//                n.setTitle("这是一个题目");
//                n.setType("normal");
//                n.setMeetingBombId(meeting.getBombId());
//                n.setMeetingName(meeting.getName());
//                n.setMeetingObjectId(meeting.getObjectId());
//                n.save();
//
//            }
//        });
//        //展示全部会议时不可新建会议
//        if(editType.equals("all")){
//            fab.setVisibility(View.GONE);
//        }


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