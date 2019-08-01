package com.svgi.lectureschedule.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.svgi.lectureschedule.R;
import com.svgi.lectureschedule.feature.CommonMethod;
import com.svgi.lectureschedule.feature.Student;

import java.util.ArrayList;
import java.util.Objects;

public class SetClassDataActivity extends AppCompatActivity {

    private Spinner spinCollage, spinYear, spinBranch, spinBatch;
    private FirebaseFirestore db;
    private ArrayList<String> arrayListCollage, arrayListYear, arrayListBranch, arrayListBatch;
    private ArrayAdapter<String> arrayAdapterCollage, arrayAdapterYear, arrayAdapterBranch, arrayAdapterBatch;
    private String collage = null, year = null, branch = null, batch = null;
    private Student student;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_class_data);

        initUI();
        initVar();

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        initClick();
        getCollageData();
    }

    private void initUI() {
        spinCollage = findViewById(R.id.spinCollage);
        spinYear = findViewById(R.id.spinYear);
        spinBranch = findViewById(R.id.spinBranch);
        spinBatch = findViewById(R.id.spinBatch);
    }

    private void initVar() {
        arrayListCollage = new ArrayList<>();
        arrayListYear = new ArrayList<>();
        arrayListBranch = new ArrayList<>();
        arrayListBatch = new ArrayList<>();

        arrayAdapterCollage = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayListCollage);
        arrayAdapterCollage.add("select collage");
        arrayAdapterYear = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayListYear);
        arrayAdapterYear.add("select year");
        arrayAdapterBranch = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayListBranch);
        arrayAdapterBranch.add("select branch");
        arrayAdapterBatch = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayListBatch);
        arrayAdapterBatch.add("select batch");

        spinCollage.setAdapter(arrayAdapterCollage);
        spinYear.setAdapter(arrayAdapterYear);
        spinBranch.setAdapter(arrayAdapterBranch);
        spinBatch.setAdapter(arrayAdapterBatch);

        student = CommonMethod.loadStudentFromFile(this);
    }

    private void initClick() {
        spinCollage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    collage = spinCollage.getSelectedItem().toString();
                    getYearData();
                } else {
                    collage = null;
                }
                Log.d("spin", "onItemSelected: ===========================================" + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    year = spinYear.getSelectedItem().toString();
                    getBranchData();
                } else {
                    year = null;
                }
                Log.d("spin", "onItemSelected: ===========================================" + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    branch = spinBranch.getSelectedItem().toString();
                    getBatchData();
                } else {
                    branch = null;
                }
                Log.d("spin", "onItemSelected: ===========================================" + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinBatch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    batch = spinBatch.getSelectedItem().toString();
                } else {
                    batch = null;
                }
                Log.d("spin", "onItemSelected: ===========================================" + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAndFetch();
            }
        });

    }

    private void getCollageData() {

        db.collection("collage").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    arrayListCollage.clear();
                    arrayListCollage.add("select collage");
                    for (QueryDocumentSnapshot ds : Objects.requireNonNull(task.getResult())) {
                        arrayListCollage.add(ds.getId());
                    }
                    spinCollage.setEnabled(true);
                    arrayAdapterCollage.notifyDataSetChanged();
                } else {
                    Log.d("res", "Error getting documents: ", task.getException());
                }
            }
        });


    }

    private void getYearData() {
        db.collection("collage").document(collage).collection("year")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    arrayListYear.clear();
                    arrayListYear.add("select year");
                    for (QueryDocumentSnapshot ds : Objects.requireNonNull(task.getResult())) {
                        arrayListYear.add(ds.getId());
                    }
                    spinYear.setEnabled(true);
                    arrayAdapterYear.notifyDataSetChanged();
                } else {
                    Log.d("res", "Error getting documents: ", task.getException());
                }
            }
        });

    }

    private void getBranchData() {
        db.collection("collage").document(collage).collection("year")
                .document(year).collection("branch")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    arrayListBranch.clear();
                    arrayListBranch.add("select branch");
                    for (QueryDocumentSnapshot ds : Objects.requireNonNull(task.getResult())) {
                        arrayListBranch.add(ds.getId());
                    }
                    spinBranch.setEnabled(true);
                    arrayAdapterBranch.notifyDataSetChanged();
                } else {
                    Log.d("res", "Error getting documents: ", task.getException());
                }
            }
        });

    }

    private void getBatchData() {
        db.collection("collage").document(collage).collection("year")
                .document(year).collection("branch")
                .document(branch).collection("batch")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    arrayListBatch.clear();
                    arrayListBatch.add("select batch");
                    for (QueryDocumentSnapshot ds : Objects.requireNonNull(task.getResult())) {
                        arrayListBatch.add(ds.getId());
                    }
                    spinBatch.setEnabled(true);
                    arrayAdapterBatch.notifyDataSetChanged();
                } else {
                    Log.d("res", "Error getting documents: ", task.getException());
                }
            }
        });

    }

    private void saveAndFetch() {
        if (collage != null && year != null && branch != null && batch != null) {
            student.setCollage(collage);
            student.setYear(year);
            student.setBranch(branch);
            student.setBatch(batch);

            CommonMethod.saveStudentToFile(student,this);
            //update db data if login
            if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
                CommonMethod.updateStudentData(student, FirebaseAuth.getInstance().getUid());
            }

            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Choose data first", Toast.LENGTH_SHORT).show();
        }
    }


}
