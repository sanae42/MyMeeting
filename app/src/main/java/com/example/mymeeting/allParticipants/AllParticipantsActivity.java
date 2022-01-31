package com.example.mymeeting.allParticipants;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.mymeeting.MainActivity;
import com.example.mymeeting.MeetingListAdapter;
import com.example.mymeeting.R;
import com.example.mymeeting.bomb.Meeting;
import com.example.mymeeting.bomb._User;
import com.example.mymeeting.meetingItem;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static org.litepal.LitePalApplication.getContext;

public class AllParticipantsActivity extends AppCompatActivity {

    final String TAG = "AppCompatActivity";

    //传参可以用传递class
    meetingItem meeting;

    //recyclerview内容
    private List<_User> allParticipantsList = new ArrayList<>();
    //meetingItemList备份
    private List<_User> backupList = new ArrayList<>();

    //recyclerview适配器
    private AllParticipantsListAdapter adapter;

    //    下拉刷新
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_participants);

        //从跳转的活动得到传值
        Intent intent = getIntent();
        meeting = (meetingItem)intent.getSerializableExtra("meeting");

        //        recyclerview设置
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        适配器设置，设置显示1列
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AllParticipantsListAdapter(allParticipantsList);
        recyclerView.setAdapter(adapter);

//        下拉刷新
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.purple_500);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllParticipantsFromBomb();
            }
        });

        getAllParticipantsFromBomb();



    }


    public void getAllParticipantsFromBomb(){
        swipeRefresh.setRefreshing(true);
        Bmob.initialize(getContext(),"de0d0d10141439f301fc9d139da66920");
        BmobQuery<_User> query = new BmobQuery<_User>();
        Meeting M = new Meeting();
        M.setObjectId(meeting.getObjectId());
        query.addWhereRelatedTo("participant", new BmobPointer(M));
        query.findObjects(new FindListener<_User>() {
            @Override
            public void done(List<_User> list, BmobException e) {
                if(e==null) {
                    Log.d(TAG, "获取全部参会者数据成功，list长度：" + list.size());
                    for (_User u:list){
                        allParticipantsList.add(u);
                        backupList.add(u);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            swipeRefresh.setRefreshing(false);
                        }
                    });
                }else {
                    Log.d(TAG, "获取全部参会者数据失败：" + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefresh.setRefreshing(false);
                        }
                    });
                }

            }
        });
    }
}