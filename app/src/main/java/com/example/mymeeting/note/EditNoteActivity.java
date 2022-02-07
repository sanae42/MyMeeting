package com.example.mymeeting.note;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymeeting.R;
import com.example.mymeeting.activityCollector.BaseActivity;
import com.example.mymeeting.db.meetingItem;
import com.example.mymeeting.db.noteItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.litepal.LitePalApplication.getContext;

public class EditNoteActivity extends BaseActivity {

    final String TAG = "EditNoteActivity";

    //操作类型：
    //new：新建笔记，传参meeting
    //edit：编辑笔记，传参note
    private String editType;

    //传参可以用传递class
    meetingItem meeting;
    noteItem note;

    FloatingActionButton saveFab;
//    FloatingActionButton editFab;

//    TextView titleShow;
    EditText titleEdit;
    TextView detail;
//    TextView contentShow;
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

        initiateView();
    }

    //初始化布局
    private void initiateView(){
//        titleShow = (TextView) findViewById(R.id.title_show);
        titleEdit = (EditText) findViewById(R.id.title_edit);
        detail = (TextView) findViewById(R.id.detail);
//        contentShow = (TextView) findViewById(R.id.content_show);
        contentEdit = (TextInputEditText) findViewById(R.id.content_edit);

        if(editType.equals("new")){
            titleEdit.setText("会议"+meeting.getName()+"的笔记");
            detail.setText("会议名："+meeting.getName()+"\n会议id"+meeting.getBombId());
        }
        else if(editType.equals("edit")) {
            titleEdit.setText(note.getTitle());
            detail.setText("会议名："+note.getMeetingName()+"\n会议id"+note.getMeetingBombId());
            contentEdit.setText(note.getContent());
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
//        editFab= (FloatingActionButton) findViewById(R.id.edit_fab);
        saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editType.equals("new")){
                    noteItem n = new noteItem();
                    n.setContent(contentEdit.getText().toString());
                    n.setCreateDate(new Date());
                    n.setUpdateDate(new Date());
                    n.setTitle(titleEdit.getText().toString());
                    n.setType("normal");
                    n.setMeetingBombId(meeting.getBombId());
                    n.setMeetingName(meeting.getName());
                    n.setMeetingObjectId(meeting.getObjectId());
                    if(n.save()==true){
                        Toast.makeText(getContext(), "笔记保存成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                    else {
                        Toast.makeText(getContext(), "笔记保存失败", Toast.LENGTH_SHORT).show();
                    }

                }
                else if(editType.equals("edit")) {
                    noteItem n = new noteItem();
                    n.setContent(contentEdit.getText().toString());
                    n.setUpdateDate(new Date());
                    n.setTitle(titleEdit.getText().toString());
                    if(n.update(note.getId())>0){
                        Toast.makeText(getContext(), "笔记保存成功", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getContext(), "笔记保存失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        if(editType.equals("new")){

        }
        else if(editType.equals("edit")) {

        }



    }

    /**
     * 按键监听，此处即toolbar上按键
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(editType.equals("new")){
                    showDialogNew();
                }
                else if(editType.equals("edit")) {
                    if(contentEdit.getText().toString().equals(note.getContent())!=true || titleEdit.getText().toString().equals(note.getTitle())!=true){
                        showDialogEdit();
                    }
                    else {
                        finish();
                    }
                }
        }
        return true;
    }

    /**
     * 返回动作监听
     */
    @Override
    public void onBackPressed() {
        // super.onBackPressed();//注释掉这行,back键不退出activity
        if(editType.equals("new")){
            showDialogNew();
        }
        else if(editType.equals("edit")) {
            if(contentEdit.getText().toString().equals(note.getContent())!=true || titleEdit.getText().toString().equals(note.getTitle())!=true){
                showDialogEdit();
            }
            else {
                finish();
            }
        }
    }


    /**
     * 新建确认对话框
     */
    private void showDialogNew() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否新建这条笔记");
        builder.setMessage("是否新建这条笔记?");
        builder.setIcon(R.drawable.ic_baseline_save_24);
//        //点击对话框以外的区域是否让对话框消失
//        builder.setCancelable(true);
        //设置正面按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("对话框测试","点击了确定");
                noteItem n = new noteItem();
                n.setContent(contentEdit.getText().toString());
                n.setCreateDate(new Date());
                n.setUpdateDate(new Date());
                n.setTitle(titleEdit.getText().toString());
                n.setType("normal");
                n.setMeetingBombId(meeting.getBombId());
                n.setMeetingName(meeting.getName());
                n.setMeetingObjectId(meeting.getObjectId());

                dialog.dismiss();

                if(n.save()==true){
                    Toast.makeText(getContext(), "笔记保存成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK,intent);
                    finish();
                }
                else {
                    Toast.makeText(getContext(), "笔记保存失败", Toast.LENGTH_SHORT).show();
                }

            }
        });
        //设置反面按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("对话框测试","点击了取消");
                dialog.dismiss();
                finish();
            }
        });
//        //设置中立按钮
//        builder.setNeutralButton("编辑", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 编辑确认对话框
     */
    private void showDialogEdit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否编辑这条笔记");
        builder.setMessage("是否编辑这条笔记?");
        builder.setIcon(R.drawable.ic_baseline_save_24);
//        //点击对话框以外的区域是否让对话框消失
//        builder.setCancelable(true);
        //设置正面按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("对话框测试","点击了确定");
                noteItem n = new noteItem();
                n.setContent(contentEdit.getText().toString());
                n.setUpdateDate(new Date());
                n.setTitle(titleEdit.getText().toString());

                dialog.dismiss();

                if(n.update(note.getId())>0){
                    Toast.makeText(getContext(), "笔记保存成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK,intent);
                    finish();
                }
                else {
                    Toast.makeText(getContext(), "笔记保存失败", Toast.LENGTH_SHORT).show();
                }

            }
        });
        //设置反面按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("对话框测试","点击了取消");
                dialog.dismiss();
                finish();
            }
        });
//        //设置中立按钮
//        builder.setNeutralButton("编辑", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}