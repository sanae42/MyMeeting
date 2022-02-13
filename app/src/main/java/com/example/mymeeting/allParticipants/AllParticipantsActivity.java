package com.example.mymeeting.allParticipants;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.mymeeting.MainActivity;
import com.example.mymeeting.R;
import com.example.mymeeting.activityCollector.BaseActivity;
import com.example.mymeeting.bomb.Meeting;
import com.example.mymeeting.bomb._User;
import com.example.mymeeting.chat.ChatActivity;
import com.example.mymeeting.chat.ConversationActivity;
import com.example.mymeeting.db.meetingItem;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static org.litepal.LitePalApplication.getContext;

public class AllParticipantsActivity extends BaseActivity {

    final String TAG = "AppCompatActivity";

    //传参可以用传递class
    meetingItem meeting;

    //recyclerview内容
    private List<_User> allParticipantsList = new ArrayList<>();
    //meetingItemList备份
    private List<_User> backupList = new ArrayList<>();

    //recyclerview适配器
    private AllParticipantsListAdapter adapter;

    SearchView searchView;

    PopupWindow popupWindow;

    // 环信登录进度条弹出框
    private ProgressDialog mDialog;

    //    下拉刷新
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_participants);


        //从跳转的活动得到传值
        Intent intent = getIntent();
        meeting = (meetingItem)intent.getSerializableExtra("meeting");

        initiateView();

        getAllParticipantsFromBomb();

    }

    private void initiateView(){
        //        recyclerview设置
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        适配器设置，设置显示1列
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AllParticipantsListAdapter(allParticipantsList);
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(new AllParticipantsListAdapter.OnClickListener() {
            @Override
            public void onClick(View itemView, int position) {

            }

            @Override
            public void onLongClick(View itemView, int position) {
//                Toast.makeText(getContext(), "长按", Toast.LENGTH_SHORT).show();
                initPopupWindow(itemView, position);
            }
        });

//        下拉刷新
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.purple_500);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllParticipantsFromBomb();
            }
        });

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


    private void initPopupWindow(View view, int position) {
        if(popupWindow == null){
            View popupView = LayoutInflater.from(AllParticipantsActivity.this).inflate(R.layout.item_popup,null);
            Button button1 = popupView.findViewById(R.id.btn_1);
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //取消展示popupWindow
                    popupWindow.dismiss();

                    Toast.makeText(getContext(), "点按事件", Toast.LENGTH_SHORT).show();
                    if (EMClient.getInstance().isLoggedInBefore()) {
                        // 如果已经登录跳转界面
                        // 跳转到聊天界面，开始聊天
                        Intent intent = new Intent(AllParticipantsActivity.this, ConversationActivity.class);
                        // EaseUI封装的聊天界面需要这两个参数，聊天者的username，以及聊天类型，单聊还是群聊
                        intent.putExtra("conversationId", allParticipantsList.get(position).getUsername());
                        intent.putExtra("chatType", EMMessage.ChatType.Chat);
                        //优先漫游
                        intent.putExtra("isRoaming", true);
                        startActivity(intent);
                    }else {
                        // 如果未登录进行登录
                        easeLoginThenGoToConversation(allParticipantsList.get(position).getUsername());
                    }
                }
            });
            // 构造函数关联
            popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
        }
        // 展示popup
        popupWindow.showAsDropDown(view);
    }

    /**
     * 环信登录并跳转聊天界面
     */
    private void easeLoginThenGoToConversation(String userId){
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

                        // 登录成功跳转界面
                        // 跳转到聊天界面，开始聊天
                        Intent intent = new Intent(AllParticipantsActivity.this, ConversationActivity.class);
                        // EaseUI封装的聊天界面需要这两个参数，聊天者的username，以及聊天类型，单聊还是群聊
                        intent.putExtra("conversationId", userId);
                        intent.putExtra("chatType", EMMessage.ChatType.Chat);
                        //优先漫游
                        intent.putExtra("isRoaming", true);
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
                        Log.d("lzan13", "登录失败 Error code:" + i + ", message:" + s);
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
                    allParticipantsList.clear();
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

    public void search(String s)
    {
        allParticipantsList.clear();
        for(_User u: backupList){
            if(u.getObjectId().indexOf(s)!=-1 || u.getUsername().indexOf(s)!=-1 || u.getNick()!=null && u.getNick().indexOf(s)!=-1)
                allParticipantsList.add(u);
        }
        adapter.notifyDataSetChanged();
    }
}