package com.example.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.Retrofit.ImageUpload;
import com.example.myapplication.Retrofit.ImageUploadTask;
import com.example.myapplication.Retrofit.Retrofit2Client;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class Menu2Fragment extends Fragment {
    private static String IP_ADDRESS = "165.229.90.48";
    private static String TAG = "Write DIARY";
    private static final int PICK_FROM_ALBUM = 1;
    private boolean isPermission = true;
    private File tempFile;
    private String FileName;

    private Retrofit retrofit;

    EditText editTitle, editContent;
    Button btnGetImage, btnGenre, btnWrite;
    ImageView imageView;
    RatingBar ratingBar;
    float RATING = 0;
    String id;

    final CharSequence[] genre = {"코미디", "어드벤쳐", "액션", "드라마", "서스펜스/스릴러", "로맨틱 코미디", "호러", "뮤지컬", "다큐멘터리", "etc"};
    int select;
    String selectGenre = "";

    public static Menu2Fragment newInstance() {
        return new Menu2Fragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getActivity() != null && getActivity() instanceof MainActivity)
            id = ((MainActivity) getActivity()).getId();
        Log.d("아이디", id);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu2, container, false);


        retrofit = Retrofit2Client.getInstance();


        editTitle = (EditText) view.findViewById(R.id.editTitle);
        editContent = (EditText) view.findViewById(R.id.editContent);
        btnGenre = (Button) view.findViewById(R.id.btnGenre);
        btnGetImage = (Button) view.findViewById(R.id.getImage);


        btnWrite = (Button) view.findViewById(R.id.btnWrite);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        imageView = (ImageView) view.findViewById(R.id.imageView);

        tedPermission();

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                RATING = rating;
            }
        });

        btnGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder shopDialog = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Dialog_Alert);
                shopDialog.setTitle("장르를 골라주세요").setSingleChoiceItems(genre, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("몇번쨰?", String.valueOf(which));
                        select = which;
                        selectGenre = genre[which].toString();
                        Log.d("장르??",selectGenre);
                    }
                }).setNeutralButton("선택", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setCancelable(false).show();
            }
        });

        btnGetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPermission) gotoAlbum();
                else
                    Toast.makeText(view.getContext(), getResources().getString(R.string.permission_2), Toast.LENGTH_SHORT).show();
            }
        });

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitle.getText().toString();
                String content = editContent.getText().toString();
                if (title.equals("") || content.equals("")) {
                    Toast.makeText(getContext(), "내용 or 제목을 입력하여 주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    //디비에 저장하기 RatingBar도 저장해야함
                    try {
                        //ImageUploadTask imageUpload = new ImageUploadTask(tempFile, title, content, selectGenre, String.valueOf(RATING), id);
                        //String str = imageUpload.execute().get();
                        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), tempFile);
                        MultipartBody.Part body = MultipartBody.Part.createFormData("image", tempFile.getName(), requestFile);
                        RequestBody requestTitle = RequestBody.create(okhttp3.MultipartBody.FORM, title);
                        RequestBody requestContent = RequestBody.create(okhttp3.MultipartBody.FORM, content);
                        RequestBody requestGenre = RequestBody.create(okhttp3.MultipartBody.FORM, selectGenre);
                        RequestBody requestRating = RequestBody.create(okhttp3.MultipartBody.FORM, String.valueOf(RATING));
                        RequestBody requestId = RequestBody.create(okhttp3.MultipartBody.FORM, id);

                        retrofit.create(ImageUpload.class).uploadImage(body, requestId, requestTitle, requestGenre, requestRating, requestContent).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                                Log.d(TAG, "onResponse: " + Integer.toString(response.code()));
                                try {
                                    String result = response.body().string();
                                    Log.d(TAG, result);
                                } catch (IOException e) {
                                    Log.d(TAG, "onResponse: ");
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.d(TAG, "onFailure: " + t.getMessage());
                            }

                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                editTitle.setText(null);
                editContent.setText(null);
                ratingBar.setRating(0);
                imageView.setImageBitmap(null);
            }
        });
        return view;
    }

    private void tedPermission() {
            PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //권한 요청 성공
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                //권한 요청 실패
            }
        };
        TedPermission.with(getActivity())
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA).check();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //갤러리에서 이미지 가져온 뒤에 할 행동
        if (resultCode != getActivity().RESULT_OK) {
            Toast.makeText(getActivity(), "취소되었습니다.", Toast.LENGTH_SHORT).show();

            if (tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        Log.e("파일 삭제하기", tempFile.getAbsolutePath() + "삭제성공!!");
                        tempFile = null;
                    }
                }
            }
            return;
        }
        if (requestCode == PICK_FROM_ALBUM) {
            Uri photoUrl = data.getData();
            Cursor cursor = null;

            try {
                String[] proj = {MediaStore.Images.Media.DATA};
                assert photoUrl != null;
                cursor = getActivity().getContentResolver().query(photoUrl, proj, null, null, null);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);//몇번째꺼 가지고 오는지?
                cursor.moveToFirst();

                tempFile = new File(cursor.getString(column_index));
                Log.d("이미지 파일 이름!!!!!!", cursor.getString(column_index));
                String filePath = cursor.getString(column_index);
                String[] array = filePath.split("/");
                for (int i = 0; i < array.length; i++)
                    System.out.println("이름이름" + array[i]); //5번째거 가지고 오기
                FileName = array[5];
                Log.d("FileName", FileName);
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }
        setImage();
    }

    private void setImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap orignbm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        Log.d("이미지 경로 !!!!!!!", tempFile.getAbsolutePath());
        ImageView imageView = getActivity().findViewById(R.id.imageView);
        imageView.setImageBitmap(orignbm); //이미지 비트맵 형식으로 저장하기
        imageView.getLayoutParams().height = 150;
        imageView.getLayoutParams().width = 100;
        imageView.requestLayout();
    }

    private void gotoAlbum() { //갤러리 불러오기
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    public class WriteDB extends AsyncTask<String, Void, String> {
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

            progressDialog.dismiss();
            Log.d(TAG, "POST response  - " + result);
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = (String) params[0];
            String id = (String) params[1];
            String title = (String) params[2];
            String content = (String) params[3];
            String genre = (String) params[4];
            String rating = (String) params[5];

            String postParameters = "id=" + id + "&title=" + title + "&content=" + content + "&genre=" + genre + "&rating=" + rating;
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
