package com.svgi.lectureschedule.feature;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.svgi.lectureschedule.R;
import com.svgi.lectureschedule.model.BookIssuedDetail;

import java.util.ArrayList;

public class MyBookRecyclerViewAdapter extends RecyclerView.Adapter<MyBookRecyclerViewAdapter.ViewHolder> {

    private ArrayList<BookIssuedDetail> bookIssuedDetailArrayList;

    public MyBookRecyclerViewAdapter(ArrayList<BookIssuedDetail> bookIssuedDetailArrayList) {
        this.bookIssuedDetailArrayList = bookIssuedDetailArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LinearLayout.inflate(parent.getContext(), R.layout.fragment_book_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookIssuedDetail bookIssuedDetail = bookIssuedDetailArrayList.get(position);
        holder.title.setText(bookIssuedDetail.getTitle());
        holder.author.setText("By " + bookIssuedDetail.getAuthor());
        holder.issuedDate.setText(bookIssuedDetail.getIssueDate().toString());
        if (bookIssuedDetail.getSubmitDate().equals(bookIssuedDetail.getIssueDate())) {
            holder.submitDate.setText("Not Submitted");
            holder.submitDate.setTextColor(Color.RED);
        } else {
            holder.submitDate.setText(bookIssuedDetail.getSubmitDate().toString());
        }
    }

    @Override
    public int getItemCount() {
        return bookIssuedDetailArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, issuedDate, submitDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
            author = itemView.findViewById(R.id.item_author);
            issuedDate = itemView.findViewById(R.id.item_issued_date);
            submitDate = itemView.findViewById(R.id.item_submit_date);
        }
    }


}
