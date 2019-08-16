package com.svgi.lectureschedule.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.svgi.lectureschedule.R;
import com.svgi.lectureschedule.feature.CommonMethod;
import com.svgi.lectureschedule.feature.Student;

import java.util.ArrayList;

public class SearchBookFragments extends Fragment {
    ListView resList;
    EditText editKeyWord;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    Student student;
    ArrayList<BookDetail> bookDetailArrayList;
    OnSearchBookListener onSearchBookListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_book_fragment, container, false);
        if (onSearchBookListener != null && FirebaseAuth.getInstance().getCurrentUser() == null)
            onSearchBookListener.onAuthNotFound();
        else {

            resList = view.findViewById(R.id.resultList);
            editKeyWord = view.findViewById(R.id.editKeyWord);
            arrayList = new ArrayList<>();
            bookDetailArrayList = new ArrayList<>();
            arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, arrayList);
            resList.setAdapter(arrayAdapter);
            student = CommonMethod.loadStudentFromFile(getContext());
            resList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(getContext(), arrayList.get(i), Toast.LENGTH_SHORT).show();
                }
            });

            editKeyWord.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String key = editKeyWord.getText().toString();
                    prepareSearchList(key);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            getBookDetailList();
        }
        return view;
    }

    private void getBookDetailList() {
        FirebaseFirestore.getInstance().collection("collage").document(student.getCollage()).collection("other").
                document("library").collection("book-detail").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                    BookDetail bookDetail = ds.toObject(BookDetail.class);
                    Log.d("search", "onSuccess: book " + bookDetail.name);
                    bookDetailArrayList.add(bookDetail);
                }
                prepareSearchList("");
            }
        });
    }

    private void prepareSearchList(String key) {
        arrayList.clear();
        Log.d("search", "onKey: key ========= " + key);
        for (BookDetail bookDetail : bookDetailArrayList) {
            if (bookDetail.name.toLowerCase().contains(key.toLowerCase()) || bookDetail.author.toLowerCase().contains(key.toLowerCase())) {
                arrayList.add(bookDetail.name);
                arrayAdapter.notifyDataSetChanged();
            }
        }
        arrayAdapter.notifyDataSetChanged();

    }

    public void setOnSearchBookListener(OnSearchBookListener onSearchBookListener) {
        this.onSearchBookListener = onSearchBookListener;
    }

    public interface OnSearchBookListener {
        void onAuthNotFound();
    }
}
