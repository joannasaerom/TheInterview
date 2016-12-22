package com.epicodus.theinterview.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.epicodus.theinterview.Constants;
import com.epicodus.theinterview.R;
import com.epicodus.theinterview.models.Chat;
import com.epicodus.theinterview.ui.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;


/**
 * Created by joannaanderson on 12/21/16.
 */

public class FirebaseChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    View mView;
    Context mContext;

    public FirebaseChatViewHolder(View itemView){
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindChat(Chat chat){
        TextView mChatTitle = (TextView) mView.findViewById(R.id.chatTitle);

        mChatTitle.setText(chat.getHiringManagerChatId());
    }

    @Override
    public void onClick(View v){
        final ArrayList<Chat> chats = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        DatabaseReference ref = FirebaseDatabase
                .getInstance()
                .getReference(Constants.FIREBASE_CHAT_REFERENCE)
                .child(uid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    chats.add(snapshot.getValue(Chat.class));
                }

                int itemPosition = getLayoutPosition();
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("chat", Parcels.wrap(chats.get(itemPosition)));
                mContext.startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
