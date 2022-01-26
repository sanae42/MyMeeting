package com.example.mymeeting.bomb;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.mymeeting.MainActivity;
import com.example.mymeeting.sp.UserStatus;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import static org.litepal.LitePalApplication.getContext;

public class doBomb {

    private String appkey = "de0d0d10141439f301fc9d139da66920";

    private static final String TAG = "doBomb";

    private Context context;

    public doBomb(Context c){
        context = c;
    }

    public Boolean addMeeting(Meeting meeting){
        Bmob.initialize(getContext(),appkey);

        final Boolean[] result = {true};

        meeting.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if(e==null){
                    Log.d(TAG, "添加数据成功，返回objectId为："+objectId);
                    result[0] = true;
                }else{
                    Log.d(TAG, "创建数据失败：" + e.getMessage());
                    result[0] = false;
                }
            }
        });

        //TODO:添加本地数据库新会议，可以采取仅添加此会议或重新拉取服务器会议数据，应等待确认服务器添加会议成功后再添加本地新建会议

        return result[0];
    }


    public Boolean addMeetingTest(){
        Meeting meeting = new Meeting();
        meeting.setName("测试会议x");
        meeting.setComtent("123412345");
        meeting.setIntroduction("jianjie");
        meeting.setLength("两小时");
        meeting.setLocation("sy108");
        meeting.setOrganizer("18301038");

        BmobDate bmobDate= new BmobDate(new Date());
        meeting.setHostDate(bmobDate);
        meeting.setRegistrationDate(bmobDate);

        //TODO:buxing,需要单独传id viewModel也得需要context
//        UserStatus userStatus = new UserStatus(context);

        Boolean bool = this.addMeeting(meeting);

        return bool;
    }

}
