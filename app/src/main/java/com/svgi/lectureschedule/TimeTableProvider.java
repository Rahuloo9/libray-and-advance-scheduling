package com.svgi.lectureschedule;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import javax.annotation.Nullable;

public class TimeTableProvider {
    private Map<String, String> SUBJECTS, FACULTY = null, ROOM_NUM = null;
    private String collage, year, branch, batch;
    private ArrayList<String> TIME;
    private String[] DAYS;
    private int CURRENT_MIN;
    private Calendar calendar;
    private int dayIndex, currentLectureIndex;
    private FirebaseFirestore db;
    private String TAG = "MainActivity";
    private boolean init = false;
    private Context context;
    private SharedPreferences sharedPreferences;
    private OnTimeTableDataListener onTimeTableDataListener = null;

    public TimeTableProvider(Context context) {
        this.context = context;
        initVar();
        fetchTimeData();
    }

    private void initVar() {
        calendar = Calendar.getInstance();
        DAYS = context.getResources().getStringArray(R.array.DAYS);
        Log.d(TAG, "initVar: day Index========================================= "+dayIndex+" day "+calendar.toString()+" cal "+(new Date()).toString());
        sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();
        collage = sharedPreferences.getString("collage", "");
        year = sharedPreferences.getString("year", "");
        branch = sharedPreferences.getString("branch", "");
        batch = sharedPreferences.getString("batch", "");

        Log.d(TAG, "initVar: " + collage + year + branch + batch);
    }

    private void fetchTimeData() {
        db.collection("collage").document(collage).collection("other")
                .document("time").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (Objects.requireNonNull(documentSnapshot).exists()) {

                    Log.d(TAG, "Current data: =============================== " + documentSnapshot.getData());
                    TIME = (ArrayList<String>) documentSnapshot.get("time");
                    fetchData();
                } else {
                    Log.d(TAG, "onEvent: == data note found");
                }
            }
        });
    }

    private void fetchData() {
        db.collection("collage").document(collage).collection("year")
                .document(year).collection("branch")
                .document(branch).collection("batch")
                .document(batch).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (Objects.requireNonNull(documentSnapshot).exists()) {
                    Log.d(TAG, "onEvent: == data found");
                    Log.d(TAG, "Current data: " + documentSnapshot.getData());
                    SUBJECTS = (Map<String, String>) documentSnapshot.get("subject");
                    FACULTY = (Map<String, String>) documentSnapshot.get("faculty");
                    ROOM_NUM = (Map<String, String>) documentSnapshot.get("room");
                    getCurrentLecture();
                    init = true;
                } else {
                    Log.d(TAG, "onEvent: == data note found");
                }
            }
        });
    }

    protected void inValidate() {
        if (init) {
            initVar();
            getCurrentLecture();
        }
    }

    private void getCurrentLecture() {
        CURRENT_MIN = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        dayIndex = calendar.get(Calendar.DAY_OF_WEEK)-1;

        currentLectureIndex = -1;
        for (int i = 0; i < TIME.size() - 1; i++) {
            int a = (Integer.parseInt(TIME.get(i)));
            int b = (Integer.parseInt(TIME.get(i + 1)));
            if (a <= CURRENT_MIN && b > CURRENT_MIN) {
                currentLectureIndex = i;
                break;
            }
        }
        if (onTimeTableDataListener != null) {
            String key = getId(dayIndex, currentLectureIndex);
            onTimeTableDataListener.currentLecture(currentLectureIndex != -1,
                    SUBJECTS.get(key)+" key "+key, FACULTY != null ? FACULTY.get(key) : null,
                    ROOM_NUM != null ? ROOM_NUM.get(key) : null);
            key = getId(dayIndex, currentLectureIndex + 1);

            onTimeTableDataListener.nextLecture(currentLectureIndex < TIME.size() - 2 && currentLectureIndex != -1,
                    SUBJECTS.get(key),
                    FACULTY != null ? FACULTY.get(key) : null,
                    ROOM_NUM != null ? ROOM_NUM.get(key) : null
            );

            showDayList(0);
        }
    }

    private String getId(int day, int lec) {
        return "day" + day + "lec" + lec;
    }

    public void setOnTimeTableDataListener(OnTimeTableDataListener onTimeTableDataListener) {
        this.onTimeTableDataListener = onTimeTableDataListener;
    }

    public interface OnTimeTableDataListener {
        void currentLecture(boolean isLecture, String sub, String fac, String room);
        void nextLecture(boolean isNext, String sub, String fac, String room);
        void dayListListener(ArrayList<String> strings,String day);
    }
    public void showDayList(int counter){
        dayIndex+=counter;
        if (dayIndex==0)dayIndex=6;
        if (dayIndex==7)dayIndex=1;
        ArrayList<String> strings=new ArrayList<>();
        for (int i=0;i<TIME.size()-1;i++){
            strings.add((i+1)+" : "+SUBJECTS.get(getId(dayIndex,i)));
        }
        if(onTimeTableDataListener!=null)onTimeTableDataListener.dayListListener(strings,DAYS[dayIndex]);
    }
}
