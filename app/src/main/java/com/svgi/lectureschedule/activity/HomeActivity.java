package com.svgi.lectureschedule.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.svgi.lectureschedule.MainActivity;
import com.svgi.lectureschedule.R;
import com.svgi.lectureschedule.feature.CommonMethod;
import com.svgi.lectureschedule.feature.Student;
import com.svgi.lectureschedule.feature.TimeTableProvider;

import java.io.File;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity
        implements TimeTableProvider.OnTimeTableDataListener{


    private TimeTableProvider timeTableProvider;
    private TextView currentLec, nextLec, dayTitle;
    private ImageButton btnPrev, btnNext;
    private ListView dayList;
    private LinearLayout curLinear, nextLinear;
    private ArrayList<String> lecList;
    private ArrayAdapter<String> arrayAdapter;
    private FirebaseAuth auth;
    private Student student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FirebaseApp.initializeApp(getApplicationContext());
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
           startActivity(new Intent(this, MainActivity.class));
           finish();
        }
        File file = new File(getFilesDir(), "student.txt");
        if (file.exists()&&auth.getCurrentUser()==null) {
            student = CommonMethod.loadStudentFromFile(this);
            initUI();
        } else {
            if (auth.getCurrentUser() == null) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                CommonMethod.fetchStudentData(auth.getUid(), this);
                //onCreate(savedInstanceState);
            }
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.Login) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")


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
        timeTableProvider = new TimeTableProvider(this, student);
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

        final ScrollView mScrollView = findViewById(R.id.scroll);
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
        if (student != null) timeTableProvider.inValidate();
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
