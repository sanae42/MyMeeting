package com.example.mymeeting.sign;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymeeting.R;
import com.example.mymeeting.activityCollector.BaseActivity;
import com.example.mymeeting.bomb.Meeting;
import com.example.mymeeting.bomb._User;
import com.example.mymeeting.db.meetingItem;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import static org.litepal.LitePalApplication.getContext;


public class MeetingSignActivity extends BaseActivity {

    final String TAG = "MeetingSignActivity";

    String appkey = "de0d0d10141439f301fc9d139da66920";

    //传参可以用传递class
    meetingItem meeting;

    ProgressDialog progressDialog;

    TextView signinDetail;

    LinearLayout unsignedLayout;
    LinearLayout signedLayout;

    CardView QRcodeCardview;
    ImageView QRcode;

    //二维码
    Bitmap bitmap;

    private List<_User> allParticipantList = new ArrayList<>();
    private List<_User> allSigninList = new ArrayList<>();

    private List<_User> allSigninParticipantList = new ArrayList<>();
    private List<_User> allUnsigninParticipantList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_sign);

        //从跳转的活动得到传值
        Intent intent = getIntent();
        meeting = (meetingItem)intent.getSerializableExtra("meeting");

        initiateView();

        // 动态申请一组权限
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 1);
        }else {

        }

        //活动初始化数据
        showProgress();
        getParticipantFromBomb();

    }

    //当全部权限通过时才能扫描二维码
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用此功能", Toast.LENGTH_SHORT).show();
//                            finish();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    /**
     * 初始化控件
     */
    private void initiateView(){
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

        Button scanButton = findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeetingSignActivity.this, CaptureActivity.class);
                startActivityForResult(intent, 0);
                onActivityResult(5,5,intent );
            }
        });

        signinDetail = (TextView)findViewById(R.id.signin_detail);

        signedLayout = (LinearLayout) findViewById(R.id.signed_layout);
        unsignedLayout = (LinearLayout) findViewById(R.id.unsigned_layout);

        QRcodeCardview = (CardView) findViewById(R.id.QRcode_cardview);
        QRcode = (ImageView) findViewById(R.id.QRcode);
        QRcode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                imgChooseDialog();
                return true;
            }
        });

        if(meeting.getIfOriginator()){
            bitmap = QRCodeUtil.createSimpleQRCodeBitmap(meeting.getObjectId(), 800, 800,"UTF-8","H", "1", Color.BLACK, Color.WHITE);
            QRcode.setImageBitmap(bitmap);
        }
        else {
            QRcodeCardview.setVisibility(View.GONE);
        }
    }

    /**
     * 长按二维码图片弹出选择框（保存或分享）
     */
    private void imgChooseDialog(){
        AlertDialog.Builder choiceBuilder = new AlertDialog.Builder(MeetingSignActivity.this);
        choiceBuilder.setCancelable(false);
        choiceBuilder
                .setTitle("选择")
                .setSingleChoiceItems(new String[]{"存储至手机", "分享"}, -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0://存储
                                        saveImg(bitmap);
                                        break;
                                    case 1:// 分享
                                        shareImg(bitmap);
                                        break;
                                    default:
                                        break;
                                }
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        choiceBuilder.create();
        choiceBuilder.show();
    }

    /**
     * 保存图片至本地
     * @param bitmap
     */
    private void saveImg(Bitmap bitmap){
        String fileName = "qr_"+System.currentTimeMillis() + ".jpg";
        boolean isSaveSuccess = ImageUtil.saveImageToGallery(MeetingSignActivity.this, bitmap,fileName);
        if (isSaveSuccess) {
            Toast.makeText(getContext(), "图片已保存至本地", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "保存图片失败，请稍后重试", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 分享图片(直接将bitamp转换为Uri)
     * @param bitmap
     */
    private void shareImg(Bitmap bitmap){
        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");//设置分享内容的类型
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent = Intent.createChooser(intent, "分享");
        startActivity(intent);
    }



    /**
     * 监听活动返回信息
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TextView tvx = findViewById(R.id.textView);

        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                if(content.equals(meeting.getObjectId())){
                    showProgress();
                    signinMeeting();
                }else {
                    Toast.makeText(getContext(), "请确保扫描正确的会议签到二维码" , Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    /**
     * 从服务器获取全部参会者数据，得到全部参会者数据后调用getSigninListFromBomb()
     */
    public void getParticipantFromBomb(){
        Bmob.initialize(getContext(),appkey);
        BmobQuery<_User> query = new BmobQuery<_User>();
        Meeting M = new Meeting();
        M.setObjectId(meeting.getObjectId());
        query.addWhereRelatedTo("participant", new BmobPointer(M));
        query.findObjects(new FindListener<_User>() {
            @Override
            public void done(List<_User> list, BmobException e) {
                if(e==null) {
                    Log.d(TAG, "获取全部参会者数据成功，list长度：" + list.size());
                    allParticipantList.clear();
                    for (_User u:list){
                        allParticipantList.add(u);
                    }

                    //得到全部参会者数据后再获取全部已签到者数据
                    getSigninListFromBomb();
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            adapter.notifyDataSetChanged();
//                            swipeRefresh.setRefreshing(false);
//                        }
//                    });
                }else {
                    Log.d(TAG, "获取全部参会者数据失败：" + e.getMessage());
                    Toast.makeText(getContext(), "获取全部参会者数据失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            }
        });
    }

    /**
     * 从服务器获取全部已签到者数据
     */
    public void getSigninListFromBomb(){
//        Bmob.initialize(getContext(),appkey);
        BmobQuery<_User> query = new BmobQuery<_User>();
        Meeting M = new Meeting();
        M.setObjectId(meeting.getObjectId());
        query.addWhereRelatedTo("signinParticipant", new BmobPointer(M));
        query.findObjects(new FindListener<_User>() {
            @Override
            public void done(List<_User> list, BmobException e) {
                if(e==null) {
                    Log.d(TAG, "获取全部已签到者数据成功，list长度：" + list.size());
                    allSigninList.clear();
                    for (_User u:list){
                        allSigninList.add(u);
                    }

                    //处理数据
                    processingData();
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            adapter.notifyDataSetChanged();
//                            swipeRefresh.setRefreshing(false);
//                        }
//                    });
                }else {
                    Log.d(TAG, "获取全部已签到者数据失败：" + e.getMessage());
                    Toast.makeText(getContext(), "获取全部已签到者数据失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            }
        });
    }

    /**
     * 处理从服务器得到的数据/绘制图表
     */
    private void processingData(){
        allSigninParticipantList.clear();
        for(_User u1: allParticipantList){
            for(_User u2: allSigninList){
                if(u1.getObjectId().equals(u2.getObjectId())) {
                    allSigninParticipantList.add(u1);
                }
            }
        }
        allUnsigninParticipantList.clear();
        for(_User u3: allParticipantList){
            if(allSigninParticipantList.contains(u3)==false){
                allUnsigninParticipantList.add(u3);
            }
        }



        signinDetail.setText("已签到："+allSigninParticipantList.size()+"\n未签到："+allUnsigninParticipantList.size());
        Boolean signed = false;
        for (_User u: allSigninParticipantList){
            if(u.getObjectId().equals(BmobUser.getCurrentUser().getObjectId())==true){
                signed = true;
                break;
            }
        }
        if(signed==true){
            signedLayout.setVisibility(View.VISIBLE);
            unsignedLayout.setVisibility(View.GONE);
        }else {
            signedLayout.setVisibility(View.GONE);
            unsignedLayout.setVisibility(View.VISIBLE);
        }

        progressDialog.dismiss();
    }

    /**
     * 会议签到
     */
    private void signinMeeting(){
        Bmob.initialize(getContext(),appkey);
        BmobRelation relation = new BmobRelation();
        Meeting m = new Meeting();
        m.setObjectId(meeting.getObjectId());
        _User u = new _User();
        u.setObjectId(BmobUser.getCurrentUser().getObjectId());
        relation.add(u);
        m.setSigninParticipant(relation);
        m.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.d(TAG, "签到成功");
                    Toast.makeText(getContext(), "签到成功", Toast.LENGTH_SHORT).show();
                    //重新从服务器获取数据
                    getParticipantFromBomb();
//                    progressDialog.dismiss();
                }else {
                    Log.d(TAG, "签到失败：" + e.getMessage());
                    Toast.makeText(getContext(), "签到失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
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
