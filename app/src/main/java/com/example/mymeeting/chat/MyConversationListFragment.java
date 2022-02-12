package com.example.mymeeting.chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mymeeting.R;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.manager.EaseSystemMsgManager;
import com.hyphenate.easeui.modules.conversation.EaseConversationListFragment;
import com.hyphenate.easeui.utils.EaseCommonUtils;


public class MyConversationListFragment extends EaseConversationListFragment {


    @Override
    public void onItemClick(View view, int position) {
        super.onItemClick(view, position);
        //添加点击事件实现逻辑
        Object item = conversationListLayout.getItem(position).getInfo();
        // 跳转到聊天界面，开始聊天
        Intent intent = new Intent(getActivity(), ConversationActivity.class);
        // EaseUI封装的聊天界面需要这两个参数，聊天者的username，以及聊天类型，单聊还是群聊
        intent.putExtra("conversationId", ((EMConversation)item).conversationId());
        intent.putExtra("chatType", EMMessage.ChatType.Chat);
        intent.putExtra("isRoaming", true);
        startActivity(intent);

        if(item instanceof EMConversation) {
            if(EaseSystemMsgManager.getInstance().isSystemConversation((EMConversation) item)) {

            }else {

            }
        }
    }
}