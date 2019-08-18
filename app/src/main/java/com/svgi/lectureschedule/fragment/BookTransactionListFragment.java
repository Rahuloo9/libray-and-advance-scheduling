package com.svgi.lectureschedule.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.svgi.lectureschedule.R;
import com.svgi.lectureschedule.feature.MyBookRecyclerViewAdapter;
import com.svgi.lectureschedule.model.BookIssuedDetail;

import java.util.ArrayList;

public class BookTransactionListFragment extends Fragment {
    private MyBookRecyclerViewAdapter myBookRecyclerViewAdapter;
    private ArrayList<BookIssuedDetail> bookDetailArrayList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);
        RecyclerView listView = (RecyclerView) view;
        bookDetailArrayList = new ArrayList<>();
        myBookRecyclerViewAdapter = new MyBookRecyclerViewAdapter(bookDetailArrayList);
        listView.setAdapter(myBookRecyclerViewAdapter);
//        final RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300);
        listView.setLayoutManager(new GridLayoutManager(view.getContext(), 1));
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        getBookTransactionList();
        return view;
    }


    private void getBookTransactionList() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String UID = FirebaseAuth.getInstance().getUid();
            FirebaseFirestore.getInstance().collection("students").document(UID).collection("bookTransaction").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            Log.d("book", "onSuccess: == get in book list");
                            for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                                BookIssuedDetail bookIssuedDetail = ds.toObject(BookIssuedDetail.class);
                                bookDetailArrayList.add(bookIssuedDetail);
                                Log.d("book", "onSuccess: =============== " + bookIssuedDetail.getTitle());
                                myBookRecyclerViewAdapter.notifyDataSetChanged();
                            }

                        }
                    });
        }
    }

}
