package com.epicodus.theinterview.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.epicodus.theinterview.R;
import com.epicodus.theinterview.models.Feedback;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by joannaanderson on 12/21/16.
 */

public class FirebaseFeedbackViewHolder extends RecyclerView.ViewHolder {
    View mView;
    Context mContext;

    public FirebaseFeedbackViewHolder(View itemView){
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
    }

    public void bindFeedback(Feedback feedback){
        TextView mFeedback = (TextView) mView.findViewById(R.id.feedback);
        TextView mTimestamp = (TextView) mView.findViewById(R.id.timestamp);

        Date date = new Date(feedback.getTimestamp());
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = df.format(date);

        mFeedback.setText(feedback.getTextBody());
        mTimestamp.setText("Feedback given on: " + formattedDate);

    }
}
