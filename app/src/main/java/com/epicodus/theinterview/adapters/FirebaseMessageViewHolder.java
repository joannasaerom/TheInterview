package com.epicodus.theinterview.adapters;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.epicodus.theinterview.Constants;
import com.epicodus.theinterview.R;
import com.epicodus.theinterview.models.Message;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

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

                Toast.makeText(mContext, "Play", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(mContext, "Pause", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void bindMessage(Message message){
        TextView mTimestamp = (TextView) mView.findViewById(R.id.timestamp);
        mMessage = message;
        mTimestamp.setText("Sent:" + message.getTimestamp());
    }

    private void fetchAudioUrlFromFirebase() {
        Log.d("FetchAudio", mMessage.getTextBody());

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
