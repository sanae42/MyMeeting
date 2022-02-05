package com.example.mymeeting.group;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.mymeeting.MeetingFragment;
import com.example.mymeeting.R;
import com.example.mymeeting.activityCollector.BaseActivity;
import com.example.mymeeting.bomb.GroupMessage;
import com.example.mymeeting.bomb.Meeting;
import com.example.mymeeting.bomb._User;
import com.example.mymeeting.db.meetingItem;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import static org.litepal.LitePalApplication.getContext;

//        ******************************************
//        *****开启活动获得群聊列表数据，之后直到活动关闭不再从服务器获取数据
//        *****发送一条消息会同时在本地list加载一条示例消息和开启上传线程把消息上传到服务器
//        *****发送消息不再加载进度条，发送失败会通过toast提示
//        *****可以考虑加键盘收起监听器，仅在recyclerView处于最底部时打开键盘刷新recyclerView显示底部
//        ******************************************

public class MeetingGroupActivity extends BaseActivity {

    private String appkey = "de0d0d10141439f301fc9d139da66920";

    private static final String TAG = "MeetingGroupActivity";

    //传参传递的会议class
    meetingItem meeting;

    private List<Msg> msgList = new ArrayList<Msg>();

    private EditText inputText;

    private Button send;

    private RecyclerView msgRecyclerView;

    //进度条
    ProgressDialog progressDialog;

    //消息列表适配器
    private MsgAdapter adapter;

    //比较两条Msg日期的比较器
    Comparator<Msg> comparator = new Comparator<Msg>() {
        @Override
        public int compare(Msg o1, Msg o2) {
            if(o1.getPostDate().getTime() > o2.getPostDate().getTime())return 1;
            else return -1;
        }
    } ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_group);

        //从跳转的活动得到传值
        Intent intent = getIntent();
        meeting = (meetingItem)intent.getSerializableExtra("meeting");

        // 初始化消息数据
//        initMsgs();
        getMessageFromBomb();

        //    初始化控件
        initiateView();


    }

    private void initiateView() {
        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        msgRecyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    sendMessageToBomb(content);

                    //暂时更新一条消息进行显示
                    Msg msg = new Msg();
                    msg.setContent(content);
                    msg.setIfMyMessage(true);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                    msgRecyclerView.smoothScrollToPosition(msgList.size()-1);// 将ListView定位到最后一行
//                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
                    inputText.setText(""); // 清空输入框中的内容
                }
            }
        });

//        ScrollView scrollView = findViewById(R.id.scroll_view);
//        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if(true){
//                    msgRecyclerView.smoothScrollToPosition(msgList.size()-1); // 将ListView定位到最后一行
//                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
//                }
//            }
//        });
    }

//    //判断recyclerView是否位于最底部
//    public static boolean isVisBottom(RecyclerView recyclerView){
//        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//        //屏幕中最后一个可见子项的position
//        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
//        //当前屏幕所看到的子项个数
//        int visibleItemCount = layoutManager.getChildCount();
//        //当前RecyclerView的所有子项个数
//        int totalItemCount = layoutManager.getItemCount();
//        //RecyclerView的滑动状态
//        int state = recyclerView.getScrollState();
//        if(visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && state == recyclerView.SCROLL_STATE_IDLE){
//            return true;
//        }else {
//            return false;
//        }
//    }


    private void sendMessageToBomb(String s){
        //展示进度条
//        showProgress();

        Bmob.initialize(getContext(),appkey);

        GroupMessage groupMessage = new GroupMessage();
        groupMessage.setContent(s);
        groupMessage.setState("normal");
        groupMessage.setType("normal");

        BmobDate postDate= new BmobDate(new Date());
        groupMessage.setPostDate(postDate);

        Meeting m = new Meeting();
        m.setObjectId(meeting.getObjectId());
        groupMessage.setMeeting(m);

        groupMessage.setSender(BmobUser.getCurrentUser(_User.class));
        if (meeting.getIfOriginator() == true){
            groupMessage.setSenderType("originator");
        }
        else {
            groupMessage.setSenderType("participant");
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                groupMessage.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e==null){
                            Log.d(TAG, "上传群组消息成功");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //取消展示进度条
//                                    progressDialog.dismiss();
                                }
                            });
                        }else{
                            Log.d(TAG, "上传群组消息失败：" + e.getMessage());
                            Toast.makeText(getContext(), "上传群组消息失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //取消展示进度条
//                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }
                });

            }
        }).start();

    }

    private void getMessageFromBomb(){
        //展示进度条
        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bmob.initialize(getContext(),appkey);
                BmobQuery<GroupMessage> bmobQuery = new BmobQuery<>();
                //搜索条件
                Meeting m = new Meeting();
                m.setObjectId(meeting.getObjectId());
                bmobQuery.addWhereEqualTo("meeting", m);
                bmobQuery.findObjects(new FindListener<GroupMessage>() {
                    @Override
                    public void done(List<GroupMessage> list, BmobException e) {
                        if(e==null){
                            Log.d(TAG, "获取服务器聊天群组数据成功，list长度："+list.size());

                            //没有群聊信息就退出此方法
                            if(list.size()==0) {
                                //取消展示进度条
                                progressDialog.dismiss();
                                return;
                            }

                            //清空聊天信息列表
                            msgList.clear();
                            for(GroupMessage g:list){
                                Msg msg = new Msg();
                                msg.setBomb_id(g.getId().intValue());
                                msg.setContent(g.getContent());
                                msg.setObjectId(g.getObjectId());

                                Date date = new Date();
                                SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                try {
                                    if(meeting.getRegistrationDate()!=null)
                                        date=format.parse(g.getPostDate().getDate());
                                } catch (ParseException parseException) {
                                    parseException.printStackTrace();
                                }
                                msg.setPostDate(date);

                                msg.setSenderType(g.getSenderType());
                                msg.setState(g.getState());
                                msg.setType(g.getType());

                                msg.setSender(g.getSender());
                                if(g.getSender().getObjectId().equals(BmobUser.getCurrentUser(_User.class).getObjectId()))
                                {
                                    msg.setIfMyMessage(true);
                                }
                                else {
                                    msg.setIfMyMessage(false);
                                }

                                //加入list
                                msgList.add(msg);

                            }

                            //刷新recyclerView布局
                            Collections.sort(msgList,comparator);
                            adapter.notifyDataSetChanged();
                            msgRecyclerView.smoothScrollToPosition(msgList.size()-1); // 将ListView定位到最后一行
                            msgRecyclerView.scrollToPosition(msgList.size() - 1);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //取消展示进度条
                                    progressDialog.dismiss();
                                }
                            });
                        }else{
                            Log.d(TAG, "获取服务器聊天群组数据失败：" + e.getMessage());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //取消展示进度条
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }



    /**
     * 展示进度条
     */
    public  void showProgress(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("正在和服务器同步数据");
        progressDialog.setMessage("请稍后...");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

}
