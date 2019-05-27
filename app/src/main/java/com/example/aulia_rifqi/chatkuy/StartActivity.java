package com.example.aulia_rifqi.chatkuy;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private Button mReqBtn;
    private Button mLogBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mReqBtn = (Button) findViewById(R.id.start_req_btn);
        mLogBtn = (Button) findViewById( R.id.start_login_btn );

        mReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent req_intent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(req_intent);

            }
        });

        mLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent log_intent = new Intent( StartActivity.this, LoginActivity.class );
                startActivity( log_intent );
            }
        } );

    }

}
