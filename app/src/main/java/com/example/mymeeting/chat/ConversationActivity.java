package com.example.mymeeting.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.mymeeting.R;
import com.hyphenate.easeui.modules.chat.EaseChatFragment;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class ConversationActivity extends AppCompatActivity {

    // 当前聊天的 ID
    private String mChatId;
    private MyChatFragment chatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        EaseTitleBar titleBarMessage = findViewById(R.id.title_bar);
        //设置右侧菜单图标
        titleBarMessage.setRightImageResource(R.drawable.chat_user_info);
        //设置标题
        titleBarMessage.setTitle("和用户"+getIntent().getExtras().get("conversationId").toString()+"的聊天");
//        //设置标题位置
//        titleBarMessage.setTitlePosition(EaseTitleBar.TitlePosition.Left);
        //设置右侧菜单图标的点击事件
        titleBarMessage.setOnRightClickListener(new EaseTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {

            }
        });
        //设置返回按钮的点击事件
        titleBarMessage.setOnBackPressListener(new EaseTitleBar.OnBackPressListener() {
            @Override
            public void onBackPress(View view) {
                finish();
            }
        });

        // 这里直接使用EaseUI封装好的聊天界面
        chatFragment = new MyChatFragment();
        // 将参数传递给聊天界面
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.ec_layout_container, chatFragment).commit();

    }

    /**
     * 初始化界面
     */
    private void initView() {

    }
}