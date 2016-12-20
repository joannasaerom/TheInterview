package com.epicodus.theinterview.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.epicodus.theinterview.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.messageList) ListView mMessageList;
    @Bind(R.id.activityTitle) TextView mActivityTitle;
    @Bind(R.id.micImage) ImageView mMicImage;
    @Bind(R.id.sendButton) Button mSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        mMicImage.setOnClickListener(this);
        mSendButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mMicImage){

        }
        if (v == mSendButton){

        }

    }
}
