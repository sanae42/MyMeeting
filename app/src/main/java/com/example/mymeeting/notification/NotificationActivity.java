package com.example.mymeeting.notification;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.mymeeting.R;
import com.example.mymeeting.db.noteItem;
import com.example.mymeeting.db.notificationItem;
import com.example.mymeeting.note.AllNoteListAdapter;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

public class NotificationActivity extends AppCompatActivity {

    Toolbar toolbar;

    //recyclerview内容
    private List<notificationItem> notificationList = new ArrayList<>();

    //recyclerview适配器
    private NotificationListdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initView();

        getNotificationDateFromLitePal();
    }

    private void initView(){
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

        //        recyclerview设置
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        //   适配器设置，设置显示1列
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NotificationListdapter(notificationList);
        recyclerView.setAdapter(adapter);

    }

    private void getNotificationDateFromLitePal(){
        notificationList.clear(); //清空列表

        List<notificationItem> notifications = DataSupport.where("userObjectId = ?" , BmobUser.getCurrentUser().getObjectId()).find(notificationItem.class);
        for (notificationItem n:notifications){
            notificationList.add(n);
        }

        adapter.notifyDataSetChanged();
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