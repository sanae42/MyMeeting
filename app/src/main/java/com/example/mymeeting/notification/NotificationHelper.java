package com.example.mymeeting.notification;

import android.content.Intent;
import android.widget.Toast;

import com.example.mymeeting.db.noteItem;
import com.example.mymeeting.db.notificationItem;

import java.util.Date;

import cn.bmob.v3.BmobUser;

import static org.litepal.LitePalApplication.getContext;

public class NotificationHelper {

    public Boolean addNotification(String title, String content){
        notificationItem n = new notificationItem();
        n.setCreateDate(new Date());
        n.setUserObjectId(BmobUser.getCurrentUser().getObjectId());
        n.setTitle(title);
        n.setContent(content);
        if(n.save()==true){
            return true;
        }
        else {
            return false;
        }
    }
}
