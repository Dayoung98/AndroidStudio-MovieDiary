package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Menu1Fragment extends Fragment {
    private static String IP_ADDRESS = "165.229.90.48";
    private RecyclerView recyclerView;
    private ArrayList<menu1Item> list = new ArrayList();
    private Menu1Adapter menu1Adapter;
    private String mJsonString;
    private String user_id; //사용자 아이디

    String GENRE_ITEM = "", RATING_ITEM = ""; //장르랑 별점 저장
    Spinner spinner_genre, spinner_rating;

    public static Menu1Fragment newInstance() {
        return new Menu1Fragment();
    }

    @Override
    public void onResume() {
        super.onResume();

        //if (menu1Adapter != null) {
        //    list.clear();
        //    menu1Adapter = new Menu1Adapter(getContext(), list, user_id);
        //    new getGenreData().execute("http://" + IP_ADDRESS + "/getDiary.php", user_id, GENRE_ITEM, RATING_ITEM);//안불러옴
        //}
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getActivity() != null && getActivity() instanceof MainActivity)
            user_id = ((MainActivity) getActivity()).getId();
        Log.d("아이디", user_id);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu1, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        spinner_genre = (Spinner) view.findViewById(R.id.GenreSpinner);
        spinner_rating = (Spinner) view.findViewById(R.id.RatingSpinner);

        spinner_rating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                list.clear();
                RATING_ITEM = String.valueOf(position);
                //Log.d("GENRE_ITEM ::",GENRE_ITEM);
                Log.d("RATING_ITEM :: ", RATING_ITEM);
                Log.d("USER_ID", user_id);

                Log.d("별점 높은 순서 시작 ::", RATING_ITEM);
                menu1Adapter = new Menu1Adapter(getActivity(), list, user_id);
                new getGenreData().execute("http://" + IP_ADDRESS + "/getDiary.php", user_id, GENRE_ITEM, RATING_ITEM);//안불러옴
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner_genre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                list.clear();
                GENRE_ITEM = adapterView.getItemAtPosition(i).toString();

                Log.d("별점 높은 순서 시작 ::", RATING_ITEM);
                menu1Adapter = new Menu1Adapter(getActivity(), list, user_id);
                new getGenreData().execute("http://" + IP_ADDRESS + "/getDiary.php", user_id, GENRE_ITEM, RATING_ITEM);//안불러옴

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    //genre입력받은거 가지고 오기
    public class getGenreData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getContext(),
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                System.out.println("ERROR");
            } else {
                mJsonString = result;
                Log.d("POSTEXECUTE_RESULT", result);
                setResult();
            }
            LinearLayoutManager layoutManager = new LinearLayoutManager((MainActivity) getActivity());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(menu1Adapter);
            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... params) { //php 연결하기
            String serverURL = params[0];
            String id = params[1];
            String genre = params[2];
            String sort = params[3];
            String postParameters = "id=" + id + "&genre=" + genre + "&sort=" + sort;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private void setResult() {
        String TAG_JSON = "Diary";
        String TAG_TITLE = "title";
        String TAG_CONTENT = "content";
        String TAG_GENRE = "genre";
        String TAG_RATING = "rating";
        String TAG_SYSDATE = "write_date";
        String TAG_IMAGE = "image";
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            Log.d("디비 시작", mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            JSONObject item;

            for (int i = 0; i < jsonArray.length(); i++) {
                item = jsonArray.getJSONObject(i);

                String title = item.getString(TAG_TITLE);
                String content = item.getString(TAG_CONTENT);
                String genre = item.getString(TAG_GENRE);
                String rating = item.getString(TAG_RATING);
                String sysdate = item.getString(TAG_SYSDATE);
                String image = item.getString(TAG_IMAGE);
                String realPath = "http://" + IP_ADDRESS + "/image/" + image;

                menu1Item temp = new menu1Item();
                temp.setTitle(title);
                temp.setContent(content);
                temp.setGenre(genre);
                temp.setRating(rating);
                temp.setSysdate(sysdate);
                temp.setImg_url(realPath);

                list.add(temp);
                menu1Adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }
    }
}
