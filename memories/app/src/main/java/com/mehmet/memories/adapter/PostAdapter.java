package com.mehmet.memories.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mehmet.memories.databinding.RecyclerRowBinding;
import com.mehmet.memories.model.Post;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

//1. extends RecyclerView.adapter and add PostHolder
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    //4.
    ArrayList<Post> postArrayList;

    public PostAdapter(ArrayList<Post> postArrayList) {
        this.postArrayList = postArrayList;
    }

    //3. create methods of PostAdapter
    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new PostHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        holder.recyclerRowBinding.txtRecyclerViewUserName.setText(postArrayList.get(position).email);
        holder.recyclerRowBinding.txtRecyclerViewComment.setText(postArrayList.get(position).comment);
        Picasso.get().load(postArrayList.get(position).downloadUrl).into(holder.recyclerRowBinding.imageViewRecyclerViewImage);
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();//5.
    }

        //2. create this and its constractur
    class PostHolder extends RecyclerView.ViewHolder{

        RecyclerRowBinding recyclerRowBinding;

        public PostHolder(RecyclerRowBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;
        }
    }
}
