package com.epicodus.theinterview.ui;

import android.app.ProgressDialog;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.epicodus.theinterview.Constants;
import com.epicodus.theinterview.R;
import com.epicodus.theinterview.models.Chat;
import com.epicodus.theinterview.models.Message;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.messageList) RecyclerView mMessageList;
    @Bind(R.id.activityTitle) TextView mActivityTitle;
    @Bind(R.id.micImage) ImageView mMicImage;
    @Bind(R.id.sendButton) Button mSendButton;
    @Bind(R.id.finishButton) Button mFinishButton;
    @Bind(R.id.recordText) TextView mRecordText;

    private Chat mChat;
    private MediaRecorder mRecorder;
    private String mFileName;
    private StorageReference mStorage;
    private ProgressDialog mDialog;
    private String randomFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        mChat = Parcels.unwrap(getIntent().getParcelableExtra("chat"));

        mStorage = FirebaseStorage.getInstance().getReference();

        mMicImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent){
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){

                    startRecording();
                    mRecordText.setText("Recording");
                    Log.d("Chatstart", "recording");

                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP){

                    stopRecording();
                    mRecordText.setText("Press mic to start over");
                    Log.d("Chatend", "end recording");

                }
                return false;
            }
        });

        mDialog = new ProgressDialog(this);

        mMicImage.setOnClickListener(this);
        mSendButton.setOnClickListener(this);
        mFinishButton.setOnClickListener(this);

        //if chat active is false then change color of imagebackground?  when they try to click on mic give toast saying the interview has finished. same for button?
    }

    @Override
    public void onClick(View v) {
        if (v == mSendButton){
            uploadAudio();
        }
        if (v == mFinishButton){
            //set chat activity to false. if hiring manager get feedback prompt and save feedback. for both users return to main activity.

        }

    }

    private void startRecording() {
        randomFileName = UUID.randomUUID().toString() + ".3gp";

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/" + randomFileName;

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(mFileName);

        try {
            mRecorder.prepare();
            mRecorder.start();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e){
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.reset();
        mRecorder.release();
        mRecorder = null;
    }

    private void uploadAudio(){
        mRecordText.setText("Press mic to record");
        mDialog.setMessage("Sending");
        mDialog.show();

        StorageReference filepath = mStorage.child("Audio").child(randomFileName);
        Uri uri = Uri.fromFile(new File(mFileName));
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mDialog.dismiss();
            }
        });

        String filePathString = filepath.toString();
        long today = new Date().getTime();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String chatId = mChat.getPushId();

        Message message = new Message(filePathString, today, uid, chatId);

        DatabaseReference msgRef = FirebaseDatabase
                .getInstance()
                .getReference(Constants.FIREBASE_MESSAGE_REFERENCE)
                .child(chatId);

        DatabaseReference pushRef = msgRef.push();
        String pushId = pushRef.getKey();
        message.setPushId(pushId);
        pushRef.setValue(message);

    }
}
