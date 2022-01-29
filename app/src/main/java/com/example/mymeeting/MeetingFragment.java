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
//                refreshFruits();
                getDataFromBomb();
            }
        });

        //    TODO：测试从服务器获取数据 *****  为什么开启应用后的第一次刷新不显示正常？
//        swipeRefresh.setRefreshing(true);
        getDataFromBomb();
//        adapter.notifyDataSetChanged();


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


    public void getDataFromBomb(){
        if(BmobUser.isLogin()==false){
            //TODO:重要，如果未登录就尝试获取BmobUser.getCurrentUser会闪退，所以要先判断是否登录
            return;
        }
        swipeRefresh.setRefreshing(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String userObjectId = BmobUser.getCurrentUser(_User.class).getObjectId();
                Bmob.initialize(getContext(),"de0d0d10141439f301fc9d139da66920");
                BmobQuery<Meeting> bmobQuery = new BmobQuery<>();
                bmobQuery.findObjects(new FindListener<Meeting>() {
                    @Override
                    public void done(List<Meeting> list, BmobException e) {
                        if(e==null){
                            Log.d(TAG, "获取服务器数据成功，list长度："+list.size());
                            meetingItemList.clear(); //清空会议列表

                            final Integer[] count = {0};
                            Integer sum = list.size();
                            //循环
                            for(Meeting meeting : list){
                                meetingItem m= new meetingItem();
                                m.setObjectId(meeting.getObjectId());
                                m.setName(meeting.getName());
                                m.setType(meeting.getType());
                                m.setTypeNumber(meeting.getTypeNumber());
                                Date date1 = new Date();
                                SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                try {
                                    if(meeting.getRegistrationDate()!=null)
                                        date1=format.parse(meeting.getRegistrationDate().getDate());
                                } catch (ParseException parseException) {
                                    parseException.printStackTrace();
                                }
                                m.setRegistrationDate(date1);
                                Date date2 = new Date();
                                try {
                                    if(meeting.getHostDate()!=null)
                                        date2=format.parse(meeting.getHostDate().getDate());
                                } catch (ParseException parseException) {
                                    parseException.printStackTrace();
                                }
                                m.setHostDate(date2);
                                m.setLength(meeting.getLength());
                                m.setLocation(meeting.getLocation());
                                m.setLocationNumber(meeting.getLocationNumber());
                                m.setState(meeting.getState());
                                m.setLocationNumber(meeting.getStateNumber());
                                m.setIntroduction(meeting.getIntroduction());
                                m.setComtent(meeting.getComtent());
                                m.setImageId(R.drawable.pear); //
                                m.setOrganizer(meeting.getOrganizer());


                                //TODO：设置是否申请和是否参会
                                m.setIfOriginator(false);
                                m.setIfParticipant(false);

                                if(meeting.getOriginator()!=null)
                                    if(meeting.getOriginator().getObjectId().equals(userObjectId)){
                                        m.setIfOriginator(true);
                                        Log.d(TAG, "找到申请者：");
                                    }


                                BmobQuery<_User> query_p = new BmobQuery<_User>();
                                query_p.addWhereRelatedTo("participant", new BmobPointer(meeting));
                                query_p.findObjects(new FindListener<_User>() {
                                    @Override
                                    public void done(List<_User> list, BmobException e) {
                                        for (_User user:list){
                                            if (userObjectId.equals(user.getObjectId()))
                                            {
                                                m.setIfParticipant(true);
                                                Log.d(TAG, "找到参会者："+ m.getIfParticipant() + meeting.getId());
                                                break;
                                            }
                                        }
                                        count[0] ++;
                                        //第一个页面和第二个页面的两种情况
                                        if(id==1){
                                            meetingItemList.add(m);
                                            Log.d(TAG, "找到   ："+ m.getIfParticipant());
                                        }
                                        //第一个页面和第二个页面的两种情况
                                        if(id==2){
                                            if(m.getIfParticipant()==true){
                                                meetingItemList.add(m);
                                            }
                                        }
                                        if(count[0] == sum){
                                            //TODO:返回主线程位置3 终于对了
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    adapter.notifyDataSetChanged();
                                                    backupList.clear();
                                                    for(meetingItem m: meetingItemList){
                                                        backupList.add(m);
                                                    }
                                                    swipeRefresh.setRefreshing(false);
                                                }
                                            });
                                        }
                                    }
                                });


                            }
                            //TODO:返回主线程位置2
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    adapter.notifyDataSetChanged();
//                                    swipeRefresh.setRefreshing(false);
//                                }
//                            });
                        }else{
                            Log.d(TAG, "获取服务器数据失败：" + e.getMessage());
                        }
                    }
                });
                //TODO:返回主线程位置1
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        adapter.notifyDataSetChanged();
//                        swipeRefresh.setRefreshing(false);
////                        getDataFromBomb2();
//                    }
//                });
            }
        }).start();
    }


    //    TODO：refresh刷新TextView
    public void refresh(String s)
    {
//        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
        meetingItemList.clear();
        for(meetingItem m: backupList){
            if(m.getName().indexOf(s)!=-1)
                meetingItemList.add(m);
        }
        adapter.notifyDataSetChanged();


    }

}