package com.epicodus.theinterview.adapters;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.epicodus.theinterview.Constants;
import com.epicodus.theinterview.R;
import com.epicodus.theinterview.models.Message;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;


public class FirebaseMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    @Bind(R.id.playback) ToggleButton mPlaybackImage;

    View mView;
    Context mContext;
    private Message mMessage;

    private MediaPlayer mMediaPlayer;

    public FirebaseMessageViewHolder(View itemView){
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);

        mPlaybackImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        if (v == mPlaybackImage) {
            if (((ToggleButton) v).isChecked()){
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                fetchAudioUrlFromFirebase();
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mPlaybackImage.setChecked(false);
                    }
                });

            } else{
                mMediaPlayer.stop();
            }

        }
    }

    public void bindMessage(Message message){
        TextView mTimestamp = (TextView) mView.findViewById(R.id.timestamp);
        RelativeLayout mLayout = (RelativeLayout) mView.findViewById(R.id.container);

        mMessage = message;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        if (!message.getUserId().equals(uid)){
            RelativeLayout.LayoutParams layoutLp = (RelativeLayout.LayoutParams) mLayout.getLayoutParams();
            RelativeLayout.LayoutParams textLp = (RelativeLayout.LayoutParams) mTimestamp.getLayoutParams();
            layoutLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            textLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            mPlaybackImage.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryLight));
            mLayout.setBackgroundResource(R.drawable.backgroundyou);

        }

        Date date = new Date(message.getTimestamp());
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = df.format(date);


        mTimestamp.setText("Sent: " + formattedDate);
    }

    private void fetchAudioUrlFromFirebase() {

        StorageReference storage = FirebaseStorage
                .getInstance()
                .getReference()
                .child(Constants.FIREBASE_AUDIO_REFERENCE)
                .child(mMessage.getTextBody());

        storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    final String url = uri.toString();
                    mMediaPlayer.setDataSource(url);
                    mMediaPlayer.prepareAsync();
                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mMediaPlayer) {
                            mMediaPlayer.start();
                        }
                    });
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
}
