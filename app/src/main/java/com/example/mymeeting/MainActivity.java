package com.example.mymeeting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymeeting.about.AboutActivity;
import com.example.mymeeting.activityCollector.BaseActivity;
import com.example.mymeeting.bomb.Meeting;
import com.example.mymeeting.bomb._User;
import com.example.mymeeting.calendar.CalendarActivity;
import com.example.mymeeting.chat.ChatActivity;
import com.example.mymeeting.db.meetingItem;
import com.example.mymeeting.login.LoginActivity;
import com.example.mymeeting.note.AllNoteActivity;
import com.example.mymeeting.notification.NotificationActivity;
import com.example.mymeeting.pager.SectionsPagerAdapter;
import com.example.mymeeting.setting.SettingActivity;
import com.example.mymeeting.userEdit.UserEditActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static org.litepal.LitePalApplication.getContext;

public class MainActivity extends BaseActivity {

    final String TAG = "MainActivity";

    //    侧边栏
    private DrawerLayout mDrawerLayout;

    //    搜索栏
    SearchView searchView;

    // 环信登录进度条弹出框
    private ProgressDialog mDialog;

    //    ViewPager及其适配器
    SectionsPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;

    View headview;
    //  侧边栏头部布局
    RelativeLayout loggedLayout;
    RelativeLayout unloggedLayout;

    //sp数据库 存放应用设置状态
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    //广播
    IntentFilter intentFilter;
    Receiver receiver;

    String appkey = "de0d0d10141439f301fc9d139da66920";

    //TODO: ****_User表中有attendingMeeting的版本
    private List<Meeting> attendingMeetingList = new ArrayList<>();


    @Override
    protected void onResume() {
        super.onResume();
        //广播测试成功
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.mymeeting.REFRESH_DATA");
        receiver = new Receiver();
        registerReceiver(receiver, intentFilter);

        //TODO:获取服务器数据写在onResume里
        //从服务器获取数据，_User表中有attendingMeeting的版本
        getAttendingMeetingFromBomb();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(receiver != null){
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //    初始化控件
        initiateView();
        Connector.getDatabase(); //执行任何一次数据库操作，初始化数据库

        //TODO:按书上在onPause和onResume写了绑定/解除绑定广播接收器，但在onResume写的好像不可以，必须在onCreate里再写一遍（如下
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.mymeeting.REFRESH_DATA");
        receiver = new Receiver();
        registerReceiver(receiver, intentFilter);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        //如果允许后台通知则开始应用后开启通知服务
        if(pref.getBoolean("backgroundNotification", true) == true){
            Intent startIntent = new Intent(this, MyService.class);
            startService(startIntent);
        }

    }

    /**
     * 自定义的广播接收器class
     */
    class Receiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,0)).showSwipeRefresh();
            ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,1)).showSwipeRefresh();
            getAttendingMeetingFromBomb();

            refreshNav();
//            Toast.makeText(getContext(), "测试接收广播成功", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 根据登录状态刷新侧边栏样式
     */
    private void refreshNav(){
        if(BmobUser.isLogin()==true){
            loggedLayout.setVisibility(View.VISIBLE);
            unloggedLayout.setVisibility(View.GONE);
            TextView nick = (TextView)headview.findViewById(R.id.nick);
            TextView username = (TextView)headview.findViewById(R.id.username);
            username.setText(BmobUser.getCurrentUser(_User.class).getUsername());
            nick.setText(BmobUser.getCurrentUser(_User.class).getNick());
        }else {
            loggedLayout.setVisibility(View.GONE);
            unloggedLayout.setVisibility(View.VISIBLE);
        }
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
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            //侧边栏按键点击
            public boolean onNavigationItemSelected(MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_chat:
                        if(BmobUser.isLogin()==true){
                            if (EMClient.getInstance().isLoggedInBefore()) {
                                // 如果已经登录跳转界面
                                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                                startActivity(intent);
                            }else {
                                // 如果未登录进行登录
                                easeLogin();
                            }
                        }else {
                            Toast.makeText(getContext(), "登录后才能使用该功能", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.nav_calendar:
                        Intent intent_calendar = new Intent();
                        intent_calendar.setClass(getApplicationContext(), CalendarActivity.class);
                        startActivity(intent_calendar);
                        break;
                    case R.id.nav_note:
                        if(BmobUser.isLogin()==true){
                            Intent intent_note = new Intent();
                            intent_note.setClass(getApplicationContext(), AllNoteActivity.class);
                            intent_note.putExtra("type","all");
                            startActivity(intent_note);
                        }else {
                            Toast.makeText(getContext(), "登录后才能使用该功能", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.nav_user:
                        if(BmobUser.isLogin()==true){
                            Intent intent_user = new Intent();
                            intent_user.setClass(getApplicationContext(), UserEditActivity.class);
                            startActivity(intent_user);
                        }else {
                            Toast.makeText(getContext(), "登录后才能使用该功能", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.nav_setting:
                        Intent intent_setting = new Intent();
                        intent_setting.setClass(getApplicationContext(), SettingActivity.class);
                        startActivity(intent_setting);
                        break;
                    case R.id.nav_more:
                        Intent intent_more = new Intent();
                        intent_more.setClass(getApplicationContext(), AboutActivity.class);
                        startActivity(intent_more);
                        break;
                    default:
                        break;
                }

        //     点击NavigationView中选项关闭侧边栏
//                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        //  根据用户是否登录改变侧边栏headerLayout样式
        Bmob.initialize(getContext(),appkey);
        headview=navView.inflateHeaderView(R.layout.nav_header);
        loggedLayout = (RelativeLayout)headview.findViewById(R.id.loggedLayout);
        unloggedLayout = (RelativeLayout)headview.findViewById(R.id.unloggedLayout);
        refreshNav();
        //注销按钮
        Button logoutButton = (Button) headview.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //bomb退出
                BmobUser.logOut();
                //环信退出（同步/异步）
                EMClient.getInstance().logout(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EMClient.getInstance().logout(true, new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "环信异步退出成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onProgress(int progress, String status) {

                            }

                            @Override
                            public void onError(int code, String message) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), "环信异步退出成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });

                    }
                }).start();

                //重新加载列表
                ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,0)).showSwipeRefresh();
                ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,1)).showSwipeRefresh();
                getAttendingMeetingFromBomb();

                refreshNav();
            }
        });
        //登录按钮
        Button loginButton = (Button) headview.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), LoginActivity.class);
                //跳转loginActivity，监听收到结果时决定是否刷新布局为登录状态
                startActivityForResult(intent,1);
            }
        });



        //        悬浮按钮
        FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BmobUser.isLogin()==true){
                    Snackbar.make(v, "要新建会议吗（测试会议）", Snackbar.LENGTH_SHORT)
                            .setAction("是的", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setClass(getApplicationContext(), EditMeetingActivity.class);
                                    //设置活动跳转传值为：新建活动
                                    intent.putExtra("type","new");
                                    startActivityForResult(intent,2);
                                    //重要，如果未登录就尝试获取BmobUser.getCurrentUser会闪退，所以要先判断是否登录
                                }
                            })
                            .show();
                }else {
                    Toast.makeText(getContext(), "登录后才能新建会议", Toast.LENGTH_SHORT).show();
                }

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

    /**
     * 环信登录
     */
    private void easeLogin(){
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("正在登陆，请稍后...");
        mDialog.show();

        String username = BmobUser.getCurrentUser().getUsername();
        String password = BmobUser.getCurrentUser().getObjectId();

        EMClient.getInstance().login(username, "1", new EMCallBack() {
            /**
             * 登陆成功的回调
             */
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDialog.dismiss();

//                        // 加载所有会话到内存
//                        EMClient.getInstance().chatManager().loadAllConversations();
//                        // 加载所有群组到内存，如果使用了群组的话
//                        EMClient.getInstance().groupManager().loadAllGroups();

                        // 登录成功跳转界面
                        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                        startActivity(intent);
                        mDialog.dismiss();
                    }
                });
            }

            /**
             * 登陆错误的回调
             * @param i
             * @param s
             */
            @Override
            public void onError(final int i, final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDialog.dismiss();
                        /**
                         * 关于错误码可以参考官方api详细说明
                         * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1_e_m_error.html
                         */
                        switch (i) {
                            // 网络异常 2
                            case EMError.NETWORK_ERROR:
                                Toast.makeText(getContext(), "网络错误 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 无效的用户名 101
                            case EMError.INVALID_USER_NAME:
                                Toast.makeText(getContext(), "无效的用户名 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 无效的密码 102
                            case EMError.INVALID_PASSWORD:
                                Toast.makeText(getContext(), "无效的密码 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 用户认证失败，用户名或密码错误 202
                            case EMError.USER_AUTHENTICATION_FAILED:
                                Toast.makeText(getContext(), "用户认证失败，用户名或密码错误 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 用户不存在 204
                            case EMError.USER_NOT_FOUND:
                                Toast.makeText(getContext(), "用户不存在 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 无法访问到服务器 300
                            case EMError.SERVER_NOT_REACHABLE:
                                Toast.makeText(getContext(), "无法访问到服务器 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 等待服务器响应超时 301
                            case EMError.SERVER_TIMEOUT:
                                Toast.makeText(getContext(), "等待服务器响应超时 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 服务器繁忙 302
                            case EMError.SERVER_BUSY:
                                Toast.makeText(getContext(), "服务器繁忙 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 未知 Server 异常 303 一般断网会出现这个错误
                            case EMError.SERVER_UNKNOWN_ERROR:
                                Toast.makeText(getContext(), "未知的服务器异常 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Toast.makeText(getContext(), "ml_sign_in_failed code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }


    /**
     * （_User表中有attendingMeeting的获取会议数据函数）从服务器获取当前用户的attendingMeetingList，再调用getDataFromBombVersion2得到会议信息
     */
    public void getAttendingMeetingFromBomb(){
        if(BmobUser.isLogin()==false){
            //重要，如果未登录时直接获取全部会议，无需获取当前用户参会list
            getDataFromBombVersion2();
        }

        //展示下拉刷新条 会有“on a null object reference”问题
        //因为在应用启动第一次刚开始加载数据时，fragment可能还没加载完成，所以在onActivityResult里添加后面两行
//        ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,0)).showSwipeRefresh();
//        ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,1)).showSwipeRefresh();

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

    /**
     * （_User表中有attendingMeeting的获取会议数据函数，被其他函数调用）从服务器获取会议信息，由getAttendingMeetingFromBomb调用
     */
    public void getDataFromBombVersion2(){

        new Thread(new Runnable() {
            @Override
            public void run() {
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
                                //设置会议图片
//                                m.setImageId(R.drawable.flower);
                                switch (m.getType()){
                                    case "兴趣社团会议":
                                        m.setImageId(R.mipmap.meeting_background4);
                                        break;
                                    case "学生职能社团会议":
                                        m.setImageId(R.mipmap.meeting_background3);
                                        break;
                                    case "学术研讨会议":
                                        m.setImageId(R.mipmap.meeting_background2);
                                        break;
                                    default:
                                        m.setImageId(R.mipmap.meeting_background1);
                                        break;
                                }
                                m.setOrganizer(meeting.getOrganizer());
                                m.setGroupId(meeting.getGroupId());

                                //TODO：设置是否申请和是否参会
                                //未登录时默认都为false
                                m.setIfOriginator(false);
                                m.setIfParticipant(false);
                                //登录时方才执行：
                                if(BmobUser.isLogin()==true){
                                    if(meeting.getOriginator()!=null)
                                        if(meeting.getOriginator().getObjectId().equals(BmobUser.getCurrentUser(_User.class).getObjectId())){
                                            m.setIfOriginator(true);
                                            Log.d(TAG, "找到申请者：");
                                        }
                                    for(Meeting M:attendingMeetingList){
                                        if(M.getObjectId().equals(meeting.getObjectId())){
                                            m.setIfParticipant(true);
                                            Log.d(TAG, "找到参加者：");
                                        }
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

    /**
     * （_User表中有attendingMeeting的获取会议数据函数，不再采用）从服务器获取会议信息，
     */
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
            case R.id.notification:
                if(BmobUser.isLogin()==true){
                    Intent intent_notification = new Intent();
                    intent_notification.setClass(getApplicationContext(), NotificationActivity.class);
                    startActivity(intent_notification);
                }else {
                    Toast.makeText(getContext(), "登录后才能使用通知", Toast.LENGTH_SHORT).show();
                }
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
                    //TODO : 登录活动已采用广播形式提醒主活动刷新，不再采用间接跳转监听；其他地方也可以使用广播
                    //登录成功，刷新侧边栏头部，并且刷新两个fragment获取数据
                    Boolean if_login = data.getBooleanExtra("login", false);
                    refreshNav();
                    //TODO: ****_User表中有attendingMeeting的版本
                    ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,0)).showSwipeRefresh();
                    ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,1)).showSwipeRefresh();
                    getAttendingMeetingFromBomb();
//                    ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,0)).getDataFromLitePal();
//                    ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,1)).getDataFromLitePal();
                }
                break;
            case 2:
                if(resultCode==RESULT_OK){
                    //新增会议成功，刷新侧边栏头部，并且刷新两个fragment获取数据
                    //TODO: ****_User表中有attendingMeeting的版本
                    ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,0)).showSwipeRefresh();
                    ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,1)).showSwipeRefresh();
                    getAttendingMeetingFromBomb();
//                    ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,0)).getDataFromLitePal();
//                    ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,1)).getDataFromLitePal();
                }
                break;
            //case 3 的startActivityForResult在MeetingListAdapter里
            case 3:
                if(resultCode==RESULT_OK){
                    //加入/退出会议成功，因为在adapter里强制转换mContext为MainActivity，使用startActivityForResult，可以不用手动刷新了
                    //MeetingActivity里删除会议后的返回刷新，和编辑会议活动编辑会议成功后返回MeetingActivity再返回主活动的监听刷新都是这里
                    //TODO: 自定义广播实验成功，广播可以替代此处部分功能（当前局部采用广播）
                    //TODO : EditMeetingActivity已采用广播形式提醒主活动刷新，不再采用间接跳转监听；其他地方也可以使用广播
                    ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,0)).showSwipeRefresh();
                    ((MeetingFragment) sectionsPagerAdapter.instantiateItem(viewPager,1)).showSwipeRefresh();
                    getAttendingMeetingFromBomb();
                }
                break;

        default:
        }
    }
}
