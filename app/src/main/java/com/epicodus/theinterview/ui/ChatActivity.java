package com.epicodus.theinterview.ui;

import android.app.ProgressDialog;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.epicodus.theinterview.R;
import com.epicodus.theinterview.models.Chat;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.messageList) ListView mMessageList;
    @Bind(R.id.activityTitle) TextView mActivityTitle;
    @Bind(R.id.micImage) ImageView mMicImage;
    @Bind(R.id.sendButton) Button mSendButton;
    @Bind(R.id.finishButton) Button mFinishButton;

    private Chat mChat;
    private MediaRecorder mRecorder;
    private String mFileName;
    private StorageReference mStorage;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        mChat = Parcels.unwrap(getIntent().getParcelableExtra("chat"));

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/recorded_audio.3gp";

        mStorage = FirebaseStorage.getInstance().getReference();

        mMicImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent){
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){

                    startRecording();
                    Log.d("Chatstart", "recording");

                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP){

                    stopRecording();
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

        }
        if (v == mFinishButton){
            //set chat activity to false. if hiring manager get feedback prompt and save feedback. for both users return to main activity.

        }

    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
//
//        if (mRecorder != null){
//            mRecorder.release();
//        }

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
        mRecorder.release();
        mRecorder = null;

        uploadAudio();
    }

    private void uploadAudio(){
        mDialog.setMessage("Sending");
        mDialog.show();

        StorageReference filepath = mStorage.child("Audio").child("new_audio.3gp");
        Uri uri = Uri.fromFile(new File(mFileName));
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mDialog.dismiss();
            }
        });

    }
}
