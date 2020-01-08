package com.example.myapplication.Retrofit;

import android.os.AsyncTask;
import java.io.File;
import retrofit2.Retrofit;

public class ImageUploadTask extends AsyncTask<String, Void, String> {

    private final String TAG = "ImageUploadTask2";
    private File file;

    private String id;
    private String title;
    private String content;
    private String genre;
    private String rating;

    private Retrofit retrofit;


    public ImageUploadTask(File file, String title, String content, String selectGenre, String rating, String id) {
        this.file = file;
        this.title = title;
        this.content = content;
        this.genre = selectGenre;
        this.rating = rating;
        this.id = id;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        retrofit = Retrofit2Client.getInstance();
    }

    @Override
    protected String doInBackground(String... strings) {

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

    }
}