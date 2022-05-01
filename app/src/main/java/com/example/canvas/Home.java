package com.example.canvas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import Adapter.Adapter_home;
import models.API;
import models.ID_USER;
import models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ActionBarDrawerToggle actionBarDrawerToggle;
   DrawerLayout drawerLayout;
   NavigationView navigationView;
   Bundle bundle;
   RecyclerView recyclerView;
   Adapter_home adapter_home;
   ArrayList<User> users;
   SharedPreferences sharedPreferences;
   boolean isLogin;
   static String Token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupNotification();
        init();
        try {
            getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
            getUsers();
    }

    private void setupNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notification_channel", "notification_channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
           FirebaseMessaging.getInstance().subscribeToTopic("general")
                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed Successfully";
                        if (!task.isSuccessful()) {
                            msg = "Subscription failed";
                        }
                        System.out.println("MESSAGE : " + msg);
                    }
                });
    }

    private void getUsers() {
        int ID = sharedPreferences.getInt("ID",-1);
        if(ID!=-1) {
            Gson gson = new GsonBuilder().setLenient().create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            API api = retrofit.create(API.class);
            ID_USER id_user = new ID_USER(ID);
            Call<ArrayList<User>> call = api.getAllUsers(id_user);
            call.enqueue(new Callback<ArrayList<User>>() {
                @Override
                public void onResponse(Call<ArrayList<User>> call, Response<ArrayList<User>> response) {
                    if (response.isSuccessful()) {
                        users = response.body();
                        setAdapter(ID,users);
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed in response", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<User>> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Not Connected", Toast.LENGTH_LONG).show();
                }
            });
        }else{
            ALERTINGLOGIN();
        }
    }

    private void ALERTINGLOGIN() {
        isLogin = false;
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("You aren't login to This APP !!");
        builder1.setPositiveButton(
                "LOGIN",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getApplicationContext(),Login.class);
                        dialog.cancel();
                        finish();
                        startActivity(intent);
                    }
                });
        AlertDialog alertDialog=builder1.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void setAdapter(int ID ,ArrayList<User> users) {
     adapter_home.setUsers(users,ID);
     adapter_home.notifyDataSetChanged();
     recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
     recyclerView.setAdapter(adapter_home);
    }
    @SuppressLint("SetTextI18n")
    private void getData() throws IOException {
        View header = navigationView.getHeaderView(0);
        String Name = sharedPreferences.getString("Name",null);
        if(Name==null){
            ALERTINGLOGIN();
        }
        else{
            isLogin =true;
            TextView name = header.findViewById(R.id.name_profile);
            name.setText(""+sharedPreferences.getString("Name",null));
            ImageView imageProfile = header.findViewById(R.id.Image_profile);
            String image = sharedPreferences.getString("Image",null);
            if(image==null){
                imageProfile.setImageResource(R.drawable.user_circle);
            }
            else {
                Picasso.with(getApplicationContext()).load(Uri.parse(image)).into(imageProfile);
                /*String url = sharedPreferences.getString("url",null);
                if(url==null){
                    imageProfile.setImageResource(R.drawable.user_circle);
                }else{
                    byte[] b = Base64.decode(url, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                    imageProfile.setImageBitmap(bitmap);
                }
            */}
            imageProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                    View view = getLayoutInflater().inflate(R.layout.profile,null);
                    builder.setView(view);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    ImageView imageView = view.findViewById(R.id.image_yourProfile);
                    if(image==null){
                        imageView.setImageResource(R.drawable.user_circle);
                    }else{
                        /*String previouslyEncodedImage = sharedPreferences.getString("url", null);
                        assert previouslyEncodedImage != null;
                        if( !previouslyEncodedImage.equalsIgnoreCase("") ) {
                            byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                            imageView.setImageBitmap(bitmap);
                        }*/
                        Picasso.with(getApplicationContext()).load(Uri.parse(image)).into(imageView);
                    }
                    TextView yourName = view.findViewById(R.id.Name_yourProfile);
                    TextView yourAddress = view.findViewById(R.id.Address_yourProfile);
                    yourName.setText(""+sharedPreferences.getString("Name",""));
                    yourAddress.setText(""+sharedPreferences.getString("Address",""));
                    Button close = view.findViewById(R.id.close_profile);
                    Button editProfile = view.findViewById(R.id.edit_profile);
                    editProfile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(),EditProfile.class);
                            startActivity(intent);
                        }
                    });
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                            System.out.println("DialogCancel!!!!");
                        }
                    });
                }
            });
        }
    }
    private void init(){
       drawerLayout = findViewById(R.id.myDrawer);
       navigationView = findViewById(R.id.nav_view);
       navigationView.setNavigationItemSelectedListener(this);
       actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open,R.string.nav_close);
       drawerLayout.addDrawerListener(actionBarDrawerToggle);
       actionBarDrawerToggle.syncState();
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       bundle = getIntent().getExtras();
       users = new ArrayList<>();
       adapter_home = new Adapter_home();
       recyclerView = findViewById(R.id.recycler_Home);
       sharedPreferences = getSharedPreferences("SaveMyData", Context.MODE_PRIVATE);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.logout){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Name",null);
            editor.putString("Image",null);
            editor.apply();
            ALERTINGLOGIN();
        }
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.searching, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        return  true;
    }

    public boolean isConnectedToServer(String url, int timeout) {
        try{
            URL myUrl = new URL(url);
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(timeout);
            connection.connect();
            return true;
        } catch (Exception e) {
            System.out.println("EROR " + e.getMessage());
            // Handle your exceptions
            return false;
        }
    }
}
