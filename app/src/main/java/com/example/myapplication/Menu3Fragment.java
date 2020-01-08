package com.example.myapplication;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Menu3Fragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<menu3Item> list = new ArrayList();

    public static Menu3Fragment newInstance() {
        return new Menu3Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu3,container,false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);

        new Description().execute();

        return view;
    }

  public class Description extends AsyncTask<Void,Void,Void>{
        ProgressDialog progressDialog;
      @Override
      protected void onPreExecute() {
          super.onPreExecute();

          progressDialog = ProgressDialog.show(getContext(),
                  "Please Wait", null, true, true);
      }

      @Override
      protected void onPostExecute(Void result) {
          //super.onPostExecute(result);
          MyAdapter myAdapter = new MyAdapter(getContext(),list); //어레이 리스트 어뎁터에 연결해주기
          LinearLayoutManager layoutManager = new LinearLayoutManager((MainActivity)getActivity());
          layoutManager.setOrientation(RecyclerView.VERTICAL);
          recyclerView.setLayoutManager(layoutManager);
          recyclerView.setAdapter(myAdapter);

          progressDialog.dismiss();
      }

      @Override
      protected Void doInBackground(Void... params) {//크롤링 해주기
          try{
              Document document = Jsoup.connect("https://movie.naver.com/movie/running/current.nhn").get();
              Elements dataSize = document.select("ul[class=lst_detail_t1]").select("li");
              int elementSize = dataSize.size();

              for(Element element : dataSize){
                  String my_title = element.select("li dt[class=tit] a").text();
                  String my_link = element.select("li div[class=thumb] a").attr("href");
                  String my_imgUrl = element.select("li div[class=thumb] a img").attr("src");
                  Element rElem = element.select("dl[class=info_txt1] dt").next().first();
                  String my_release = rElem.select("dd").text();
                  Element dElem = element.select("dt[class=tit_t2]").next().first();
                  String my_director = "감독: " + dElem.select("a").text();
                  list.add(new menu3Item(my_title, my_imgUrl, my_link, my_release, my_director));
              }
              //Log.d("리스트에 뭐잇는지","list ::"+dataSize);
          }catch (IOException e){
              e.printStackTrace();
          }
          return null;
      }
  }


}
