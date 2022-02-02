package com.example.mymeeting.bomb;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.mymeeting.MainActivity;
import com.example.mymeeting.sp.UserStatus;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import static org.litepal.LitePalApplication.getContext;

//        TODO：******************************************
//        TODO：*************这个类目前已不使用*************
//        TODO：******************************************
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

        //添加本地数据库新会议，可以采取仅添加此会议或重新拉取服务器会议数据，应等待确认服务器添加会议成功后再添加本地新建会议

        return result[0];
    }


    public void searchAllMeeting(){
        BmobQuery<Meeting> bmobQuery = new BmobQuery<>();
        bmobQuery.findObjects(new FindListener<Meeting>() {
            @Override
            public void done(List<Meeting> list, BmobException e) {
                for (Meeting meeting: list){
                    if(meeting.getOriginator()!=null)
                    Log.d(TAG, "会议："+meeting.getId()+meeting.getName()+meeting.getOriginator().getObjectId());
                }
            }
        });

    }

    public void searchAttendingMeeting(){
        BmobQuery<Meeting> bmobQuery = new BmobQuery<>();
        String userObjectId = BmobUser.getCurrentUser(_User.class).getObjectId();
        bmobQuery.findObjects(new FindListener<Meeting>() {
            @Override
            public void done(List<Meeting> list, BmobException e) {
                for (Meeting meeting: list){
//                    if(meeting.getOriginator()!=null)
//                        Log.d(TAG, "会议："+meeting.getId()+meeting.getName()+meeting.getOriginator().getObjectId());
                    BmobQuery<_User> query = new BmobQuery<_User>();
                    query.addWhereRelatedTo("participant", new BmobPointer(meeting));
                    query.findObjects(new FindListener<_User>() {
                        @Override
                        public void done(List<_User> list, BmobException e) {
                            for (_User user:list){
                                if (userObjectId.equals(user.getObjectId()))
                                {
                                    String str=meeting.getRegistrationDate().getDate();
                                    SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                    Date date=new Date();
                                    try {
                                        date=format.parse(str);
                                    } catch (ParseException parseException) {
                                        parseException.printStackTrace();
                                    }
                                    Log.d(TAG, "会议："+ date.toString());
                                }
                            }
                        }
                    });
                }
            }
        });

    }


    public Boolean addMeetingTest(){
        Meeting meeting = new Meeting();
        meeting.setName("测试会议1");
        meeting.setComtent("这是一个测试会议这是一个测试会议这是一个测试会议这是一个测试会议这是一个测试会议");
        meeting.setIntroduction("测试会议参会者关联");
        meeting.setLength("2小时");
        meeting.setLocation("sy108");
        meeting.setOrganizer("学生会文化部");

        BmobDate bmobDate= new BmobDate(new Date());
        meeting.setHostDate(bmobDate);
        meeting.setRegistrationDate(bmobDate);
        meeting.setOriginator(BmobUser.getCurrentUser(_User.class));

        _User user = BmobUser.getCurrentUser(_User.class);
        BmobRelation relation = new BmobRelation();
        relation.add(user);
        meeting.setParticipant(relation);

        //buxing,需要单独传id viewModel也得需要context
//        UserStatus userStatus = new UserStatus(context);

        Boolean bool = this.addMeeting(meeting);

        return bool;
    }

}
