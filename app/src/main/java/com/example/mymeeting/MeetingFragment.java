package com.example.mymeeting;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MeetingFragment extends Fragment {

    Integer id;

    public View view;

    private Meeting[] meetings = {new Meeting("Apple", R.drawable.apple), new Meeting("Banana", R.drawable.banana),
            new Meeting("Orange", R.drawable.orange), new Meeting("Watermelon", R.drawable.watermelon),
            new Meeting("Pear", R.drawable.pear), new Meeting("Grape", R.drawable.grape),
            new Meeting("Pineapple", R.drawable.pineapple), new Meeting("Strawberry", R.drawable.strawberry),
            new Meeting("Cherry", R.drawable.cherry), new Meeting("Mango", R.drawable.mango)};

    //recyclerview内容
    private List<Meeting> meetingList = new ArrayList<>();

    //recyclerview适配器
    private MeetingListAdapter adapter;

    //    下拉刷新
    private SwipeRefreshLayout swipeRefresh;

//    //    ViewModel
//    private SharedViewModel model;

    //    登录状态
    private boolean log;

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
//        Toast.makeText(getContext(), "创建了一个碎片", Toast.LENGTH_LONG).show();

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_meeting, container, false);

//        model = ViewModelProviders.of(this).get(SharedViewModel.class);
//        model.getLog().observe(getActivity(), new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean b) {
//                log = model.getLog().getValue();
//            }
//        });

        //初始化水果列表
        initFruits();

//        recyclerview设置
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
//        适配器设置，设置显示两列
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MeetingListAdapter(meetingList);
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
        meetingList.clear();
        for (int i = 0; i < 50; i++) {
            Random random = new Random();
            int index = random.nextInt(meetings.length);
            meetingList.add(meetings[index]);
        }
    }

    //刷新recyclerview
    public void refreshFruits() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initFruits();
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
        List<Meeting> newList = new ArrayList<>();
        for(Meeting f: meetingList){
            if(f.getName().equals("Pear")==true){
                newList.add(f);
//                Log.d(this.toString(), "有一个梨子");
            }
        }
        meetingList.clear();
        for(Meeting f: newList){
            meetingList.add(f);
        }
//        Toast.makeText(getContext(), "梨子"+newList.size(), Toast.LENGTH_SHORT).show();
//        Log.d(this.toString(), "梨子"+newList.size());
//        adapter.notifyDataSetChanged();
//        initFruits();
        adapter.notifyDataSetChanged();
        Log.d(this.toString(), "梨子"+newList.size()+" "+ meetingList.size());
        Toast.makeText(getContext(), "梨子"+newList.size()+" "+ meetingList.size(), Toast.LENGTH_SHORT).show();
//        view.findViewById(R.id.swipe_refresh).setVisibility(View.INVISIBLE);
    }

    public void changeLogMode(Boolean bool){
        log = bool;
        RelativeLayout relativeLayout = (RelativeLayout)view.findViewById(R.id.relativeLayout);
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);
        if (bool == false){
            relativeLayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
        }
        else {
            relativeLayout.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

}