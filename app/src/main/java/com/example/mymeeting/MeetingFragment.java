package com.example.mymeeting;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mymeeting.bomb.Meeting;
import com.example.mymeeting.bomb.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MeetingFragment extends Fragment {

    Integer id;

    public View view;

    private meetingItem[] meetingItems = {new meetingItem("Apple", R.drawable.apple), new meetingItem("Banana", R.drawable.banana),
            new meetingItem("Orange", R.drawable.orange), new meetingItem("Watermelon", R.drawable.watermelon),
            new meetingItem("Pear", R.drawable.pear), new meetingItem("Grape", R.drawable.grape),
            new meetingItem("Pineapple", R.drawable.pineapple), new meetingItem("Strawberry", R.drawable.strawberry),
            new meetingItem("Cherry", R.drawable.cherry), new meetingItem("Mango", R.drawable.mango)};

    //recyclerview内容
    private List<meetingItem> meetingItemList = new ArrayList<>();

    //recyclerview适配器
    private MeetingListAdapter adapter;

    //    下拉刷新
    private SwipeRefreshLayout swipeRefresh;

//    TODO：登录相关功能ViewModel暂未启用
//    //    ViewModel
//    private SharedViewModel model;
//    //    登录状态
//    private boolean log;

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

        //    TODO：登录相关功能ViewModel暂未启用
//        model = ViewModelProviders.of(this).get(SharedViewModel.class);
//        model.getLog().observe(getActivity(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean b) {
//                log = model.getLog().getValue();
//            }
//        });

        //    TODO：测试从服务器获取数据
        //初始化水果列表
        initFruits();
        Bmob.initialize(getContext(),"de0d0d10141439f301fc9d139da66920");
        BmobQuery<Meeting> query = new BmobQuery<>();

        query.findObjects(new FindListener<Meeting>() {
            @Override
            public void done(List<Meeting> list, BmobException e) {
                if(list.size()==0){
                    Log.d("测试获取服务器数据", "没有数据");
                }else {
                    Log.d("测试获取服务器数据", "list大小: "+list.size());
                    for(Meeting meeting: list){
                        Log.d("测试获取服务器数据", meeting.getRegistrationDate().getDate());
//                        Log.d("测试获取服务器数据", meeting.getId()+"");
                    }
                }
            }

        });

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
                refreshFruits();
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



    //随机水果列表
    private void initFruits() {
        meetingItemList.clear();
        for (int i = 0; i < 50; i++) {
            Random random = new Random();
            int index = random.nextInt(meetingItems.length);
            meetingItemList.add(meetingItems[index]);
        }
    }

    //刷新recyclerview
    public void refreshFruits() {
        new Thread(new Runnable() {
            @Override
            public void run() {

//                    Thread.sleep(2000);
                Bmob.initialize(getContext(),"de0d0d10141439f301fc9d139da66920");
                BmobQuery<Meeting> query = new BmobQuery<Meeting>();
                //返回50条数据，如果不加上这条语句，默认返回10条数据
                query.setLimit(100);
                //执行查询方法
                query.findObjects(new FindListener<Meeting>() {
                    @Override
                    public void done(List<Meeting> list, BmobException e) {
                        if(list.size()==0){
                            Log.d("测试获取服务器数据", "没有数据");
//                            Toast.makeText(getContext(), "云端数据库没有会议数据", Toast.LENGTH_SHORT).show();
                        }else {
                            Log.d("测试获取服务器数据", "list大小: "+list.size());
                            meetingItemList.clear(); //清空会议列表
                            for(Meeting meeting : list){
                                meetingItem m= new meetingItem();
                                m.setImageId(R.drawable.pear);
//                                m.setName(meeting.getName());
                                meetingItemList.add(m);
                            }
                        }
                    }
                });
//                query.findObjects(new FindListener<Meeting>() {
//                    @Override
//                    public void done(List<Meeting> list, BmobException e) {
//                        if(list.size()==0){
//                            Log.d("测试获取服务器数据", "没有数据");
//                            Toast.makeText(getContext(), "云端数据库没有会议数据", Toast.LENGTH_SHORT).show();
//                        }else{
//                            Log.d("测试获取服务器数据", "list大小: "+list.size());
//                            meetingItemList.clear(); //清空会议列表
//                            for(Meeting meeting : list){
//                                meetingItem m= new meetingItem();
//                                m.setImageId(R.drawable.pear);
//                                m.setName(meeting.getName());
//                                meetingItemList.add(m);
//                            }
//
//                        }
//                    }
//                });
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }




    //    TODO：refresh刷新TextView
    public void refresh(String s)
    {
//        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
        List<meetingItem> newList = new ArrayList<>();
        for(meetingItem f: meetingItemList){
            if(f.getName().equals("Pear")==true){
                newList.add(f);
//                Log.d(this.toString(), "有一个梨子");
            }
        }
        meetingItemList.clear();
        for(meetingItem f: newList){
            meetingItemList.add(f);
        }

        adapter.notifyDataSetChanged();

    }

    //    TODO：登录相关功能ViewModel暂未启用
//    public void changeLogMode(Boolean bool){
//        log = bool;
//        RelativeLayout relativeLayout = (RelativeLayout)view.findViewById(R.id.relativeLayout);
//        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);
//        if (bool == false){
//            relativeLayout.setVisibility(View.VISIBLE);
//            swipeRefreshLayout.setVisibility(View.GONE);
//        }
//        else {
//            relativeLayout.setVisibility(View.GONE);
//            swipeRefreshLayout.setVisibility(View.VISIBLE);
//        }
//    }

}