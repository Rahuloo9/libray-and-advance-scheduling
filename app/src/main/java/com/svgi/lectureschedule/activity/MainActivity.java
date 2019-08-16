package com.svgi.lectureschedule.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.svgi.lectureschedule.R;
import com.svgi.lectureschedule.feature.CommonMethod;
import com.svgi.lectureschedule.feature.Student;
import com.svgi.lectureschedule.fragment.IssueBookFragment;
import com.svgi.lectureschedule.fragment.SearchBookFragments;
import com.svgi.lectureschedule.fragment.TimeTableProvider;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        TimeTableProvider.OnTimeTableProviderListner {

    private FirebaseAuth auth;
    private NavigationView navigationView;
    private Student student;
    private DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        showTimeTableFragment();


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (auth.getCurrentUser() == null) {
            navigationView.getMenu().findItem(R.id.logout).setVisible(false);
            navigationView.getMenu().findItem(R.id.login).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.logout).setVisible(true);
            navigationView.getMenu().findItem(R.id.login).setVisible(false);
            student = CommonMethod.loadStudentFromFile(getApplicationContext());
            setStudentProfile();
        }
    }

    private void setStudentProfile() {
        View view = navigationView.getHeaderView(0);
        TextView name = view.findViewById(R.id.studentName);
        TextView email = view.findViewById(R.id.studentEmail);
        name.setText(student.getName());
        email.setText(student.getEmail());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            showTimeTableFragment();
        } else if (id == R.id.nav_issue_book) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        101);
            } else {
                shoeIssueBookFragment();
            }
        } else if (id == R.id.nav_search_book) {
            showBookSearchFragment();
        } else if (id == R.id.logout) {
            auth.signOut();
            onResume();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.login) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDataNotFound() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void showTimeTableFragment() {
        TimeTableProvider timeTableProvider = new TimeTableProvider();
        timeTableProvider.setOnTimeTableProviderListner(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentCon, timeTableProvider)
                .setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out).commit();

    }

    private void showBookSearchFragment() {
        SearchBookFragments searchBookFragments = new SearchBookFragments();
        searchBookFragments.setOnSearchBookListener(new SearchBookFragments.OnSearchBookListener() {
            @Override
            public void onAuthNotFound() {
                showTimeTableFragment();
                Toast.makeText(getApplicationContext(), "Login to search for book", Toast.LENGTH_SHORT).show();
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentCon, searchBookFragments)
                .setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out).commit();

    }

    private void shoeIssueBookFragment() {
        IssueBookFragment issueBookFragment = new IssueBookFragment();
        issueBookFragment.setOnIssuedBookFragmentListener(new IssueBookFragment.OnIssuedBookFragmentListener() {
            @Override
            public void onSuccess() {
                CommonMethod.fetchStudentData(auth.getUid(), getApplicationContext(), null);
            }

            @Override
            public void onAuthNotFound() {
                showTimeTableFragment();
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentCon, issueBookFragment)
                .setCustomAnimations(R.anim.fragment_in, R.anim.fragment_out).commit();

    }
}
