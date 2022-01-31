package com.example.mymeeting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.mymeeting.bomb.Meeting;
import com.example.mymeeting.bomb._User;
import com.example.mymeeting.bomb.doBomb;
import com.example.mymeeting.pager.SectionsPagerAdapter;
import com.example.mymeeting.sp.UserStatus;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.litepal.crud.DataSupport;
import org.litepal.exceptions.DataSupportException;
import org.litepal.tablemanager.Connector;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import de.hdodenhof.circleimageview.CircleImageView;

import static org.litepal.LitePalApplication.getContext;

public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivity";

    //    侧边栏
    private DrawerLayout mDrawerLayout;

    //    搜索栏
    SearchView searchView;

    //    ViewPager及其适配器
    SectionsPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;

    //  侧边栏头部布局
    RelativeLayout loggedLayout;
    RelativeLayout unloggedLayout;

    String appkey = "de0d0d10141439f301fc9d139da66920";

    //TODO: ****_User表中有attendingMeeting的版本
    private List<Meeting> attendingMeetingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //    初始化控件
        initiateView();
        Connector.getDatabase(); //执行任何一次数据库操作，初始化数据库

        //TODO: 测试阶段默认自动登录
        //TODO: BombUser自带自动登录功能
//        UserStatus userStatus = new UserStatus(getContext());
//        if(true){
//            userStatus.login(1002,"18301038","111111",true);
//        }

        //TODO: ****_User表中有attendingMeeting的版本
        getAttendingMeetingFromBomb();

        Log.d(TAG, "pear："+R.drawable.pear);

    }

    /**
     * 初始化控件
     */
    private void initiateView(){
        //        导航条
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //    侧边栏布局
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //   设置actionbar（即toolbar）最左侧按钮功能，点击唤出侧边栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        //  设置NavigationView布局
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        //   将NavigationView中的call作为默认选项选中
        navView.setCheckedItem(R.id.nav_call);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            //侧边栏按键点击
            public boolean onNavigationItemSelected(MenuItem item) {
        //     点击NavigationView中选项关闭侧边栏
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        //  根据用户是否登录改变侧边栏headerLayout样式
        //TODO:登录相关
        Bmob.initialize(getContext(),appkey);
        View headview=navView.inflateHeaderView(R.layout.nav_header);
        loggedLayout = (RelativeLayout)headview.findViewById(R.id.loggedLayout);
        unloggedLayout = (RelativeLayout)headview.findViewById(R.id.unloggedLayout);
        Button logoutButton = (Button) headview.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobUser.logOut();
                loggedLayout.setVisibility(View.GONE);
                unloggedLayout.setVisibility(View.VISIBLE);
            }
        });
        Button loginButton = (Button) headview.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), LoginActivity.class);
//                Bundle bundle=new Bundle();
//                bundle.putString("type", "new");
//                intent.putExtras(bundle);
                //TODO:收到结果时决定是否刷新布局为登录状态
                startActivityForResult(intent,1);
            }
        });
        if (BmobUser.isLogin()) {
            loggedLayout.setVisibility(View.VISIBLE);
            unloggedLayout.setVisibility(View.GONE);
//            User user = BmobUser.getCurrentUser(User.class);
//            Snackbar.make(view, "已经登录：" + user.getUsername(), Snackbar.LENGTH_LONG).show();
        } else {
            loggedLayout.setVisibility(View.GONE);
            unloggedLayout.setVisibility(View.VISIBLE);
        }


        //        悬浮按钮
        FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "要新建会议吗（测试会议）", Snackbar.LENGTH_SHORT)
                        .setAction("是的", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setClass(getApplicationContext(), EditMeetingActivity.class);
                                //设置活动跳转传值为：新建活动
                                intent.putExtra("type","new");
                                startActivityForResult(intent,2);
//                            //TODO:重要，如果未登录就尝试获取BmobUser.getCurrentUser会闪退，所以要先判断是否登录
                            }
                        })
                        .show();
            }
        });

        //SectionsPagerAdapter设置
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        //ViewPager设置（中间空白部分）
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        //tab(上方切换条)
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    public void getAttendingMeetingFromBomb(){
        if(BmobUser.isLogin()==false){
            //TODO:重要，如果未登录就尝试获取BmobUser.getCurrentUser会闪退，所以要先判断是否登录
            return;
        }
        BmobQuery<Meeting> query = new BmobQuery<Meeting>();
        query.addWhereRelatedTo("attendingMeeting", new BmobPointer(BmobUser.getCurrentUser()));
        query.findObjects(new FindListener<Meeting>() {
            @Override
            public void done(List<Meeting> list, BmobException e) {
                if(e==null){
                    attendingMeetingList.clear();
                    for(Meeting M:list){
                        attendingMeetingList.add(M);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getDataFromBombVersion2();
                        }
                    });
                }
            }
        });
    }

    //TODO: ****_User表中有attendingMeeting的版本
    public void getDataFromBombVersion2(){
        if(BmobUser.isLogin()==false){
            //TODO:重要，如果未登录就尝试获取BmobUser.getCurrentUser会闪退，所以要先判断是否登录
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String userObjectId = BmobUser.getCurrentUser(_User.class).getObjectId();
                Bmob.initialize(getContext(),"de0d0d10141439f301fc9d139da66920");
                BmobQuery<Meeting> bmobQuery = new BmobQuery<>();
                //增加了对会议状态的搜索条件
                bmobQuery.addWhereEqualTo("state", "normal");
                bmobQuery.findObjects(new FindListener<Meeting>() {
                    @Override
                    public void done(List<Meeting> list, BmobException e) {
                        if(e==null){
                            Log.d(TAG, "获取服务器数据成功，list长度："+list.size());
                            //清空本地数据库会议表
                            DataSupport.deleteAll(meetingItem.class);

                            final Integer[] count = {0};
                            Integer sum = list.size();
                            //循环
                            for(Meeting meeting : list){
                                meetingItem m= new meetingItem();
                                m.setObjectId(meeting.getObjectId());
                                m.setBombId(meeting.getId().intValue());
                                m.setName(meeting.getName());
                                m.setType(meeting.getType());
                                m.setTypeNumber(0);
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
                                m.setLocationNumber(0);
                                m.setState(meeting.getState());
                                m.setStateNumber(0);
                                m.setIntroduction(meeting.getIntroduction());
                                m.setComtent(meeting.getComtent());
                                m.setImageId(R.drawable.flower); //
                                m.setOrganizer(meeting.getOrganizer());


                                //TODO：设置是否申请和是否参会
                                m.setIfOriginator(false);
                                m.setIfParticipant(false);

                                if(meeting.getOriginator()!=null)
                                    if(meeting.getOriginator().getObjectId().equals(userObjectId)){
                                        m.setIfOriginator(true);
                                        Log.d(TAG, "找到申请者：");
                                    }
                                for(Meeting M:attendingMeetingList){
                                    if(M.getObjectId().equals(meeting.getObjectId())){
                                        m.setIfParticipant(true);
                                        Log.d(TAG, "找到参加者：");
                                    }
                                }
                                m.save();

                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,0)).getDataFromLitePal();
                                    ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,1)).getDataFromLitePal();
                                }
                            });
                        }else{
                            Log.d(TAG, "获取服务器数据失败：" + e.getMessage());
                        }
                    }
                });
                //TODO:返回主线程位置1
            }
        }).start();
    }


    public void getDataFromBombVersion1(){
        if(BmobUser.isLogin()==false){
            //TODO:重要，如果未登录就尝试获取BmobUser.getCurrentUser会闪退，所以要先判断是否登录
            return;
        }
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
                            //清空本地数据库会议表
                            DataSupport.deleteAll(meetingItem.class);

                            final Integer[] count = {0};
                            Integer sum = list.size();
                            //循环
                            for(Meeting meeting : list){
                                meetingItem m= new meetingItem();
                                m.setObjectId(meeting.getObjectId());
                                m.setBombId(meeting.getId().intValue());
                                m.setName(meeting.getName());
                                m.setType(meeting.getType());
                                m.setTypeNumber(0);
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
                                m.setLocationNumber(0);
                                m.setState(meeting.getState());
                                m.setStateNumber(0);
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
                                                break;
                                            }
                                        }
                                        count[0] ++;
                                        //TODO:在这里把m加入本地数据库
                                        m.save();

                                        Log.d(TAG, "pear："+m.getBombId());
                                        if(count[0] == sum){
                                            //TODO:返回主线程位置3 终于对了
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,0)).getDataFromLitePal();
                                                    ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,1)).getDataFromLitePal();
                                                    List<meetingItem> meetings = DataSupport.findAll(meetingItem.class);
//                                                    Log.d(TAG, "pear：List<meetingItem>的长度为："+meetings.size());
//                                                    if(meetings.size()!=0){
//                                                        Log.d(TAG, "pear："+meetings.get(1).getImageId());
//                                                        Log.d(TAG, "pear："+meetings.get(1).getIfParticipant());
//                                                        Log.d(TAG, "pear："+meetings.get(1).getIfOriginator());
//                                                        Log.d(TAG, "pear："+meetings.get(1).getObjectId());
//                                                        Log.d(TAG, "pear："+meetings.get(1).getBombId());
//                                                        Log.d(TAG, "pear："+meetings.get(1).getName());
//                                                        Log.d(TAG, "pear："+meetings.get(1).getIntroduction());
//                                                        Log.d(TAG, "pear："+meetings.get(1).getComtent());
//                                                        Log.d(TAG, "pear："+meetings.get(1).getLength());
//                                                        Log.d(TAG, "pear："+meetings.get(1).getHostDate());
//                                                        Log.d(TAG, "pear："+meetings.get(1).getType());
//                                                        Log.d(TAG, "pear："+meetings.get(1).getLocation());
//                                                    }

                                                }
                                            });
                                        }
                                    }
                                });
                            }
                            //TODO:返回主线程位置2
                        }else{
                            Log.d(TAG, "获取服务器数据失败：" + e.getMessage());
                        }
                    }
                });
                //TODO:返回主线程位置1
            }
        }).start();
    }



    /**
     * 菜单按键监听，此处菜单即toolbar上一系列按键
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //开启侧边栏
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            //导航条按键
//            case R.id.backup:
//                Toast.makeText(this, "You clicked Backup", Toast.LENGTH_SHORT).show();
//                break;
            case R.id.delete:
                Toast.makeText(this, "You clicked Delete", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(this, "You clicked Settings", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return true;
    }

    /**
     * 菜单创建 及搜索框相关
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);

        searchView = (SearchView) findViewById(R.id.search);
        //默认就是搜索框展开
        searchView.setIconified(true);
        //一直都是搜索框，搜索图标在输入框左侧（默认是内嵌的）
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //文字输入完成，提交的回调
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            //输入文字发生改变
            @Override
            public boolean onQueryTextChange(String s) {
                //   获得fragment实例并调用refresh
                ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,viewPager.getCurrentItem())).refresh(s);
                return true;
            }
        });
        //关闭搜索框时的回调
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });
        //点击搜索图标，搜索框展开时的回调
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

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
                if(resultCode==RESULT_OK){
                    //登录成功，刷新侧边栏头部，并且刷新两个fragment获取数据
                    Boolean if_login = data.getBooleanExtra("login", false);
                    if(if_login==true){
                        loggedLayout.setVisibility(View.VISIBLE);
                        unloggedLayout.setVisibility(View.GONE);
                    }
                    //TODO: ****_User表中有attendingMeeting的版本
                    getAttendingMeetingFromBomb();
//                    ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,0)).getDataFromLitePal();
//                    ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,1)).getDataFromLitePal();
                }
                break;
            case 2:
                if(resultCode==RESULT_OK){
                    //新增/编辑会议成功，刷新侧边栏头部，并且刷新两个fragment获取数据
                    //TODO: ****_User表中有attendingMeeting的版本
                    getAttendingMeetingFromBomb();
//                    ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,0)).getDataFromLitePal();
//                    ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,1)).getDataFromLitePal();
                }
                break;
            case 3:
                if(resultCode==RESULT_OK){
                    //加入/退出会议成功，因为在adapter里强制转换mContext为MainActivity，使用startActivityForResult，可以不用手动刷新了
                    getAttendingMeetingFromBomb();
                    Log.d(TAG, "测试返回3");
                }
                break;

        default:
        }
    }
}
