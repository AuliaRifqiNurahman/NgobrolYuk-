package com.example.aulia_rifqi.chatkuy;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    // Android Layout
    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;

    private Button mStatusBtn;
    private Button mImageBtn;

    private static final int GALLERY_PICK = 1;

    //Storage Firebase
    private StorageReference mImageStorage;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_settings );

        mDisplayImage = (CircleImageView) findViewById( R.id.settings_image );
        mName = (TextView) findViewById( R.id.settings_display_name );
        mStatus = (TextView ) findViewById( R.id.settings_status );

        mStatusBtn = (Button) findViewById( R.id.settings_status_btn );
        mImageBtn = (Button) findViewById( R.id.settings_image_btn );

        mImageStorage = FirebaseStorage.getInstance().getReference();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child( "Users" ).child( current_uid );
        mUserDatabase.keepSynced( true );

        mUserDatabase.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child( "name" ).getValue().toString();
                final String image = dataSnapshot.child( "image" ).getValue().toString();
                String status = dataSnapshot.child( "status" ).getValue().toString();
                String thumb_image = dataSnapshot.child( "thumb_image" ).getValue().toString();

                mName.setText( name );
                mStatus.setText( status );

                if (!image.equals( "default" )) {

                    Picasso.get().load( image ).networkPolicy( NetworkPolicy.OFFLINE ).placeholder( R.drawable.default_avatar ).into( mDisplayImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {

                            Picasso.get().load( image ).placeholder( R.drawable.default_avatar ).into( mDisplayImage);

                        }
                    } );

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        mStatusBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String status_value = mStatus.getText().toString();

                Intent status_intent = new Intent( SettingsActivity.this, StatusActivity.class );
                status_intent.putExtra( "status_value", status_value );
                startActivity( status_intent );

            }
        } );


        mImageBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent galleryIntent = new Intent(  );
                galleryIntent.setType( "image/*" );
                galleryIntent.setAction( Intent.ACTION_GET_CONTENT );

                startActivityForResult( Intent.createChooser( galleryIntent, "SELECT IMAGE" ), GALLERY_PICK );

            }
        } );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio( 1, 1 )
                    .setMinCropWindowSize( 1000, 500 )
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mProgressDialog = new ProgressDialog( SettingsActivity.this );
                mProgressDialog.setTitle( "Uploading Image..." );
                mProgressDialog.setMessage( "Please Wait While We Upload and Process The Image." );
                mProgressDialog.setCanceledOnTouchOutside( false );
                mProgressDialog.show();

                Uri resultUri = result.getUri();

                final File thumb_filePath = new File( resultUri.getPath() );

                //Getting the Current UID of the User and storing it in a String.
                final String currentUserUid = mCurrentUser.getUid();

                try {
                    Bitmap thumb_bitmap = new Compressor( this )
                            .setMaxWidth( 200 )
                            .setMaxHeight( 200 )
                            .setQuality( 75 )
                            .compressToBitmap( thumb_filePath );

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte = baos.toByteArray();





                //Saving the image in the Firebase Storage and naming the child with the UID.
                final StorageReference filepath = mImageStorage.child("profile_images").child(currentUserUid + ".jpg");
                final StorageReference thumb_filepath = mImageStorage.child( "profile_images" ).child( "thumbs" ).child( currentUserUid + ".jpg" );

                //If the resultUri is nor Empty or NULL.
                if (resultUri != null) {

                    //We Will setup an OnCompleteListener to store the image in the desired location in the storage.
                    filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                            //If the task is Successful we will display a toast.
                            if (task.isSuccessful()){

                                mImageStorage.child("profile_images").child(currentUserUid + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(final Uri uri) {

                                        final String downloadUrl = uri.toString();

                                        UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                                        uploadTask.addOnCompleteListener( new OnCompleteListener <UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(final Task <UploadTask.TaskSnapshot> thumb_task) {

                                                mImageStorage.getDownloadUrl().addOnCompleteListener( new OnCompleteListener <Uri>() {
                                                    @Override
                                                    public void onComplete(Task <Uri> task) {

                                                        String thumb_downloadUrl = uri.toString();


                                                if (thumb_task.isSuccessful()){

                                                    Map update_hashMap = new HashMap();
                                                    update_hashMap.put( "image", downloadUrl );
                                                    update_hashMap.put( "thumb_image", thumb_downloadUrl );

                                                    mUserDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()){

                                                                Toast.makeText(SettingsActivity.this , "Success Uploading" , Toast.LENGTH_LONG).show();
                                                                mProgressDialog.dismiss();

                                                            }

                                                        }
                                                    });

                                                } else {

                                                    Toast.makeText(SettingsActivity.this , "Error in Uploading Thumbnail" , Toast.LENGTH_LONG).show();

                                                    mProgressDialog.dismiss();

                                                }

                                            }
                                        } );

                                            }
                                        } );

                                    }
                                });


                            }else {

                                Toast.makeText(SettingsActivity.this , "Error in Uploading" , Toast.LENGTH_LONG).show();

                                mProgressDialog.dismiss();

                            }
                        }
                    });
                }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //If the task is not successful then we will display an Error Message.
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

            }
        }

    }


    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(15);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

}
