package com.svgi.lectureschedule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements TimeTableProvider.OnTimeTableDataListener {


    private SharedPreferences sharedPreferences;
    private TimeTableProvider timeTableProvider;
    private TextView currentLec, nextLec,dayTitle;
    private ImageButton btnPrev, btnNext;
    private ListView dayList;
    private LinearLayout curLinear, nextLinear;
    private ArrayList<String> lecList;
    private ArrayAdapter<String> arrayAdapter;

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
        dayTitle = findViewById(R.id.dayTitle);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        dayList = findViewById(R.id.dayList);
        lecList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, lecList);
        dayList.setAdapter(arrayAdapter);
        timeTableProvider = new TimeTableProvider(this);
        timeTableProvider.setOnTimeTableDataListener(this);

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeTableProvider.showDayList(-1);
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeTableProvider.showDayList(1);
            }
        });

        final ScrollView mScrollView =findViewById(R.id.scroll);
        dayList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScrollView.requestDisallowInterceptTouchEvent(true);
                int action = event.getActionMasked();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        mScrollView.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });

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

    @Override
    public void dayListListener(ArrayList<String> strings, String day) {
        lecList.clear();
        lecList.addAll(strings);
        dayTitle.setText(day);
        arrayAdapter.notifyDataSetChanged();
    }
}
