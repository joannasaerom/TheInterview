package com.epicodus.theinterview.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.epicodus.theinterview.Constants;
import com.epicodus.theinterview.R;
import com.epicodus.theinterview.adapters.FirebaseMessageViewHolder;
import com.epicodus.theinterview.models.Chat;
import com.epicodus.theinterview.models.Message;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
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
    private FirebaseUser user;
    private String uid;
    private String randomFileName;
    private DatabaseReference mMessageReference;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private String[] interviewQuestions = {};

    private String[] questions = {"Tell me about yourself.", "What do you like about current web development trends?", "How would you communicate with team members that are not developers?", "What are some of the challenges you faced while pairing?", "Give me an example of a recent challenge and how did you resolve it?", "What is a framework, and why use it?", "What is an object?", "What is string interpolation?"};

    private int questionCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        //if chat active is false then change color of mic/sendbuttong?  when they try to click on mic give toast saying the interview has finished. same for button?

        questionCounter = 0;

        mChat = Parcels.unwrap(getIntent().getParcelableExtra("chat"));

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        if (mChat.getHiringManager().equals(uid)){
            mFinishButton.setText("Next Q");
            interviewQuestions = generateQuestions(questions);
            mActivityTitle.setText(interviewQuestions[questionCounter]);
            questionCounter += 1;
        }

        mStorage = FirebaseStorage.getInstance().getReference();

        mMicImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent){
                //check to see if user has pressed down on mic image
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){

                    startRecording();
                    mRecordText.setText("Recording");
                //check if user lifts finger off of image
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP){

                    stopRecording();
                    mRecordText.setText("Press mic to start over");

                }
                return false;
            }
        });

        mMessageReference = FirebaseDatabase
                .getInstance()
                .getReference(Constants.FIREBASE_MESSAGE_REFERENCE)
                .child(mChat.getHiringManagerChatId());
        setUpFirebaseAdapter();

        mDialog = new ProgressDialog(this);

        mMicImage.setOnClickListener(this);
        mSendButton.setOnClickListener(this);
        mFinishButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mSendButton){
            uploadAudio();
        }
        if (v == mFinishButton){
            //check if user is the hiring manager
            if (mChat.getHiringManager().equals(uid)){
                //check what number question it is and display next question if not the last question
                if (questionCounter < 5){
                    mActivityTitle.setText(interviewQuestions[questionCounter]);
                    questionCounter += 1;
                } else {
                    //generate
                }
            }
            mChat.setActive(false);
            //set chat activity to false. if hiring manager get feedback prompt and save feedback. for both users return to main activity.
            DatabaseReference updateChatRef = FirebaseDatabase
                    .getInstance()
                    .getReference(Constants.FIREBASE_CHAT_REFERENCE)
                    .child(mChat.getHiringManager())
                    .child(mChat.getHiringManagerChatId());

            updateChatRef.setValue(mChat);

            DatabaseReference updateSecChatRef = FirebaseDatabase
                    .getInstance()
                    .getReference(Constants.FIREBASE_CHAT_REFERENCE)
                    .child(mChat.getInterviewee())
                    .child(mChat.getIntervieweeChatId());

            updateSecChatRef.setValue(mChat);

            //send users back to main activity
            Intent intent = new Intent(ChatActivity.this, MainActivity.class);
            startActivity(intent);

        }

    }

    private void startRecording() {
        //generate random file name for saving audio
        randomFileName = UUID.randomUUID().toString() + ".3gp";
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/" + randomFileName;

        //reset audio recorder if it's not null
        if (mRecorder != null){
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }

        //set up audio recorder
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

        //create filepath where audio will be saved in Firebase
        StorageReference filepath = mStorage.child(Constants.FIREBASE_AUDIO_REFERENCE).child(randomFileName);
        Uri uri = Uri.fromFile(new File(mFileName));
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mDialog.dismiss();
            }
        });

        long today = new Date().getTime();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String chatId = mChat.getHiringManagerChatId();

        Message message = new Message(randomFileName, today, uid, chatId);

        //save message
        DatabaseReference pushRef = mMessageReference.push();
        String pushId = pushRef.getKey();
        message.setPushId(pushId);
        pushRef.setValue(message);

    }

    private void setUpFirebaseAdapter(){
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Message, FirebaseMessageViewHolder>(Message.class, R.layout.message_list_item, FirebaseMessageViewHolder.class, mMessageReference) {
            @Override
            protected void populateViewHolder(FirebaseMessageViewHolder viewHolder, Message model, int position) {
                viewHolder.bindMessage(model);
            }
        };

        //set recycler view settings
        mMessageList.setHasFixedSize(true);
        mMessageList.setLayoutManager(new LinearLayoutManager(this));
        mMessageList.setAdapter(mFirebaseAdapter);
    }

    private String[] generateQuestions(String[] questions){
        int index;
        String temp;
        Random random = new Random();
        for (int i = questions.length - 1; i > 0; i--){
            index = random.nextInt(i+1);
            temp = questions[index];
            questions[index] = questions[i];
            questions[i] = temp;
        }
        String[] newQuestions = Arrays.copyOfRange(questions, 0, 5);
        return newQuestions;
    }
}
