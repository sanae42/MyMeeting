package com.example.mymeeting;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mymeeting.db.meetingItem;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class MeetingFragment extends Fragment {

    //id 1：全部会议fragment  id 2：我的会议fragment
    Integer id;

    public View view;

    final String TAG = "MeetingFragment";

    //recyclerview内容
    private List<meetingItem> meetingItemList = new ArrayList<>();
    //meetingItemList备份（搜索使用）
    private List<meetingItem> backupList = new ArrayList<>();

    //recyclerview适配器
    private MeetingListAdapter adapter;

    //    下拉刷新
    private SwipeRefreshLayout swipeRefresh;

    public MeetingFragment() {
        // Required empty public constructor
    }

    public MeetingFragment(Integer i) {
        id = i;
    }

    public static MeetingFragment newInstance() {
        MeetingFragment fragment = new MeetingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(this.toString(), "创建了一个碎片");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_meeting, container, false);

//        recyclerview设置
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
//        适配器设置，设置显示1列
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MeetingListAdapter(meetingItemList);
        recyclerView.setAdapter(adapter);

//        下拉刷新
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.purple_500);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //TODO: ****_User表中有attendingMeeting的版本
                MainActivity parentActivity = (MainActivity ) getActivity();
                parentActivity.getAttendingMeetingFromBomb();
            }
        });
        //创建完成fragment布局后先展示进度条
        showSwipeRefresh();

        return view;
    }

    /**
     * 从本地数据库获取会议数据
     */
    public void getDataFromLitePal(){
        meetingItemList.clear(); //清空会议列表
        backupList.clear();
        List<meetingItem> meetings = DataSupport.findAll(meetingItem.class);
        for (meetingItem m:meetings){
            if(id==1){
                meetingItemList.add(m);
                backupList.add(m);
            }
            else if(id==2){
                if(m.getIfParticipant()==true){
                    meetingItemList.add(m);
                    backupList.add(m);
                }
            }
        }

        adapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

    /**
     * 展示下拉加载条
     */
    public void showSwipeRefresh(){
        swipeRefresh.setRefreshing(true);
    }

    /**
     * 隐藏下拉加载条
     */
    public void cancelSwipeRefresh(){
        swipeRefresh.setRefreshing(false);
    }

    /**
     * 由MainActivity调用，根据搜索结果刷新搜索筛选后的list
     */
    public void refresh(String s)
    {
        meetingItemList.clear();
        for(meetingItem m: backupList){
            if(m.getName().indexOf(s)!=-1)
                meetingItemList.add(m);
        }
        adapter.notifyDataSetChanged();
    }
}