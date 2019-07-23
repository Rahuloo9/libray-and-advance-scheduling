package com.svgi.lectureschedule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;


public class MainActivity extends AppCompatActivity implements TimeTableProvider.OnTimeTableDataListener {


    private SharedPreferences sharedPreferences;
    private TimeTableProvider timeTableProvider;
    private TextView currentLec, nextLec;
    private LinearLayout curLinear, nextLinear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(getApplicationContext());
        sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);

        if (sharedPreferences.getString("collage", null) != null) {
            initUI();
        } else {
            startActivity(new Intent(MainActivity.this, SetClassDataActivity.class));
            finish();
        }

    }


    private void initUI() {
        currentLec = findViewById(R.id.curDetail);
        nextLec = findViewById(R.id.nextDetail);
        curLinear = findViewById(R.id.currentSection);
        nextLinear = findViewById(R.id.nextSection);
        timeTableProvider = new TimeTableProvider(this);
        timeTableProvider.setOnTimeTableDataListener(this);
    }

    private void setUIData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        timeTableProvider.inValidate();
    }


    @Override
    public void currentLecture(boolean isLecture, String sub, String fac, String room) {
        if (isLecture) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(sub);
            if (fac != null) stringBuilder.append(" by ").append(fac);
            if (room != null) stringBuilder.append(" in room no. ").append(room);
            currentLec.setText(stringBuilder.toString());
            curLinear.setVisibility(View.VISIBLE);
        } else {
            curLinear.setVisibility(View.GONE);
        }
    }

    @Override
    public void nextLecture(boolean isNext, String sub, String fac, String room) {
        if (isNext) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(sub);
            if (fac != null) stringBuilder.append(" by ").append(fac);
            if (room != null) stringBuilder.append(" in room no. ").append(room);
            nextLec.setText(stringBuilder.toString());
            nextLinear.setVisibility(View.VISIBLE);
        } else {
            nextLinear.setVisibility(View.GONE);
        }
    }
}
