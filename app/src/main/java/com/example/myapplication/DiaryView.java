package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


public class DiaryView extends AppCompatActivity {
    ImageView imageView;
    TextView title,content1,write_date,genre;
    String gettitle,getcontent1,getwrite_date,getgenre,imgUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_view);

        imageView = (ImageView)findViewById(R.id.diary_imageView);
        title = (TextView)findViewById(R.id.diary_title);
        content1 = (TextView)findViewById(R.id.diary_content);
        write_date = (TextView)findViewById(R.id.diary_write_date);
        genre = (TextView)findViewById(R.id.diary_genre);

        Intent getData = getIntent();
        gettitle = getData.getStringExtra("title");
        getcontent1 = getData.getStringExtra("content");
        getwrite_date = getData.getStringExtra("write_date");
        getgenre = getData.getStringExtra("genre");
        imgUrl = getData.getStringExtra("imgUrl");
        Log.d("이미지 주소",imgUrl);

        title.setText(gettitle);
        content1.setText(getcontent1);
        genre.setText(getgenre);
        write_date.setText(getwrite_date);
        Glide.with(this).load(imgUrl).override(500,600).into(imageView);
        getIntent().removeExtra("title");
        getIntent().removeExtra("content");
        getIntent().removeExtra("write_date");
        getIntent().removeExtra("genre");
        getIntent().removeExtra("imgUrl");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
