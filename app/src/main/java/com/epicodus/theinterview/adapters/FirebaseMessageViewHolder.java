package com.epicodus.theinterview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.epicodus.theinterview.R;
import com.epicodus.theinterview.models.Message;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by joannaanderson on 12/21/16.
 */

public class FirebaseMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @Bind(R.id.playback) ToggleButton mPlaybackImage;

    View mView;
    Context mContext;

    public FirebaseMessageViewHolder(View itemView){
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);

        mPlaybackImage.setOnClickListener(this);
    }

    public void bindMessage(Message message){
        TextView mTimestamp = (TextView) mView.findViewById(R.id.timestamp);

        mTimestamp.setText("Sent:" + message.getTimestamp());
    }

    @Override
    public void onClick(View v){
        if (v == mPlaybackImage) {
            if (((ToggleButton) v).isChecked()){
                Toast.makeText(mContext, "Play", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(mContext, "Pause", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
