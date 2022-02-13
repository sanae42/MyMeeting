package com.example.mymeeting.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.mymeeting.R;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.modules.chat.EaseChatFragment;
import com.hyphenate.easeui.modules.chat.EaseChatInputMenu;
import com.hyphenate.easeui.modules.chat.EaseChatMessageListLayout;
import com.hyphenate.easeui.modules.chat.EaseInputMenuStyle;
import com.hyphenate.easeui.modules.chat.interfaces.EaseChatExtendMenuItemClickListener;
import com.hyphenate.easeui.modules.chat.interfaces.IChatExtendMenu;
import com.hyphenate.easeui.modules.chat.interfaces.IChatPrimaryMenu;

public class MyChatFragment extends EaseChatFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //获取到菜单输入父控件
        EaseChatInputMenu chatInputMenu = chatLayout.getChatInputMenu();
        //获取到菜单输入控件
        IChatPrimaryMenu primaryMenu = chatInputMenu.getPrimaryMenu();
        if(primaryMenu != null) {
            //设置菜单样式为不可用语音模式
            primaryMenu.setMenuShowType(EaseInputMenuStyle.DISABLE_VOICE);
        }

        //获取到聊天列表控件
        EaseChatMessageListLayout messageListLayout = chatLayout.getChatMessageListLayout();
        //设置默认头像（无效）
        messageListLayout.setAvatarDefaultSrc(ContextCompat.getDrawable(mContext, R.mipmap.user_icon));

        IChatExtendMenu chatExtendMenu = chatInputMenu.getChatExtendMenu();
        chatExtendMenu.setEaseChatExtendMenuItemClickListener(new EaseChatExtendMenuItemClickListener() {
            @Override
            public void onChatExtendMenuItemClick(int itemId, View view) {

            }
        });


    }


}
