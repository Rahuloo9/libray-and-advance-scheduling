package com.svgi.lectureschedule.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.svgi.lectureschedule.R;
import com.svgi.lectureschedule.activity.SetClassDataActivity;
import com.svgi.lectureschedule.feature.CommonMethod;
import com.svgi.lectureschedule.model.Student;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;


public class TimeTableProvider extends Fragment {
    private Map<String, String> SUBJECTS, FACULTY = null, ROOM_NUM = null;
    private String collage, year, branch, batch;
    private ArrayList<Long> TIME;
    private String[] DAYS;
    private Calendar calendar;
    private int dayIndex;
    private FirebaseFirestore db;
    private OnTimeTableProviderListner onTimeTableProviderListner;
    private String TAG = "TimeTableProviderListner";
    private Context context;
    private TextView currentLec, nextLec, dayTitle;
    private LinearLayout curLinear, nextLinear;
    private ArrayList<String> lecList;
    private ArrayAdapter<String> arrayAdapter;


    @androidx.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @androidx.annotation.Nullable ViewGroup container, @androidx.annotation.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.time_table_fragment, container, false);
        context = getContext();
        initUI(view);
        initVar();

        return view;
    }


    public void initVar() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        File file = new File(getContext().getFilesDir(), "student.txt");
        Student student;
        if (file.exists()) {
            student = CommonMethod.loadStudentFromFile(getContext());
        } else if (auth.getCurrentUser() != null) {
            CommonMethod.fetchStudentData(auth.getUid(), getContext(), this);
            return;
        } else {
            // notify activity if student data is not set
            if (onTimeTableProviderListner != null) onTimeTableProviderListner.onDataNotFound();
            return;
        }
        calendar = Calendar.getInstance();
        DAYS = context.getResources().getStringArray(R.array.DAYS);
        db = FirebaseFirestore.getInstance();
        collage = student.getCollage();
        year = student.getYear();
        branch = student.getBranch();
        batch = student.getBatch();
        if (batch == null) {
            startActivity(new Intent(getContext(), SetClassDataActivity.class));
            Toast.makeText(getContext(), "Set Data Once", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "initVar: " + collage + year + branch + batch);
        fetchTimeData();
    }

    private void fetchTimeData() {
        db.collection("collage").document(collage).collection("other")
                .document("time").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (Objects.requireNonNull(documentSnapshot).exists()) {

                    Log.d(TAG, "Current data: =============================== " + documentSnapshot.getData());
                    TIME = (ArrayList<Long>) documentSnapshot.get("time");
                    fetchData();
                } else {
                    Toast.makeText(getContext(), "Institute timing not found", Toast.LENGTH_SHORT).show();
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
                } else {
                    Log.d(TAG, "onEvent: == data note found");
                }
            }
        });
    }


    private void getCurrentLecture() {
        int CURRENT_MIN = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        dayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        int currentLectureIndex = -1;
        for (int i = 0; i < TIME.size() - 1; i++) {
            int a = TIME.get(i).intValue();
            int b = TIME.get(i + 1).intValue();
            if (a <= CURRENT_MIN && b > CURRENT_MIN) {
                currentLectureIndex = i;
                break;
            }
        }
        String key = getId(dayIndex, currentLectureIndex);
        currentLecture(currentLectureIndex != -1,
                SUBJECTS.get(key), FACULTY != null ? FACULTY.get(key) : null,
                ROOM_NUM != null ? ROOM_NUM.get(key) : null);
        key = getId(dayIndex, currentLectureIndex + 1);

        nextLecture(currentLectureIndex < TIME.size() - 2 && currentLectureIndex != -1,
                SUBJECTS.get(key),
                FACULTY != null ? FACULTY.get(key) : null,
                ROOM_NUM != null ? ROOM_NUM.get(key) : null
        );

        showDayList(0);

    }

    private String getId(int day, int lec) {
        return "day" + day + "lec" + lec;
    }


    private void currentLecture(boolean isLecture, String sub, String fac, String room) {
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

    private void nextLecture(boolean isNext, String sub, String fac, String room) {
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

    private void dayListListener(ArrayList<String> strings, String day) {
        lecList.clear();
        lecList.addAll(strings);
        dayTitle.setText(day);
        arrayAdapter.notifyDataSetChanged();
    }


    private void showDayList(int counter) {
        dayIndex += counter;
        if (dayIndex == 0) dayIndex = 6;
        if (dayIndex == 7) dayIndex = 1;
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < TIME.size() - 1; i++) {
            strings.add((i + 1) + " : " + SUBJECTS.get(getId(dayIndex, i)));
        }

        dayListListener(strings, DAYS[dayIndex]);
    }

    private void initUI(View activity) {
        currentLec = activity.findViewById(R.id.curDetail);
        nextLec = activity.findViewById(R.id.nextDetail);
        curLinear = activity.findViewById(R.id.currentSection);
        nextLinear = activity.findViewById(R.id.nextSection);
        dayTitle = activity.findViewById(R.id.dayTitle);
        ImageButton btnNext = activity.findViewById(R.id.btnNext);
        ImageButton btnPrev = activity.findViewById(R.id.btnPrev);
        ListView dayList = activity.findViewById(R.id.dayList);
        lecList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, lecList);
        dayList.setAdapter(arrayAdapter);

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDayList(-1);
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDayList(1);
            }
        });

    }

    public void setOnTimeTableProviderListner(OnTimeTableProviderListner onTimeTableProviderListner) {
        this.onTimeTableProviderListner = onTimeTableProviderListner;
    }

    public interface OnTimeTableProviderListner {
        void onDataNotFound();
    }
}