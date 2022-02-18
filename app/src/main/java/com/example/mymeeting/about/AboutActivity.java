package com.example.mymeeting.about;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.mymeeting.R;
import com.example.mymeeting.userEdit.UserNickEditActivity;
import com.example.mymeeting.userEdit.UserPasswordEditActivity;

public class AboutActivity extends AppCompatActivity {

    LinearLayout ruleLiearlayout;
    LinearLayout donationLiearlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initView();
    }

    private void initView(){
        ruleLiearlayout = (LinearLayout)findViewById(R.id.rule_layout);
        ruleLiearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://gitee.com/sanae42/MyMeeting/blob/master/%E7%94%A8%E6%88%B7%E6%9C%8D%E5%8A%A1%E6%9D%A1%E6%AC%BE.md"));
                startActivity(intent);
            }
        });
        donationLiearlayout = (LinearLayout)findViewById(R.id.donation_layout);
        donationLiearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDonationDialog();
            }
        });

    }

    /**
     * 自定义dialog2 简单自定义布局
     */
    private void showDonationDialog() {
        AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(AboutActivity.this);
        alterDiaglog.setView(R.layout.dialog_donation);//加载进去
        AlertDialog dialog = alterDiaglog.create();
        //显示
        dialog.show();
    }

}