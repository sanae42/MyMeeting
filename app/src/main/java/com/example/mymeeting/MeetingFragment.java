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

import com.example.mymeeting.bomb.Meeting;
import com.example.mymeeting.bomb._User;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observer;
import java.util.Random;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MeetingFragment extends Fragment {

    Integer id;

    public View view;

    final String TAG = "MeetingFragment";

    //recyclerview内容
    private List<meetingItem> meetingItemList = new ArrayList<>();
    //meetingItemList备份
    private List<meetingItem> backupList = new ArrayList<>();

    //recyclerview适配器
    private MeetingListAdapter adapter;

    //    下拉刷新
    private SwipeRefreshLayout swipeRefresh;

////    TODO：登录相关功能ViewModel暂未启用
//    //    ViewModel
//    private SharedViewModel model;
//    //    ViewModel的数据
//    private boolean log;  //    登录状态
//    private Integer needRefreshData;  //    是否刷新列表

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

//        //    TODO：登录相关功能ViewModel暂未启用
//        model = ViewModelProviders.of(this).get(SharedViewModel.class);
//        model.getLog().observe(getActivity(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean b) {
//                log = model.getLog().getValue();
//            }
//        });


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


//        //model初始化
//        model = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
//        //初始是未登录状态
//        log = false;
////        ((fragment1) sectionsPagerAdapter.getItem(0)).changeLogMode(log);
//        model.getLog().observe(getActivity(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean b) {
//                if(id == 1){
//                    changeLogMode(model.getLog().getValue());
//                }
////                Toast.makeText(getContext(),model.getLog().getValue().toString() , Toast.LENGTH_SHORT).show();
//            }
//        });
//        Button log_button = (Button)view.findViewById(R.id.logButton);
//        log_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(model.getLog().getValue()==true)model.logout();
//                else model.login();
//            }
//        });

        return view;
    }

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

    public void showSwipeRefresh(){
        swipeRefresh.setRefreshing(true);
    }

    public void cancelSwipeRefresh(){
        swipeRefresh.setRefreshing(false);
    }

    //    TODO：refresh刷新TextView
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