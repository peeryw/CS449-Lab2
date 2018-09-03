package com.peery.android.cs449_lab2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class About extends AppCompatActivity {

    private TextView mAboutText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mAboutText = findViewById(R.id.textViewAbout);
    }

    public void exit_about_activity(View view) {

        Intent intent = new Intent(About.this, UmpireBuddy.class );
        startActivity(intent);

    }
}
