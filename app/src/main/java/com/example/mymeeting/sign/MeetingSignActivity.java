package com.example.mymeeting.sign;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.mymeeting.allParticipants.AllParticipantsListAdapter;
import com.example.mymeeting.bomb.Meeting;
import com.example.mymeeting.bomb._User;
import com.example.mymeeting.db.meetingItem;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
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

    //?????????????????????class
    meetingItem meeting;

    ProgressDialog progressDialog;

    TextView signinDetail;

    LinearLayout unsignedLayout;
    LinearLayout signedLayout;

    PieChart pieChart;

    CardView QRcodeCardview;
    ImageView QRcode;

    CardView userCardview;

    //recyclerview????????? ?????????allParticipants???????????????item??????
    private AllParticipantsListAdapter signedAdapter;
    private AllParticipantsListAdapter unsignedAdapter;

    //?????????
    Bitmap bitmap;

    private List<_User> allParticipantList = new ArrayList<>();
    private List<_User> allSigninList = new ArrayList<>();

    private List<_User> allSigninParticipantList = new ArrayList<>();
    private List<_User> allUnsigninParticipantList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_sign);

        //??????????????????????????????
        Intent intent = getIntent();
        meeting = (meetingItem)intent.getSerializableExtra("meeting");

        initiateView();

        // ????????????????????????
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

        //?????????????????????
        showProgress();
        getParticipantFromBomb();

    }

    //?????????????????????????????????????????????
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "?????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
//                            finish();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(this, "??????????????????", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    /**
     * ???????????????
     */
    private void initiateView(){
        //        ?????????
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //   ??????actionbar??????toolbar???????????????????????????????????????
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

        //        recyclerview??????
        RecyclerView signedRecyclerView = (RecyclerView) findViewById(R.id.signed_recycler_view);
        RecyclerView unsignedRecyclerView = (RecyclerView) findViewById(R.id.unsigned_recycler_view);

        GridLayoutManager signedLayoutManager = new GridLayoutManager(this, 1);
        GridLayoutManager unsignedLayoutManager = new GridLayoutManager(this, 1);
        signedRecyclerView.setLayoutManager(signedLayoutManager);
        unsignedRecyclerView.setLayoutManager(unsignedLayoutManager);

        signedAdapter = new AllParticipantsListAdapter(allSigninParticipantList);
        signedRecyclerView.setAdapter(signedAdapter);
        unsignedAdapter = new AllParticipantsListAdapter(allUnsigninParticipantList);
        unsignedRecyclerView.setAdapter(unsignedAdapter);

        signedLayout = (LinearLayout) findViewById(R.id.signed_layout);
        unsignedLayout = (LinearLayout) findViewById(R.id.unsigned_layout);

        pieChart = (PieChart) findViewById(R.id.picChart);

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

        userCardview = (CardView) findViewById(R.id.user_cardview);
        if(meeting.getIfOriginator()==false){
            userCardview.setVisibility(View.GONE);
        }
    }

    /**
     * ?????????????????????????????????????????????????????????
     */
    private void imgChooseDialog(){
        AlertDialog.Builder choiceBuilder = new AlertDialog.Builder(MeetingSignActivity.this);
        choiceBuilder.setCancelable(false);
        choiceBuilder
                .setTitle("??????")
                .setSingleChoiceItems(new String[]{"???????????????", "??????"}, -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0://??????
                                        saveImg(bitmap);
                                        break;
                                    case 1:// ??????
                                        shareImg(bitmap);
                                        break;
                                    default:
                                        break;
                                }
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        choiceBuilder.create();
        choiceBuilder.show();
    }

    /**
     * ?????????????????????
     * @param bitmap
     */
    private void saveImg(Bitmap bitmap){
        String fileName = "qr_"+System.currentTimeMillis() + ".jpg";
        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));
        //??????11???????????????uri??????????????????????????????????????????????????????????????????????????????????????????
        Toast.makeText(getContext(), "????????????????????????", Toast.LENGTH_LONG).show();
//        boolean isSaveSuccess = ImageUtil.saveImageToGallery(MeetingSignActivity.this, bitmap,fileName);
//        if (isSaveSuccess) {
//            Toast.makeText(getContext(), "????????????????????????", Toast.LENGTH_LONG).show();
//        } else {
//            Toast.makeText(getContext(), "????????????????????????????????????", Toast.LENGTH_SHORT).show();
//        }
    }

    /**
     * ????????????(?????????bitamp?????????Uri)
     * @param bitmap
     */
    private void shareImg(Bitmap bitmap){
        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");//???????????????????????????
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent = Intent.createChooser(intent, "??????");
        startActivity(intent);
    }



    /**
     * ????????????????????????
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
                    Toast.makeText(getContext(), "?????????????????????????????????????????????" , Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    /**
     * ??????????????????????????????????????????????????????????????????????????????getSigninListFromBomb()
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
                    Log.d(TAG, "????????????????????????????????????list?????????" + list.size());
                    allParticipantList.clear();
                    for (_User u:list){
                        allParticipantList.add(u);
                    }

                    //???????????????????????????????????????????????????????????????
                    getSigninListFromBomb();
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            adapter.notifyDataSetChanged();
//                            swipeRefresh.setRefreshing(false);
//                        }
//                    });
                }else {
                    Log.d(TAG, "????????????????????????????????????" + e.getMessage());
                    Toast.makeText(getContext(), "????????????????????????????????????" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            }
        });
    }

    /**
     * ??????????????????????????????????????????
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
                    Log.d(TAG, "???????????????????????????????????????list?????????" + list.size());
                    allSigninList.clear();
                    for (_User u:list){
                        allSigninList.add(u);
                    }

                    //????????????
                    processingData();
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            adapter.notifyDataSetChanged();
//                            swipeRefresh.setRefreshing(false);
//                        }
//                    });
                }else {
                    Log.d(TAG, "???????????????????????????????????????" + e.getMessage());
                    Toast.makeText(getContext(), "???????????????????????????????????????" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            }
        });
    }

    /**
     * ?????????????????????????????????/????????????
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

        signedAdapter.notifyDataSetChanged();
        unsignedAdapter.notifyDataSetChanged();


        signinDetail.setText("????????????"+allSigninParticipantList.size()+"\n????????????"+allUnsigninParticipantList.size());

        List strings = new ArrayList<>();
        Double d1 = Double.valueOf(allUnsigninParticipantList.size());
        Double d2 = Double.valueOf(allParticipantList.size());
        Double d3 = (d1/d2)*100;
        Integer integer = d3.intValue();
        strings.add(new PieEntry(integer,"?????????"));
        strings.add(new PieEntry(100-integer,"?????????"));
        PieDataSet dataSet = new PieDataSet(strings,"");
        ArrayList colors = new ArrayList();
        colors.add(ContextCompat.getColor(this,R.color.orange));
        colors.add(ContextCompat.getColor(this,R.color.blue));
        dataSet.setColors(colors);
        PieData pieData = new PieData(dataSet);
        pieData.setDrawValues(true);
        //???????????????
        pieData.setValueFormatter(new PercentFormatter());
        pieChart.setUsePercentValues(true);
        Description description = new Description();
        description.setText("");
        pieChart.setDescription(description);
        pieData.setValueTextSize(12f);
        pieChart.setData(pieData);
        pieChart.invalidate();


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
     * ????????????
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
                    Log.d(TAG, "????????????");
                    Toast.makeText(getContext(), "????????????", Toast.LENGTH_SHORT).show();
                    //??????????????????????????????
                    getParticipantFromBomb();
//                    progressDialog.dismiss();
                }else {
                    Log.d(TAG, "???????????????" + e.getMessage());
                    Toast.makeText(getContext(), "???????????????" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    /**
     * ???????????????
     */
    public  void showProgress(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("???????????????????????????");
        progressDialog.setMessage("?????????...");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    /**
     * ????????????????????????toolbar?????????
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
