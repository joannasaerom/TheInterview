package com.epicodus.theinterview.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.epicodus.theinterview.Constants;
import com.epicodus.theinterview.R;
import com.epicodus.theinterview.adapters.FirebaseFeedbackViewHolder;
import com.epicodus.theinterview.models.Feedback;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FeedbackActivity extends AppCompatActivity {
    @Bind(R.id.feedbackList) RecyclerView mFeedbackList;
    @Bind(R.id.activityTitle) TextView mActivityTitle;

    private DatabaseReference mFeedbackReference;
    private FirebaseRecyclerAdapter mFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        mFeedbackReference = FirebaseDatabase
                .getInstance()
                .getReference(Constants.FIREBASE_FEEDBACK_REFERENCE)
                .child(uid);

        setUpFirebaseAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_feedback, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.action_logout){
            logout();
            return true;
        }
        if (id == R.id.interviews){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpFirebaseAdapter(){
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Feedback, FirebaseFeedbackViewHolder>(Feedback.class, R.layout.feedback_list_item, FirebaseFeedbackViewHolder.class, mFeedbackReference) {
            @Override
            protected void populateViewHolder(FirebaseFeedbackViewHolder viewHolder, Feedback model, int position) {
                viewHolder.bindFeedback(model);
            }
        };
        mFeedbackList.setHasFixedSize(true);
        mFeedbackList.setLayoutManager(new LinearLayoutManager(this));
        mFeedbackList.setAdapter(mFirebaseAdapter);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(FeedbackActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
