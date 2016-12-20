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
    @Bind(R.id.finishButton) Button mFinishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        mMicImage.setOnClickListener(this);
        mSendButton.setOnClickListener(this);
        mFinishButton.setOnClickListener(this);

        //if chat active is false then change color of imagebackground?  when they try to click on mic give toast saying the interview has finished. same for button?
    }

    @Override
    public void onClick(View v) {
        if (v == mMicImage){

        }
        if (v == mSendButton){

        }
        if (v == mFinishButton){
            //set chat activity to false. if hiring manager get feedback prompt and save feedback. for both users return to main activity.

        }

    }
}
