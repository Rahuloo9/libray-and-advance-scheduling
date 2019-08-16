package com.svgi.lectureschedule.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;
import com.svgi.lectureschedule.R;
import com.svgi.lectureschedule.feature.CommonMethod;
import com.svgi.lectureschedule.feature.Student;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class IssueBookFragment extends Fragment implements ZXingScannerView.ResultHandler {
    private ZXingScannerView zXingScannerView;
    private EditText barCode;
    private Student student;
    private OnIssuedBookFragmentListener onIssuedBookFragmentListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.zxscanner_fragment, container, false);
        zXingScannerView = view.findViewById(R.id.zXingScannerView);
        ImageButton btnProceed = view.findViewById(R.id.btnProceed);
        barCode = view.findViewById(R.id.barCode);
        student = CommonMethod.loadStudentFromFile(getContext());
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBookRegDetail(barCode.getText().toString());
            }
        });
        if (onIssuedBookFragmentListener != null && FirebaseAuth.getInstance().getCurrentUser() == null) {
            onIssuedBookFragmentListener.onAuthNotFound();
        }

        return view;
    }

    @Override
    public void handleResult(Result result) {
        getBookRegDetail(result.getText());
        zXingScannerView.stopCamera();
    }

    private void getBookRegDetail(final String id) {
        FirebaseFirestore.getInstance().collection("collage").document(student.getCollage()).collection("other").
                document("library").collection("all-book").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    BookRegDetail bookRegDetail = documentSnapshot.toObject(BookRegDetail.class);
                    if (bookRegDetail.issuedBy == null || bookRegDetail.issuedBy.isEmpty()) {
                        getBookDetail(bookRegDetail.bid, id);
                    } else {
                        Toast.makeText(getContext(), "This book is already issued", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "no book targeting this code", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setOnIssuedBookFragmentListener(OnIssuedBookFragmentListener onIssuedBookFragmentListener) {
        this.onIssuedBookFragmentListener = onIssuedBookFragmentListener;
    }

    private void getBookDetail(String bid, final String id) {
        FirebaseFirestore.getInstance().collection("collage").document(student.getCollage()).collection("other").
                document("library").collection("book-detail").document(bid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    BookDetail bookDetail = documentSnapshot.toObject(BookDetail.class);
                    AlertDialog.Builder ab = new AlertDialog.Builder(getContext());
                    ab.setTitle("Confirm");
                    ab.setMessage("Do you want to issue,book " + bookDetail.name + " by " + bookDetail.author);
                    ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseFirestore.getInstance().collection("collage").document(student.getCollage()).collection("other").
                                    document("library").collection("all-book").document(id).update("issuedBy",
                                    FirebaseAuth.getInstance().getUid()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(), "Book Issued Successfully", Toast.LENGTH_SHORT).show();
                                    if (onIssuedBookFragmentListener != null)
                                        onIssuedBookFragmentListener.onSuccess();
                                }
                            });
                        }
                    });
                    ab.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    ab.create().show();
                } else {
                    Toast.makeText(getContext(), "Book detail not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public interface OnIssuedBookFragmentListener {
        void onSuccess();

        void onAuthNotFound();
    }

}

class BookRegDetail {
    String bid, issuedBy;

    public BookRegDetail() {
    }

    public BookRegDetail(String bid, String issuedBy) {
        this.bid = bid;
        this.issuedBy = issuedBy;
    }
}

class BookDetail {
    String name, author;
    int total, available;

    public BookDetail() {
    }
}
