package com.example.mymeeting.note;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mymeeting.EditMeetingActivity;
import com.example.mymeeting.MeetingFragment;
import com.example.mymeeting.R;
import com.example.mymeeting.activityCollector.BaseActivity;
import com.example.mymeeting.allParticipants.AllParticipantsListAdapter;
import com.example.mymeeting.bomb._User;
import com.example.mymeeting.db.meetingItem;
import com.example.mymeeting.db.noteItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobUser;

import static org.litepal.LitePalApplication.getContext;

public class AllNoteActivity extends BaseActivity {

    final String TAG = "AllNoteActivity";

    //操作类型：all：展示全部会议笔记，不能新建  meeting：展示当前会议笔记，可以新建当前会议笔记
    private String editType;

    //传参可以用传递class
    meetingItem meeting;

    //recyclerview内容
    private List<noteItem> allNoteList = new ArrayList<>();
    //备份
    private List<noteItem> backupList = new ArrayList<>();

    //recyclerview适配器
    private AllNoteListAdapter adapter;

    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_note);

        //从跳转的活动得到传值
        Intent intent = getIntent();
        editType = intent.getStringExtra("type");
        if(editType.equals("all")){

        }
        else if(editType.equals("meeting")) {
            meeting = (meetingItem) intent.getSerializableExtra("meeting");
        }
        initiateView();

        getNoteDateFromLitePal();

    }

    //初始化布局
    private void initiateView(){
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

        //        recyclerview设置
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //   适配器设置，设置显示2列
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AllNoteListAdapter(allNoteList);
        recyclerView.setAdapter(adapter);

        //添加recyclerView项目监听器
        adapter.setOnClickListener(new AllNoteListAdapter.OnClickListener(){
            @Override
            public void onClick(View itemView, int position) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), EditNoteActivity.class);
                intent.putExtra("type","edit");
                intent.putExtra("note",allNoteList.get(position));
                //监听返回
                startActivityForResult(intent,1);
                Log.d("列表项点击", "onClick"+"位置"+position);
            }

            @Override
            public void onLongClick(View itemView, int position) {
                showDialog(allNoteList.get(position));
                Log.d("列表项点击", "onLongClick"+"位置"+position);
            }


        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), EditNoteActivity.class);
                intent.putExtra("type","new");
                intent.putExtra("meeting",meeting);
                //监听返回
                startActivityForResult(intent,1);
            }
        });
        //展示全部会议时不可新建会议
        if(editType.equals("all")){
            fab.setVisibility(View.GONE);
        }

        searchView = findViewById(R.id.search);
        //默认就是搜索框展开
        searchView.setIconified(true);
        //一直都是搜索框，搜索图标在输入框左侧（默认是内嵌的）
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            //文字输入完成，提交的回调
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            //输入文字发生改变
            @Override
            public boolean onQueryTextChange(String s) {
                search(s);
                return true;
            }
        });

    }

    private void getNoteDateFromLitePal(){
        allNoteList.clear(); //清空会议列表
        backupList.clear();
        if(editType.equals("all")){
            List<noteItem> notes = DataSupport.where("userObjectId = ?" ,BmobUser.getCurrentUser().getObjectId()).find(noteItem.class);
            for (noteItem n:notes){
                allNoteList.add(n);
                backupList.add(n);
            }
        }else if(editType.equals("meeting")){
            List<noteItem> notes = DataSupport.where("meetingObjectId = ? and userObjectId = ?" ,meeting.getObjectId(), BmobUser.getCurrentUser().getObjectId()).find(noteItem.class);
            for (noteItem n:notes){
                allNoteList.add(n);
                backupList.add(n);
            }
        }

        adapter.notifyDataSetChanged();
//        swipeRefresh.setRefreshing(false);
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

    /**
     * 接收活动返回结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                //监听到编辑活动返回的结果
                if(resultCode==RESULT_OK){
                    getNoteDateFromLitePal();
                }
                getNoteDateFromLitePal();
                break;
            default:
        }
    }
    /**
     * 编辑确认对话框
     */
    private void showDialog(noteItem note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("笔记操作");
        builder.setMessage("笔记操作");
        builder.setIcon(R.drawable.ic_baseline_edit_24);
//        //点击对话框以外的区域是否让对话框消失
//        builder.setCancelable(true);
        //设置正面按钮
        builder.setPositiveButton("编辑", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("对话框测试","点击了编辑");
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), EditNoteActivity.class);
                intent.putExtra("type","edit");
                intent.putExtra("note",note);
                //监听返回
                startActivityForResult(intent,1);
                dialog.dismiss();
            }
        });
        //设置反面按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("对话框测试","点击了取消");
                dialog.dismiss();
            }
        });
        //设置中立按钮
        builder.setNeutralButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("对话框测试","点击了删除");
                dialog.dismiss();
                if(note.delete()>0){
                    Toast.makeText(getContext(), "笔记删除成功", Toast.LENGTH_SHORT).show();
                    getNoteDateFromLitePal();
                }else {
                    Toast.makeText(getContext(), "笔记删除失败", Toast.LENGTH_SHORT).show();
                }

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }




    public void search(String s)
    {
        allNoteList.clear();
        for(noteItem n: backupList){
            if(n.getTitle().indexOf(s)!=-1 )
                allNoteList.add(n);
        }
        adapter.notifyDataSetChanged();
    }



}