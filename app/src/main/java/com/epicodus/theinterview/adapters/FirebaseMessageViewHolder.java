package com.epicodus.theinterview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.epicodus.theinterview.R;
import com.epicodus.theinterview.models.Message;

/**
 * Created by joannaanderson on 12/21/16.
 */

public class FirebaseMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    View mView;
    Context mContext;

    public FirebaseMessageViewHolder(View itemView){
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
    }

    public void bindMessage(Message message){
        ImageView mPlaybackImage = (ImageView) mView.findViewById(R.id.playbackImage);
        TextView mTimestamp = (TextView) mView.findViewById(R.id.timestamp);

        mTimestamp.setText("Sent:" + message.getTimestamp());
    }

    @Override
    public void onClick(View v){

    }
}
