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

public class Login extends AppCompatActivity {
    private String TAG = "로그인화면 확인";
    private static String IP_ADDRESS = "165.229.90.48";
    Button btnLogin,btnRegister;
    String id,password;
    EditText editId,editPwd;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnRegister = (Button)findViewById(R.id.btnRegister);
        editId = (EditText)findViewById(R.id.editId);
        editPwd = (EditText)findViewById(R.id.editPwd);
    }

    public void btn_Login(View view){
        id = editId.getText().toString();
        password = editPwd.getText().toString();

        try {
            LoginDB task = new LoginDB();
            String str = task.execute("http://" + IP_ADDRESS+ "/loginDiary.php", id, password).get();

            if (str.equals("success")) {
                Toast toast = Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_LONG);
                toast.show();
                finish();
                Intent intent = new Intent(Login.this,MainActivity.class);
                intent.putExtra("아이디",id);
                editId.setText("");
                editPwd.setText("");
                startActivity(intent);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 다시 확인하세용", Toast.LENGTH_LONG);
                toast.show();
                //정상완료안됬을 때
            }
        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    public void btn_Register(View view){
        Intent intent = new Intent(Login.this,Register.class);
        startActivity(intent);
    }

    public class LoginDB extends AsyncTask<String,Void, String> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(Login.this,
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
            String pw = (String) params[2];

            String postParameters = "id=" + id + "&pw=" + pw;
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
