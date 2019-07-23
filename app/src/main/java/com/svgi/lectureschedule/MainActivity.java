package com.svgi.lectureschedule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    private Map<String,String> SUBJECTS, FACULTY, ROOM_NUM;
    private String collage, year, branch, batch;
    private ArrayList<Integer> TIME;
    private String[] DAYS;
    private int CURRENT_MIN;
    private Calendar calendar;
    private int dayIndex, currentLectureIndex;
    private SharedPreferences sharedPreferences;
    private FirebaseFirestore db;
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(getApplicationContext());
        sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);

        if (sharedPreferences.getString("collage", null) != null) {
            initUI();
            initVar();
            fetchData();
        } else {
            startActivity(new Intent(MainActivity.this, SetClassDataActivity.class));
            finish();
        }

    }

    private void initVar() {
        calendar = Calendar.getInstance();
        DAYS = getResources().getStringArray(R.array.DAYS);

        collage = sharedPreferences.getString("collage", "");
        year = sharedPreferences.getString("year", "");
        branch = sharedPreferences.getString("branch", "");
        batch = sharedPreferences.getString("batch", "");
        Log.d(TAG, "initVar: "+collage+year+branch+batch);
    }

    private void initUI() {

    }

    private void fetchData() {
        db = FirebaseFirestore.getInstance();

        db.collection("collage").document(collage).collection("year")
                .document(year).collection("branches")
                .document(branch).collection("batches")
                .document(batch).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (Objects.requireNonNull(documentSnapshot).exists()) {
                    Log.d(TAG, "onEvent: == data found");
                    Log.d(TAG, "Current data: " + documentSnapshot.getData());
                    SUBJECTS = (Map<String, String>) documentSnapshot.get("subject");
                    FACULTY = (Map<String, String>) documentSnapshot.get("faculty");
                    ROOM_NUM= (Map<String, String>) documentSnapshot.get("room");
                    TIME =(ArrayList<Integer>)documentSnapshot.get("time");
                } else {
                    Log.d(TAG, "onEvent: == data note found");
                }
            }
        });
    }

    private void setUIData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferences.getString("collage", null) != null&&false) {
            CURRENT_MIN = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
            currentLectureIndex = -1;
            for (int i = 0; i < TIME.size() - 1; i++) {
                if (TIME.get(i) <= CURRENT_MIN && TIME.get(i + 1) > CURRENT_MIN) {
                    currentLectureIndex = i;
                }
            }
            setUIData();
        }
    }

}
