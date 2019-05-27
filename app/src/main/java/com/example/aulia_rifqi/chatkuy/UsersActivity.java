package com.example.aulia_rifqi.chatkuy;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private EditText mSearchField;
    private ImageButton mSearchBtn;

    private RecyclerView mUsersList;

    private DatabaseReference mUsersDatabase;

    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_users );

        mToolbar = (Toolbar) findViewById( R.id.users_appBar );
        setSupportActionBar( mToolbar );

        getSupportActionBar().setTitle( "Find Friends" );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child( "Users" );

        mLayoutManager = new LinearLayoutManager(this);

        mSearchField = (EditText) findViewById( R.id.search_field );
        mSearchBtn = (ImageButton) findViewById( R.id.search_btn );

        mUsersList = (RecyclerView) findViewById( R.id.users_list );
        mUsersList.setHasFixedSize( true );
        mUsersList.setLayoutManager( mLayoutManager );

        mSearchBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchText = mSearchField.getText().toString();
                firebaseUserSearch(searchText);

            }
        } );

    }

    private void firebaseUserSearch(String searchText) {

        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery( mUsersDatabase.orderByChild( "name" ).startAt( searchText ).endAt( searchText + "\uf8ff" ),Users.class )
                .build();

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter <Users, UsersViewHolder>(options) {

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from( parent.getContext() )
                        .inflate( R.layout.users_single_layout, parent, false );

                return new UsersViewHolder( view );
            }

            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder usersViewHolder, int i, @NonNull Users users) {

                usersViewHolder.setName(users.getName());
                usersViewHolder.setUserStatus(users.getStatus());
                usersViewHolder.setUserImage(users.getThumb_image(), getApplicationContext());

                final String user_id = getRef( i ).getKey();

                usersViewHolder.mView.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent profileIntent = new Intent( UsersActivity.this, ProfileeActivity.class );
                        profileIntent.putExtra( "user_id", user_id );
                        startActivity( profileIntent );

                    }
                } );

            }
        };

        mUsersList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery( mUsersDatabase,Users.class )
                .build();

        FirebaseRecyclerAdapter <Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter <Users, UsersViewHolder>( options ) {
            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from( parent.getContext() )
                        .inflate( R.layout.users_single_layout, parent, false );

                return new UsersViewHolder( view );
            }

            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder usersViewHolder, int i, @NonNull Users users) {

                usersViewHolder.setName(users.getName());
                usersViewHolder.setUserStatus(users.getStatus());
                usersViewHolder.setUserImage(users.getThumb_image(), getApplicationContext());

                final String user_id = getRef( i ).getKey();

                usersViewHolder.mView.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent profileIntent = new Intent( UsersActivity.this, ProfileeActivity.class );
                        profileIntent.putExtra( "user_id", user_id );
                        startActivity( profileIntent );

                    }
                } );

            }
        };

        mUsersList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder( View itemView) {
            super( itemView );

            mView = itemView;

        }

        public void setName(String name) {

            TextView userNameView = (TextView) mView.findViewById( R.id.user_single_name );
            userNameView.setText( name );

        }

        public void setUserStatus(String status){

            TextView userStatusView = (TextView) mView.findViewById( R.id.user_single_status );
            userStatusView.setText( status );

        }


        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = (CircleImageView) mView.findViewById( R.id.user_single_image );

            Picasso.get().load( thumb_image ).placeholder( R.drawable.default_avatar ).into( userImageView );

        }

    }

}
