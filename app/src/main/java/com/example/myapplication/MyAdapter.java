package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    //데이터 배열 선언
    private ArrayList<menu3Item> mList;
    Context context;
    String url;

    public MyAdapter(Context context,ArrayList<menu3Item> mList){
        this.context = context;
        this.mList = mList;
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView_img;
        private TextView textView_title, textView_release, texView_director;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView_img = (ImageView) itemView.findViewById(R.id.imageView_img);
            textView_title = (TextView) itemView.findViewById(R.id.textView_title);
            textView_release = (TextView) itemView.findViewById(R.id.textView_release);
            texView_director = (TextView) itemView.findViewById(R.id.textView_director);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {//리사이클러 뷰 선택했을 때 할 활동
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        url = getItem(pos).getDetail_link();
                        System.out.println(getItem(pos).getDetail_link());

                        Fragment fragment = new Fragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("주소",url);
                        fragment.setArguments(bundle);
                        Intent intent = new Intent(context,MovieWebView.class);
                        intent.putExtra("주소",url);
                        context.startActivity(intent);

                        notifyItemChanged(pos);
                    }
                }
            });
        }

    }

    //생성자
    public MyAdapter(ArrayList<menu3Item> list) {
        this.mList = list;
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu3_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
        holder.textView_title.setText(String.valueOf(mList.get(position).getTitle()));
        holder.textView_release.setText(String.valueOf(mList.get(position).getRelease()));
        holder.texView_director.setText(String.valueOf(mList.get(position).getDirector()));
        GlideApp.with(holder.itemView).load(mList.get(position).getImg_url())
                .override(300,400)
                .into(holder.imageView_img);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public menu3Item getItem(int pos){
        return mList.get(pos);
    }

}