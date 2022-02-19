package com.example.mymeeting;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.mymeeting.db.meetingItem;

import org.litepal.crud.DataSupport;

import java.util.Date;
import java.util.List;

import static org.litepal.LitePalApplication.getContext;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("服务", "onStartCommand: ");

        new Thread(new Runnable() {
            @Override
            public void run() {
                work();
            }
        }).start();
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour = 60*60*1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);
    }

    private void work(){
        List<meetingItem> meetings = DataSupport.findAll(meetingItem.class);
        Date date = new Date(System.currentTimeMillis());
        int num = 0;
        for (meetingItem m:meetings){
            if(m.getIfParticipant()==true){
                if( (date.getYear()+1900)==(m.getHostDate().getYear()+1900) || (date.getMonth()+1)==(m.getHostDate().getMonth()+1) || (date.getDate())==(m.getHostDate().getDate()))
                {
                    num++;
                }
            }
        }
        makeNotification(num);
    }

    private String createNotificationChannel(String channelID, String channelNAME, int level) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(channelID, channelNAME, level);
            manager.createNotificationChannel(channel);
            return channelID;
        } else {
            return null;
        }
    }


    private void makeNotification(int num) {
        if(num==0) return;

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

        String channelId = createNotificationChannel("my_channel_ID", "my_channel_NAME", NotificationManager.IMPORTANCE_HIGH);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getContext(), channelId)
                .setContentTitle("会议提醒")
                .setContentText("您有"+num+"个会议将在今天举行")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.logo)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.notify(100, notification.build());
    }

}