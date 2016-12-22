package com.epicodus.theinterview.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.epicodus.theinterview.Constants;
import com.epicodus.theinterview.R;
import com.epicodus.theinterview.models.Chat;
import com.epicodus.theinterview.models.Feedback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.parceler.Parcels;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewFeedbackActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.instructions) TextView mInstructions;
    @Bind(R.id.feedback) EditText mFeedback;
    @Bind(R.id.submitButton) Button mSubmitButton;
    @Bind(R.id.activityTitle) TextView mActivityTitle;

    private Chat mChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_feedback);

        ButterKnife.bind(this);

        mChat = Parcels.unwrap(getIntent().getParcelableExtra("chat"));

        Typeface gravitas = Typeface.createFromAsset(getAssets(), "fonts/gravitas-one.regular.ttf");
        mActivityTitle.setTypeface(gravitas);
        mSubmitButton.setTypeface(gravitas);

        mSubmitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        if (v == mSubmitButton){
            String feedback = mFeedback.getText().toString();
            String userId = mChat.getInterviewee();
            String feedbackGivenBy = mChat.getHiringManager();
            long timestamp = new Date().getTime();

            if(feedback.equals("")){
                mFeedback.setError("Please enter feedback");
                return;
            }

            Feedback newFeedback = new Feedback(feedback, userId, feedbackGivenBy, timestamp);

            DatabaseReference feedbackRef = FirebaseDatabase
                    .getInstance()
                    .getReference(Constants.FIREBASE_FEEDBACK_REFERENCE)
                    .child(userId);

            DatabaseReference pushRef = feedbackRef.push();
            String pushId = pushRef.getKey();
            newFeedback.setPushId(pushId);
            pushRef.setValue(newFeedback);

            Intent intent = new Intent(NewFeedbackActivity.this, MainActivity.class);
            startActivity(intent);



        }
    }
}
