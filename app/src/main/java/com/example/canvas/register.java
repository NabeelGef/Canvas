package com.example.canvas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import models.API;
import models.User;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Url;

public class register extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    EditText name , email , password ,confirm_password , address;
    ImageView photo;
    ProgressBar loading;
    Button signup;
    SharedPreferences sharedPreferences;
    TextView login;
    Bundle bundle;
    String encodeImage;
    public static final int GET_FROM_GALLERY = 2;
    File url_image_upload = null;
    Uri selectedImage = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        init();
        SignUp();
        LOGIN();
        SELECTIMAGE();
    }

    private void SELECTIMAGE() {
    photo.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            upload_Image();
            }
    });
    }

    private void upload_Image() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent,GET_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            selectedImage = data.getData();
            Bitmap bitmap = null;
            String filePath = "";
            InputStream stream;
            try {
                stream = getContentResolver().openInputStream(selectedImage);
                Bitmap realImage = BitmapFactory.decodeStream(stream);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                realImage.compress(Bitmap.CompressFormat.JPEG,100,baos);
                byte[] bytes = baos.toByteArray();
                encodeImage = Base64.encodeToString(bytes,Base64.DEFAULT);
                /*SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Image",encodeImage);
                editor.apply();
                */
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                photo.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private String getPath(Uri selectedImage) {
        String path = "";
        if(selectedImage==null)
            return path;
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    private void LOGIN() {
    login.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(),Login.class);
            finish();
            startActivity(intent);
        }
    });
    }
    private void SignUp() {
    signup.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(checkInput()){
                requestPermission();
             }
        }
    });
    }

    private void PUT_INTO_DB() {
        loading.setVisibility(View.VISIBLE);
    String Email = email.getText().toString();
    String Name = name.getText().toString();
    String Password = password.getText().toString();
    String Address = address.getText().toString();

    MultipartBody.Part image = null;
        if(selectedImage!=null) {
            File file = new File(getPath(selectedImage));
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            image = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
        }
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        API api =retrofit.create(API.class);
        Call<ResponseBody> call=api.posting(Name,Email,Password,Address,image,"admin","");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String state = response.body().string();
                    System.out.println("State : " + state);
                    if(state.isEmpty()){
                       Toast.makeText(getApplicationContext(),response.body().string(),Toast.LENGTH_LONG).show();
                       loading.setVisibility(View.GONE);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Success Register",Toast.LENGTH_LONG).show();
                        loading.setVisibility(View.GONE);
                        Intent intent = new Intent(getApplicationContext(),Login.class);
                        startActivity(intent);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
                System.out.println("Error in " + t.getMessage());
                loading.setVisibility(View.GONE);
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 786 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            PUT_INTO_DB();
        }
    }
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 786);
        } else {
            System.out.println("UPLOADING");
            PUT_INTO_DB();
        }
    }
    private boolean checkInput() {
    if(name.getText().toString().isEmpty()||email.getText().toString().isEmpty()||password.getText().toString().isEmpty()
    ||confirm_password.getText().toString().isEmpty()||address.getText().toString().isEmpty()){
        Toast.makeText(getApplicationContext(),"All Field required!!",Toast.LENGTH_LONG).show();
        return false;
       }
    else if (!confirm_password.getText().toString().equals(password.getText().toString()))
    {
        Toast.makeText(getApplicationContext(),"password is Wrong",Toast.LENGTH_LONG).show();
        return false;
    }
    else if(password.getText().toString().length()<8){
        Toast.makeText(getApplicationContext(),"The password is Weak",Toast.LENGTH_LONG).show();
        return false;
    }
    return true;
    }
    private void init(){
        name = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirmPassword);
        address = findViewById(R.id.address);
        photo = findViewById(R.id.avatar);
        signup = findViewById(R.id.signup);
        loading = findViewById(R.id.progress_circular);
        login = findViewById(R.id.Goto_Login);
        bundle = new Bundle();
        sharedPreferences = getSharedPreferences("SaveMyData",Context.MODE_PRIVATE);
    }
}