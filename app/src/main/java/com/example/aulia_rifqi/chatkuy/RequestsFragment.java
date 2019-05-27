package com.example.aulia_rifqi.chatkuy;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private View RequestsFragmentView;
    private RecyclerView mRequestsList;

    private DatabaseReference FriendRequestsDatabase, UsersRef, FriendsDatabase;
    private FirebaseAuth mAuth;
    private String currentUserId;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RequestsFragmentView = inflater.inflate(R.layout.fragment_requests, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child( "Users" );
        FriendRequestsDatabase = FirebaseDatabase.getInstance().getReference().child( "Friend_req" );
        FriendsDatabase = FirebaseDatabase.getInstance().getReference().child( "Friends" );

        mRequestsList = (RecyclerView) RequestsFragmentView.findViewById( R.id.requests_list );
        mRequestsList.setLayoutManager( new LinearLayoutManager( getContext() ) );

        return RequestsFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery( FriendRequestsDatabase.child( currentUserId ), Request.class )
                .build();

        FirebaseRecyclerAdapter<Request, RequestsViewHolder> adapter = new FirebaseRecyclerAdapter <Request, RequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, int i, @NonNull Request model) {

                holder.itemView.findViewById( R.id.acc_req_btn ).setVisibility( View.VISIBLE );
                holder.itemView.findViewById( R.id.decline_req_btn ).setVisibility( View.VISIBLE );

                final String list_user_id = getRef( i ).getKey();

                DatabaseReference getTypeRef = getRef( i ).child( "request_type" ).getRef();

                getTypeRef.addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){

                            String type = dataSnapshot.getValue().toString();

                            //Received Friend Request
                            if (type.equals( "received" )){

                                Button request_sent_btn = holder.itemView.findViewById( R.id.acc_req_btn );
                                request_sent_btn.setText( "Request Received" );

                                holder.itemView.findViewById( R.id.decline_req_btn ).setVisibility( View.INVISIBLE );

                                UsersRef.child( list_user_id ).addValueEventListener( new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.hasChild( "image" )){

                                            final String requestProfileImage = dataSnapshot.child( "image" ).getValue().toString();

                                            Picasso.get().load( requestProfileImage ).into( holder.profileImage );

                                        }

                                        final String requestUserName = dataSnapshot.child( "name" ).getValue().toString();
                                        final String requestUserStatus = dataSnapshot.child( "status" ).getValue().toString();

                                        holder.userName.setText( requestUserName );
                                        holder.userStatus.setText( "I want to be your friend, let's chat with NgobrolYuk!" );

                                        holder.itemView.setOnClickListener( new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                CharSequence options[] = new CharSequence[]{

                                                        "Accept Friend Request",
                                                        "Decline Friend Request"

                                                };

                                                AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
                                                builder.setTitle( requestUserName + " Request Options" );

                                                builder.setItems( options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        if (i == 0){

                                                            final String currentDate = DateFormat.getDateTimeInstance().format( new Date(  ) );

                                                            FriendsDatabase.child( currentUserId ).child( list_user_id ).child( "date" )
                                                                    .setValue( currentDate ).addOnCompleteListener( new OnCompleteListener <Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()){

                                                                        FriendsDatabase.child( list_user_id ).child( currentUserId ).child( "date" )
                                                                                .setValue( currentDate ).addOnCompleteListener( new OnCompleteListener <Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                                if (task.isSuccessful()){

                                                                                    FriendRequestsDatabase.child( currentUserId ).child( list_user_id ).removeValue()
                                                                                            .addOnCompleteListener( new OnCompleteListener <Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task <Void> task) {

                                                                                                    if (task.isSuccessful()){

                                                                                                        FriendRequestsDatabase.child( list_user_id ).child( currentUserId ).removeValue()
                                                                                                                .addOnCompleteListener( new OnCompleteListener <Void>() {
                                                                                                                    @Override
                                                                                                                    public void onComplete(@NonNull Task <Void> task) {

                                                                                                                        if (task.isSuccessful()){

                                                                                                                            Toast.makeText( getContext(), "Friend Request Accepted Successfully", Toast.LENGTH_SHORT ).show();

                                                                                                                        }

                                                                                                                    }
                                                                                                                } );

                                                                                                    }

                                                                                                }
                                                                                            } );
                                                                                }

                                                                            }
                                                                        } );

                                                                    }

                                                                }
                                                            } );

                                                        }

                                                        if (i == 1){

                                                            FriendRequestsDatabase.child( currentUserId ).child( list_user_id ).removeValue()
                                                                    .addOnCompleteListener( new OnCompleteListener <Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task <Void> task) {

                                                                            if (task.isSuccessful()){

                                                                                FriendRequestsDatabase.child( list_user_id ).child( currentUserId ).removeValue()
                                                                                        .addOnCompleteListener( new OnCompleteListener <Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task <Void> task) {

                                                                                                if (task.isSuccessful()){

                                                                                                    Toast.makeText( getContext(), "Friend Request Decline Successfully", Toast.LENGTH_SHORT ).show();

                                                                                                }

                                                                                            }
                                                                                        } );

                                                                            }

                                                                        }
                                                                    } );

                                                        }

                                                    }
                                                } );

                                                builder.show();

                                            }
                                        } );

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                } );

                            }

                            //Send Request Friend
                            else if (type.equals( "sent" )){

                                Button request_sent_btn = holder.itemView.findViewById( R.id.acc_req_btn );
                                request_sent_btn.setText( "Request Sent" );

                                holder.itemView.findViewById( R.id.decline_req_btn ).setVisibility( View.INVISIBLE );

                                UsersRef.child( list_user_id ).addValueEventListener( new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.hasChild( "image" )){

                                            final String requestProfileImage = dataSnapshot.child( "image" ).getValue().toString();

                                            Picasso.get().load( requestProfileImage ).into( holder.profileImage );

                                        }

                                        final String requestUserName = dataSnapshot.child( "name" ).getValue().toString();
                                        final String requestUserStatus = dataSnapshot.child( "status" ).getValue().toString();

                                        holder.userName.setText( requestUserName );
                                        holder.userStatus.setText( "You have sent a request to " + requestUserName );

                                        holder.itemView.setOnClickListener( new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                CharSequence options[] = new CharSequence[]{

                                                        "Cancel Friend Request"

                                                };

                                                AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
                                                builder.setTitle( "Already Sent Request" );

                                                builder.setItems( options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        if (i == 0){

                                                            FriendRequestsDatabase.child( currentUserId ).child( list_user_id ).removeValue()
                                                                    .addOnCompleteListener( new OnCompleteListener <Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task <Void> task) {

                                                                            if (task.isSuccessful()){

                                                                                FriendRequestsDatabase.child( list_user_id ).child( currentUserId ).removeValue()
                                                                                        .addOnCompleteListener( new OnCompleteListener <Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task <Void> task) {

                                                                                                if (task.isSuccessful()){

                                                                                                    Toast.makeText( getContext(), "Friend Request Cancel Successfully", Toast.LENGTH_SHORT ).show();

                                                                                                }

                                                                                            }
                                                                                        } );

                                                                            }

                                                                        }
                                                                    } );

                                                        }

                                                    }
                                                } );

                                                builder.show();

                                            }
                                        } );

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                } );

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );

            }

            @NonNull
            @Override
            public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.users_req_layout, viewGroup, false );
                RequestsViewHolder holder = new RequestsViewHolder( view );
                return holder;
            }
        };

        mRequestsList.setAdapter( adapter );
        adapter.startListening();

    }


    public static class RequestsViewHolder extends RecyclerView.ViewHolder{

        TextView userName, userStatus;
        CircleImageView profileImage;
        Button AcceptButton, DeclineButton;

        public RequestsViewHolder(@NonNull View itemView) {
            super( itemView );

            userName = itemView.findViewById( R.id.user_req_name );
            userStatus = itemView.findViewById( R.id.user_req_status );
            profileImage = itemView.findViewById( R.id.user_req_image );
            AcceptButton = itemView.findViewById( R.id.acc_req_btn );
            DeclineButton = itemView.findViewById( R.id.decline_req_btn );

        }
    }

}