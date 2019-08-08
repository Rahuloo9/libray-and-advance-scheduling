package com.svgi.lectureschedule.feature;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.svgi.lectureschedule.MainActivity;
import com.svgi.lectureschedule.activity.HomeActivity;
import com.svgi.lectureschedule.activity.SetClassDataActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.annotation.Nullable;

public class CommonMethod {
    public static void fetchStudentData(String uid, final Context context) {
        FirebaseFirestore.getInstance().collection("students").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    Student student = documentSnapshot.toObject(Student.class);
                    saveStudentToFile(student,context);
                   // context.startActivity(new Intent(context, MainActivity.class));
                }else{
                    Toast.makeText(context,"Select Institue",Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, SetClassDataActivity.class));
                }
            }
        });
    }

    public static void updateStudentData(Student student, String uid) {
        FirebaseFirestore.getInstance().collection("students").document(uid).set(student);
    }
    public static void saveStudentToFile(Student student,Context context){
        try {
            File file = new File(context.getFilesDir(), "student.txt");
            if (!file.exists()) file.createNewFile();
            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream o = new ObjectOutputStream(f);

            // Write objects to file
            o.writeObject(student);
            Log.d("login", "onEvent: ================================== "+student.getCollage());
            o.close();
            f.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static Student loadStudentFromFile(Context context) {
        Student student = null;
        try {
            FileInputStream fi = new FileInputStream(new File(context.getFilesDir(), "student.txt"));
            ObjectInputStream oi = new ObjectInputStream(fi);

            student = (Student) oi.readObject();
            Log.d("login", "onEvent: ================================== student "+student.getCollage());

            oi.close();
            fi.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (student==null)student=new Student();
        return student;
    }
}
