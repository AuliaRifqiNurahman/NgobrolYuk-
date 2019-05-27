package com.example.aulia_rifqi.chatkuy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List <Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mTimeUser;
    private FirebaseAuth mAuth;

    public MessageAdapter(List <Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public TextView displayName;
        public CircleImageView profileImage;
        public TextView displayTime;
        public ImageView messageImage;

        @SuppressLint("WrongViewCast")
        public MessageViewHolder(@NonNull View view) {
            super( view );

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            displayName = (TextView) view.findViewById(R.id.name_text_layout);
            displayTime = ( TextView ) view.findViewById( R.id.time_text_layout );
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);

        }
    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.message_single_layout, parent, false );

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder( view );
    }


    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder viewHolder, int i) {

        String mCurrentUserId = mAuth.getCurrentUser().getUid();

        final Messages m = mMessageList.get( i );

        final long time_user = m.getTime();
        String strDateFormat = "hh:mm";
        DateFormat dateFormat = new SimpleDateFormat( strDateFormat );
        final String formattedDate = dateFormat.format( time_user );

        String from_user = m.getFrom();
        String message_type = m.getType();

        mTimeUser = FirebaseDatabase.getInstance().getReference();
        mTimeUser.child( "messages" ).child( "time" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String time = dataSnapshot.child( String.valueOf( formattedDate ) ).getKey().toString();
                viewHolder.displayTime.setText( time );

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child( "Users" ).child( from_user );
        mUserDatabase.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild( "image" )){

                    String name = dataSnapshot.child("name").getValue().toString();
                    String profileImage = dataSnapshot.child( "thumb_image" ).getValue().toString();

                    viewHolder.displayName.setText(name);

                    Picasso.get().load( profileImage ).placeholder( R.drawable.default_avatar ).into( viewHolder.profileImage );

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        } );

        if(message_type.equals("text")) {

            viewHolder.messageText.setText(m.getMessage());
            viewHolder.messageImage.setVisibility(View.INVISIBLE);


        } else {

            viewHolder.messageText.setVisibility(View.INVISIBLE);
            Picasso.get().load( m.getMessage() ).placeholder(R.drawable.default_avatar).into(viewHolder.messageImage);

        }

        if (from_user.equals( mCurrentUserId )){

            viewHolder.messageText.setBackgroundResource( R.drawable.send_text_background );
            viewHolder.messageText.setTextColor(Color.BLACK);

        } else {

            viewHolder.messageText.setBackgroundResource( R.drawable.message_text_background );
            viewHolder.messageText.setTextColor(Color.BLACK);

        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}