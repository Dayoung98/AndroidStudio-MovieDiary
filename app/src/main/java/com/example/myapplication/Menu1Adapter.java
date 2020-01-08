package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Menu1Adapter extends RecyclerView.Adapter<Menu1Adapter.ViewHolder> {


    private ArrayList<menu1Item> mList;
    Context context;
    String user_id;


    public Menu1Adapter(Context context,ArrayList<menu1Item> mList,String user_id){
        this.context = context;
        this.mList = mList;
        this.user_id = user_id;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private TextView textView_title,textView_genre,textView_sysdate,textView_rating;

        public ViewHolder(View itemView){
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.imageView_img);
            textView_title = (TextView)itemView.findViewById(R.id.textView_title);
            textView_genre= (TextView)itemView.findViewById(R.id.textView_genre);
            textView_sysdate= (TextView)itemView.findViewById(R.id.textView_sysdate);
            textView_rating=(TextView)itemView.findViewById(R.id.textView_rating);
        }
    }

    @NonNull
    @Override
    public Menu1Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu1_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Menu1Adapter.ViewHolder holder, int position) {
        holder.textView_title.setText(String.valueOf(mList.get(position).getTitle()));
        holder.textView_genre.setText(String.valueOf(mList.get(position).getGenre()));
        holder.textView_rating.setText(String.valueOf(mList.get(position).getRating()));
        holder.textView_sysdate.setText(String.valueOf(mList.get(position).getSysdate()));
        GlideApp.with(holder.itemView).load(mList.get(position).getImg_url())
                .override(300,400)
                .into(holder.imageView);

        final int pos = position;

        //아이템 클릭 리스너 달기
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pos != RecyclerView.NO_POSITION){
                    //일기에 대한 디테일 fragment 보여주기
                    String title = getItem(pos).getTitle();
                    String content = getItem(pos).getContent();
                    String write_date = getItem(pos).getSysdate();
                    String genre = getItem(pos).getGenre();
                    String imgUrl = getItem(pos).getImg_url();

                    Intent intent = new Intent(context,DiaryView.class);
                    intent.putExtra("title",title);
                    intent.putExtra("content",content);
                    intent.putExtra("write_date",write_date);
                    intent.putExtra("genre",genre);
                    intent.putExtra("imgUrl",imgUrl);

                    context.startActivity(intent);
                    notifyItemChanged(pos);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //길게 누르면 삭제할 수 있게?
                if(pos != RecyclerView.NO_POSITION){
                    //dialog-->delete
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("삭제");
                    builder.setMessage("삭제하시겠습니까?");
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //데이터 베이스에서 삭제하기
                            String title = getItem(pos).getTitle();
                            String content = getItem(pos).getContent();
                            new DeleteFromDB().execute("http://165.229.90.48/delete_data.php",user_id,title,content);
                            mList.remove(getItem(pos));
                            Menu1Adapter.this.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //가만히 나두기
                        }
                    });
                    builder.show();
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public menu1Item getItem(int pos){
        return mList.get(pos);
    }

    private class DeleteFromDB extends AsyncTask<String,Void,String>{
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context,"WAIT",null,true,true);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();
            Log.d("POST-RESPONSE = ",s);
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            String id = params[1];
            String title = params[2];
            String content = params[3];
            String postParameters = "id=" + id +"&title="+title +"&content="+content;

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
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
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
}
