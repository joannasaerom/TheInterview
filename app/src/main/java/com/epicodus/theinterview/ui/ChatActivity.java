package com.epicodus.theinterview.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
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
import android.widget.Toast;

import com.epicodus.theinterview.Constants;
import com.epicodus.theinterview.R;
import com.epicodus.theinterview.adapters.FirebaseMessageViewHolder;
import com.epicodus.theinterview.models.Chat;
import com.epicodus.theinterview.models.Message;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
    private int questionCounter;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private List<String> interviewQuestions = new ArrayList<>();

    private String[] questions = {"Tell me about yourself.", "What do you like about current web development trends?", "How would you communicate with team members that are not developers?", "What are some of the challenges you faced while pairing?", "Give me an example of a recent challenge and how did you resolve it?", "What is a framework, and why use it?", "What is an object?", "What is string interpolation?"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        Typeface gravitas = Typeface.createFromAsset(getAssets(), "fonts/gravitas-one.regular.ttf");
        mActivityTitle.setTypeface(gravitas);
        mSendButton.setTypeface(gravitas);
        mFinishButton.setTypeface(gravitas);

        //create local variables
        final ArrayList<Message> mMessages = new ArrayList<>();

        mChat = Parcels.unwrap(getIntent().getParcelableExtra("chat"));

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        if (!mChat.isActive()) {
            mFinishButton.setEnabled(false);
            mSendButton.setEnabled(false);
            mMicImage.setEnabled(false);
            mActivityTitle.setText("Interview Over");
            Toast.makeText(ChatActivity.this, "Interview is no long active", Toast.LENGTH_SHORT).show();
        }


        //show question prompt if user is hiring manager
        if (mChat != null){
            if (mChat.getHiringManager().equals(uid)){
                questionCounter = mChat.getQuestionNumber();
                if (mChat.getQuestions().size() != 0){
                    interviewQuestions = mChat.getQuestions();
                } else {
                    interviewQuestions = generateQuestions(questions);
                    for (int i = 0; i < interviewQuestions.size(); i++){
                        mChat.addQuestion(interviewQuestions.get(i));
                    }
                }

                if (questionCounter > 0 && questionCounter < 5){
                    mActivityTitle.setTextSize(15.0f);
                    mActivityTitle.setTypeface(Typeface.SANS_SERIF);
                    mFinishButton.setText("Next Q");
                    mActivityTitle.setText(interviewQuestions.get(questionCounter - 1));

                } else if (questionCounter == 0){
                    mFinishButton.setText("Next Q");
                    mActivityTitle.setTextSize(15.0f);
                    mActivityTitle.setTypeface(Typeface.SANS_SERIF);
                    mActivityTitle.setText(interviewQuestions.get(questionCounter));
                } else if (questionCounter == 5 && mChat.isActive() == true){
                    mActivityTitle.setText(interviewQuestions.get(questionCounter - 1));
                    mActivityTitle.setTextSize(15.0f);
                    mActivityTitle.setTypeface(Typeface.SANS_SERIF);
                }
            }

        }

        mStorage = FirebaseStorage.getInstance().getReference();

        mMicImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent){
                //check to see if user has pressed down on mic image
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    randomFileName = UUID.randomUUID().toString() + ".3gp";
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

        mMessageReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mMessages.add(snapshot.getValue(Message.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setUpFirebaseAdapter();

        mDialog = new ProgressDialog(this);

        mMicImage.setOnClickListener(this);
        mSendButton.setOnClickListener(this);
        mFinishButton.setOnClickListener(this);
    }

    @Override
    public void onPause(){
        super.onPause();

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
    }

    @Override
    public void onClick(View v) {
        if (v == mSendButton){
            if (randomFileName == null || randomFileName.equals("")){
                Toast.makeText(ChatActivity.this, "Audio is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadAudio();
        }
        if (v == mFinishButton){
            //check if user is the hiring manager
            if (mChat.getHiringManager().equals(uid)){
                //check what number question it is and display next question if not the last question
                if (questionCounter == 4){
                    mActivityTitle.setText(interviewQuestions.get(questionCounter));
                    mFinishButton.setText("Finish");
                    questionCounter += 1;
                    mChat.setQuestionNumber(questionCounter);
                } else if (questionCounter < 5){
                    mActivityTitle.setText(interviewQuestions.get(questionCounter));
                    questionCounter += 1;
                    mChat.setQuestionNumber(questionCounter);
                } else {
                    setChatInactive();

                    Intent intent = new Intent(ChatActivity.this, NewFeedbackActivity.class);
                    intent.putExtra("chat", Parcels.wrap(mChat));
                    startActivity(intent);
                }
            }
            if (mChat.getInterviewee().equals(uid)){
                setChatInactive();

                //send users back to main activity
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    private void startRecording() {
        //generate random file name for saving audio
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

        //set randomFileName to empty string
        randomFileName = "";

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
        LinearLayoutManager lm = new LinearLayoutManager(this);
        mMessageList.setLayoutManager(lm);
        mMessageList.setAdapter(mFirebaseAdapter);

    }

    private List<String> generateQuestions(String[] questions){
        int index;
        String temp;
        Random random = new Random();
        for (int i = questions.length - 1; i > 0; i--){
            index = random.nextInt(i+1);
            temp = questions[index];
            questions[index] = questions[i];
            questions[i] = temp;
        }
        String[] shortenedList = Arrays.copyOfRange(questions, 0, 5);
        List<String> newQuestions = new ArrayList<>();

        for (int j = 0; j < shortenedList.length; j++){
            newQuestions.add(shortenedList[j]);
        }
        return newQuestions;
    }

    private void setChatInactive(){
        mChat.setActive(false);
        //create database reference for hr user to update their chat data
        DatabaseReference updateChatRef = FirebaseDatabase
                .getInstance()
                .getReference(Constants.FIREBASE_CHAT_REFERENCE)
                .child(mChat.getHiringManager())
                .child(mChat.getHiringManagerChatId());
        updateChatRef.setValue(mChat);

        //create database reference for interviewee to update their chat data
        DatabaseReference updateSecChatRef = FirebaseDatabase
                .getInstance()
                .getReference(Constants.FIREBASE_CHAT_REFERENCE)
                .child(mChat.getInterviewee())
                .child(mChat.getIntervieweeChatId());
        updateSecChatRef.setValue(mChat);
    }
}
