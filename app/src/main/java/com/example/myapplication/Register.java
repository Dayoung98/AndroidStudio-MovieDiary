package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Register extends AppCompatActivity {
    private static String IP_ADDRESS="165.229.90.48";
    private static String TAG = "Register";
    String id,password,name,passwordchk;
    Button btnRegister,btnIdCheck;
    EditText editName,editId,editPassword,editPasswordChk;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnIdCheck = (Button)findViewById(R.id.btnIdCheck);
        editName = (EditText)findViewById(R.id.editName);
        editId = (EditText)findViewById(R.id.editId);
        editPassword = (EditText)findViewById(R.id.editPassword);
        editPasswordChk = (EditText)findViewById(R.id.editPasswordChk);
    }

    public void btn_Id_Check(View view){
        id = editId.getText().toString();
        try {
            IDCHECKDB task = new IDCHECKDB();
            String str = task.execute("http://" + IP_ADDRESS+ "/check_id.php", id).get();
            if (str.equals("Usable")) {
                //로그 메세지 확인하기
                Log.d(TAG, "아이디 확인하기");
                Toast toast = Toast.makeText(getApplicationContext(),"사용할 수 있는 아이디 입니다..",Toast.LENGTH_SHORT);
                toast.show();

            } else {
                Toast toast = Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG);
                toast.show();
                //정상완료안됐을때
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btn_Register(View view){
        id = editId.getText().toString();
        password = editPassword.getText().toString();
        passwordchk = editPasswordChk.getText().toString();

        if(password.equals(passwordchk)){
            try {
                RegisterDB task = new RegisterDB();
                String str = task.execute("http://" + IP_ADDRESS+ "/registerDiary.php", id, password,name).get();
                if (str.equals("SQL문 처리 성공")) {
                    //로그 메세지 확인하기
                    Log.d(TAG, "회원가입");
                    Toast toast = Toast.makeText(getApplicationContext(),"회원가입이 완료되었습니다.",Toast.LENGTH_SHORT);
                    toast.show();
                    finish();
                    Intent intent  = new Intent(Register.this,Login.class);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG);
                    toast.show();
                    //정상완료안됐을때
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(this,"비밀번호를 다시 확인하여 주세요", Toast.LENGTH_SHORT).show();
        }
    }

    public class RegisterDB extends AsyncTask<String,Void, String> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Register.this,
                    "Please Wait", null, true, true);
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "POST response  - " + result);
        }
        @Override
        protected String doInBackground(String ... params) {
            String serverURL = (String) params[0];
            String id = (String) params[1];
            String password = (String) params[2];
            String name = (String) params[3];

            String postParameters = "id=" + id + "&password=" + password + "&name=" + name;
            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "SignUp: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }
    public class IDCHECKDB extends AsyncTask<String,Void, String> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Register.this,
                    "Please Wait", null, true, true);
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "POST response  - " + result);
        }
        @Override
        protected String doInBackground(String ... params) {
            String serverURL = (String) params[0];
            String id = (String) params[1];

            String postParameters = "id=" + id;
            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();

            } catch (Exception e) {
                Log.d(TAG, "SignUp: Error ", e);
                return new String("Error: " + e.getMessage());
            }
        }
    }
}

