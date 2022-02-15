package com.example.mymeeting;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mymeeting.activityCollector.ActivityCollector;
import com.example.mymeeting.activityCollector.BaseActivity;
import com.example.mymeeting.allParticipants.AllParticipantsActivity;
import com.example.mymeeting.bomb.Meeting;
import com.example.mymeeting.bomb._User;
import com.example.mymeeting.chat.ConversationActivity;
import com.example.mymeeting.db.meetingItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.haibin.calendarview.CalendarView;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

import org.angmarch.views.NiceSpinner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import static org.litepal.LitePalApplication.getContext;

public class EditMeetingActivity extends BaseActivity {

    private String appkey = "de0d0d10141439f301fc9d139da66920";

    private static final String TAG = "EditMeetingActivity";

    //操作类型：new：新建  edit：编辑
    private String editType;

    //要编辑的会议
    private meetingItem meetingToEdit;

    EditText nameEditText;
    TextInputEditText introductionEditText;
    NiceSpinner typeSpinner;

    EditText organizerEditText;
    TextInputEditText contentEditText;
    NiceSpinner locationSpinner;

    EditText lengthEditText;
    CalendarView hostTimeCalendar;
    TimePicker hostTimePicker;

    FloatingActionButton saveFab;
    FloatingActionButton quickSaveFab; //

    CardView pictureCardview;
    Button uploadPictureButton;
    ImageView picture;
    String picturePath = "";

    //系统相册活动
    public static final int CHOOSE_PHOTO = 2;

    //加载进度条
    ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_meeting);

        initiateView();

        //根据操作类型设置显示填写内容
        Intent intent = getIntent();
        editType = intent.getStringExtra("type");
        if(editType.equals("new")){
            //TODO：图片选项未完成，暂不展示
            pictureCardview.setVisibility(View.GONE);
        }
        else if(editType.equals("edit")){
            meetingToEdit = (meetingItem)intent.getSerializableExtra("meeting");

            nameEditText.setText(meetingToEdit.getName());
            introductionEditText.setText(meetingToEdit.getIntroduction());
            typeSpinner.setSelectedIndex(0);   //后续再用内联函数之类的方式实现，懒得写了，就选0了

            organizerEditText.setText(meetingToEdit.getOrganizer());
            contentEditText.setText(meetingToEdit.getComtent());
            locationSpinner.setSelectedIndex(0);   //后续再用内联函数之类的方式实现，懒得写了，就选0了

            lengthEditText.setText(meetingToEdit.getLength());
            hostTimeCalendar.scrollToCalendar(meetingToEdit.getHostDate().getYear()+1900, meetingToEdit.getHostDate().getMonth()+1,meetingToEdit.getHostDate().getDate());
            hostTimePicker.setMinute(meetingToEdit.getHostDate().getMinutes());
            hostTimePicker.setHour(meetingToEdit.getHostDate().getHours());

            //编辑会议时不展示图片选项
            pictureCardview.setVisibility(View.GONE);
        }




    }

    /**
     * 初始化控件
     */
    private void initiateView() {
        //        导航条
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //   设置actionbar（即toolbar）最左侧按钮显示状态和图标
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        nameEditText = (EditText)findViewById(R.id.nameEditText);
        introductionEditText = (TextInputEditText)findViewById(R.id.introductionEditText);
        typeSpinner = (NiceSpinner)findViewById(R.id.typeSpinner);
        ArrayList<String> dataset_type = new ArrayList<>(Arrays.asList("兴趣社团会议", "学生职能社团会议", "学术研讨会议"));
        typeSpinner.attachDataSource(dataset_type);

        organizerEditText = (EditText)findViewById(R.id.organizerEditText);
        contentEditText = (TextInputEditText)findViewById(R.id.contentEditText);
        locationSpinner = (NiceSpinner)findViewById(R.id.locationSpinner);
        ArrayList<String> dataset_loc = new ArrayList<>(Arrays.asList("sy101", "sy102", "sy103","sy104", "sy105", "sy106"));
        locationSpinner.attachDataSource(dataset_loc);

        lengthEditText = (EditText)findViewById(R.id.lengthEditText);
        hostTimeCalendar = (CalendarView)findViewById(R.id.hostTimeCalendar);
//        Log.d(TAG, "日历选中："+hostTimeCalendar.getSelectedCalendar().getYear()+" "+hostTimeCalendar.getSelectedCalendar().getMonth()+" "+hostTimeCalendar.getSelectedCalendar().getDay());
        hostTimePicker = (TimePicker)findViewById(R.id.hostTimePicker);

        //上传图片只在新建会议时允许进行
        pictureCardview = (CardView)findViewById(R.id.upload_image_cardview);
        uploadPictureButton = (Button) findViewById(R.id.choose_from_album);
        picture = (ImageView) findViewById(R.id.picture);
        uploadPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果没有授予相册权限，申请授权；授予则打开相册
                if (ContextCompat.checkSelfPermission(EditMeetingActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditMeetingActivity.this, new String[]{ android.Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
                } else {
                    openAlbum();
                }
            }
        });


        //        悬浮按钮
        saveFab = (FloatingActionButton)findViewById(R.id.saveFab);
        quickSaveFab = (FloatingActionButton)findViewById(R.id.quickSaveFab); //
        saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "要提交会议吗", Snackbar.LENGTH_SHORT)
                        .setAction("是的", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(BmobUser.isLogin()==false){
                                    Toast.makeText(getContext(), "请在登录后再操作", Toast.LENGTH_SHORT).show();
                                }
                                else saveMeeting();
                            }
                        }).show();
            }
        });

    }

    /**
     * 打开相册
     */
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    /**
     * 监听权限授予
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "你需要授予使用相册权限才能上传图片", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    /**
     * 监听活动返回信息
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
//                        handleImageBeforeKitKat(data);
                        Toast.makeText(this, "您的设备安卓版本过低", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 处理图片
     */
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath); // 根据图片路径显示图片
    }

    /**
     * 展示图片
     */
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            picturePath = imagePath;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /**
     * 保存提交会议
     */
    public void saveMeeting(){
        String name = nameEditText.getText().toString();
        String introduction = introductionEditText.getText().toString();
        String type = typeSpinner.getText().toString();

        String organizer = organizerEditText.getText().toString();
        String content = contentEditText.getText().toString();
        String location = locationSpinner.getText().toString();

        String length = lengthEditText.getText().toString();
        Date hDate = new Date();
//        hDate = hostTimeCalendar.getSelectedCalendar().
//        hDate.setYear(hostTimeCalendar.getSelectedCalendar().getYear());
//        hDate.setMonth(hostTimeCalendar.getSelectedCalendar().getMonth());
//        hDate.setDate(hostTimeCalendar.getSelectedCalendar().getDay());
        hDate.setTime(hostTimeCalendar.getSelectedCalendar().getTimeInMillis());
        hDate.setHours(hostTimePicker.getCurrentHour());
        hDate.setMinutes(hostTimePicker.getCurrentMinute());
        hDate.setSeconds(0);
        Log.d(TAG, "日历选中："+ hDate.toString());
        BmobDate hostDate = new BmobDate(hDate);
        BmobDate registrationDate= new BmobDate(new Date());

        Log.d(TAG, "测试获取输入框内容："+hostTimeCalendar);

        Bmob.initialize(getContext(),appkey);
        Meeting meeting = new Meeting();
        meeting.setName(name);
        meeting.setComtent(content);
        meeting.setIntroduction(introduction);
        meeting.setLength(length);
        meeting.setLocation(location);
        meeting.setOrganizer(organizer);
        meeting.setHostDate(hostDate);
        meeting.setRegistrationDate(registrationDate);

        meeting.setOriginator(BmobUser.getCurrentUser(_User.class));

        //根据操作类型分情况
        if(editType.equals("new")){
            //新建会议state为“normal”
            meeting.setState("normal");

            BmobRelation relation = new BmobRelation();
            relation.add(BmobUser.getCurrentUser(_User.class));
            meeting.setParticipant(relation);
        }
        else if(editType.equals("edit")){}


        //展示进度条
        showProgress();

        if(editType.equals("new")){
            if(picturePath==null || picturePath.equals("")){
                saveFunction(meeting);
            }
            else if (picturePath.length()>0){
                BmobFile bmobFile = new BmobFile(new File(picturePath));
                bmobFile.uploadblock(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            //bmobFile.getFileUrl()--返回的上传文件的完整地址
                            Toast.makeText(getContext(), "上传图片成功", Toast.LENGTH_SHORT).show();
                            //在meeting中插入上传成功的图片文件
                            meeting.setPicture(bmobFile);
                            saveFunction(meeting);
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "上传图片失败"+e.getMessage());
                                    Toast.makeText(getContext(), "上传图片失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    //取消展示进度条
                                    progressDialog.dismiss();
                                }
                            });

                        }

                    }

                    @Override
                    public void onProgress(Integer value) {
                        // 返回的上传进度（百分比）
                    }
                });
            }
        }
        else if(editType.equals("edit")){
            meeting.update(meetingToEdit.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "创建编辑成功");
                                Toast.makeText(getContext(), "创建编辑成功", Toast.LENGTH_SHORT).show();
                                //TODO : (已过时)这里应该提醒主活动list刷新，但因为不是主活动跳转的活动，没法用监听活动返回来实现; 但可以先返回MeetingActivity，在监听，再返回主活动，间接实现
                                //返回主活动，刷新两个列表
//                                Intent intent = new Intent();
//                                setResult(RESULT_OK,intent);
                                //TODO : 这里采用广播通知主活动刷新，活动管理器退出到主活动
                                Intent intent_broadcast = new Intent("com.example.mymeeting.REFRESH_DATA");
                                sendBroadcast(intent_broadcast, "com.example.mymeeting.REFRESH_DATA");
                                //取消展示进度条
                                progressDialog.dismiss();
                                ActivityCollector.backToMainActivity();

//                                // 自定义广播测试  失败
//                                Intent intent_broadcast = new Intent("com.example.mymeeting.refreshData1");
////                                intent_broadcast.setComponent(new ComponentName("com.example.mymeeting","com.example.mymeeting.broadcast.MyReceiver"));
//                                sendBroadcast(intent_broadcast);

//                                finish();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "创建编辑失败");
                                Toast.makeText(getContext(), "创建编辑失败", Toast.LENGTH_SHORT).show();
                                //取消展示进度条
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            });
        }


    }

    /**
     * saveMeeting调用的新建会议时的保存子方法
     */
    protected void saveFunction(Meeting meeting){
        meeting.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                //TODO: ****_User表中有attendingMeeting的版本
                if(e==null){
                    Log.d(TAG, "创建会议成功，返回objectId为："+objectId);
                    Toast.makeText(getContext(), "创建会议成功，返回objectId为："+objectId, Toast.LENGTH_SHORT).show();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BmobRelation relation = new BmobRelation();
                            Meeting m = new Meeting();
                            m.setObjectId(objectId);
                            relation.add(m);
                            _User u = new _User();
                            u.setObjectId(BmobUser.getCurrentUser().getObjectId());
                            u.setAttendingMeeting(relation);
                            u.update(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        Log.d(TAG, "会议和当前用户参会绑定成功");

                                        //新建环信群组
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                newGroup(objectId);
                                            }
                                        }).start();

                                        //返回主活动，刷新两个列表
                                        Intent intent = new Intent();
                                        setResult(RESULT_OK,intent);
                                        finish();
                                    }else{
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //取消展示进度条
                                                progressDialog.dismiss();
                                            }
                                        });
                                        Log.d(TAG, "会议和当前用户参会绑定失败"+e.getMessage());
                                    }
                                }
                            });

                        }
                    });

                }else{
                    Log.d(TAG, "创建会议失败：" + e.getMessage());
                    Toast.makeText(getContext(), "创建会议失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 新建环信群
     */
    //TODO：方法逻辑需要进一步精简
    private void newGroup(String objectId){
        String username = BmobUser.getCurrentUser().getUsername();
        String password = "1";
        //已经登录
        if (EMClient.getInstance().isLoggedInBefore()){
            //开始创建群组
            EMGroupOptions option = new EMGroupOptions();
            option.maxUsers = 99;
            option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
            String groupName = objectId;
            String desc = "";
            String[] allMembers = new String[]{};
            String reason = "";
            try {
                EMClient.getInstance().groupManager().createGroup(groupName, desc, allMembers, reason, option);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //会议创建成功
                        Toast.makeText(getContext(), "会议群组创建成功" , Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (HyphenateException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //会议创建失败
                        int errorCode = e.getErrorCode();
                        String message = e.getMessage();
                        Toast.makeText(getContext(), "会议群组创建失败" +errorCode+" "+message, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "群组创建失败: "+errorCode+" "+message);
                    }
                });
            }
        }else {
            //没有登录，开始登录
            EMClient.getInstance().login(username, password, new EMCallBack() {
                /**
                 * 登陆成功的回调
                 */
                @Override
                public void onSuccess() {
                    //开始创建群组
                    EMGroupOptions option = new EMGroupOptions();
                    option.maxUsers = 99;
                    option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                    String groupName = objectId;
                    String desc = "";
                    String[] allMembers = new String[]{};
                    String reason = "";
                    try {

                        EMGroup group = EMClient.getInstance().groupManager().createGroup(groupName, desc, allMembers, reason, option);
                        group.getGroupId()
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //会议创建成功
                                Toast.makeText(getContext(), "会议群组创建成功" , Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //会议创建失败
                                int errorCode = e.getErrorCode();
                                String message = e.getMessage();
                                Toast.makeText(getContext(), "会议群组创建失败" +errorCode+" "+message, Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "群组创建失败: "+errorCode+" "+message);
                            }
                        });
                    }
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

    }

    /**
     * 展示进度条
     */
    public  void showProgress(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("正在获取服务器数据");
        progressDialog.setMessage("请稍后...");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    /**
     * 按键监听，此处即toolbar上按键
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}