package com.svgi.lectureschedule.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.svgi.lectureschedule.R;
import com.svgi.lectureschedule.feature.CommonMethod;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin, btnForget, btnSkip;
    private EditText email, pass;
    private FirebaseAuth firebaseAuth;
    private String TAG = "login";
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        initUI();
        pb.setVisibility(View.GONE);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pb.setVisibility(View.VISIBLE);
                String username = email.getText().toString().trim();
                String password = pass.getText().toString().trim();
                if (!username.isEmpty() && !password.isEmpty()) {
                    firebaseAuth.signInWithEmailAndPassword(username, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        pb.setVisibility(View.GONE);
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");
                                        CommonMethod.fetchStudentData(firebaseAuth.getUid(), LoginActivity.this, null);
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        pb.setVisibility(View.GONE);

                                    }
                                }
                            });
                }
            }
        });

        btnForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!email.getText().toString().isEmpty()) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(LoginActivity.this, "Email sent to : " + email.getText().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    email.setError("enter valid email");
                }
            }
        });
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SetClassDataActivity.class));
            }
        });
    }

    private void initUI() {
        btnLogin = findViewById(R.id.btnLogin);
        email = findViewById(R.id.editEmail);
        pass = findViewById(R.id.editPassword);
        btnForget = findViewById(R.id.btnForgot);
        btnSkip = findViewById(R.id.btnSkip);
        pb = findViewById(R.id.pb);
    }


}
