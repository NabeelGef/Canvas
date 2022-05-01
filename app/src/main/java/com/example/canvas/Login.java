package com.example.canvas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import models.API;
import models.LogWithBody;
import models.User;
import models.Log_in;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {
EditText email , password ;
Button login , signup;
Bundle bundle;
SharedPreferences sharedPreferences;
static String Token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        Login();
        SIGNUP();
    }

    private void SIGNUP() {
    signup.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(),register.class);
            finish();
            startActivity(intent);
        }
    });
    }
    private void init(){
        email = findViewById(R.id.emailLogin);
        password = findViewById(R.id.passwordLogin);
        login = findViewById(R.id.Login);
        signup = findViewById(R.id.Sign_up);
        bundle = new Bundle();
        sharedPreferences = getSharedPreferences("SaveMyData", Context.MODE_PRIVATE);
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful()){
                    Log.w("Error : " ,  task.getException());
                    return;
                }
                Token = task.getResult();
            }
        });
    }
    private  void Login(){
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(checkUp()){
                  String Email = email.getText().toString();
                  String Password = password.getText().toString();
                  LogWithBody log = new LogWithBody(Email,Password,Token);
                  Gson gson = new GsonBuilder().setLenient().create();
                  Retrofit retrofit = new Retrofit.Builder().baseUrl(Constant.URL)
                          .addConverterFactory(GsonConverterFactory.create(gson)).build();
                  API api = retrofit.create(API.class);
                  Call<Log_in> call = api.getingUsers(log);
                  call.enqueue(new Callback<Log_in>() {
                      @Override
                      public void onResponse(Call<Log_in> call, Response<Log_in> response) {
                          if(response.isSuccessful()) {
                              Toast.makeText(getApplicationContext(), "Success Login", Toast.LENGTH_LONG).show();
                              SharedPreferences.Editor editor = sharedPreferences.edit();
                              editor.putString("Name", response.body().getName());
                              editor.putString("Image",response.body().getImage());
                              editor.putString("url",response.body().getUrl());
                              editor.putString("TOKEN", response.body().getTokenMessage());
                              editor.putString("Address", response.body().getAddress());
                              editor.putInt("ID", response.body().getId());
                              editor.apply();
                              Intent intent = new Intent(getApplicationContext(), Home.class);
                              finish();
                              startActivity(intent);
                          }
                          else{
                                  try {
                                      Toast.makeText(getApplicationContext(), response.errorBody().string(), Toast.LENGTH_LONG).show();
                                  } catch (IOException e) {
                                      e.printStackTrace();
                                  }
                              }
                          }
                      @Override
                      public void onFailure(Call<Log_in> call, Throwable t) {
                          Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
                      }
                  });
              }
            }
        });
    }
    private boolean checkUp() {
    if(email.getText().toString().isEmpty()||password.getText().toString().isEmpty()){
        Toast.makeText(getApplicationContext(),"All Fields required!!",Toast.LENGTH_LONG).show();
        return false;
    }
    return true;
    }
}