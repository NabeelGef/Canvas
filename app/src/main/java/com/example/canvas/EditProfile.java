package com.example.canvas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Url;
import models.API;
import models.Log_in;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.canvas.register.GET_FROM_GALLERY;

public class EditProfile extends AppCompatActivity {
SharedPreferences sharedPreferences;
EditText name , address;
CircleImageView image , change_image;
    Uri selectedImage;
    Button edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        init();
        getData();
        EditImage();
        EDITING();

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
    private void EDITING() {
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Name = name.getText().toString();
                String Address = address.getText().toString();
                if(Address.isEmpty())
                    Address = null;
                String Email = sharedPreferences.getString("Email", null);
                MultipartBody.Part image = null;
                if (selectedImage != null) {
                    File file = new File(getPath(selectedImage));
                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    image = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
                }
                Gson gson = new GsonBuilder().setLenient().create();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Constant.URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
                API api = retrofit.create(API.class);
                Call<Log_in> call = api.editing("nab@gmail.com", Name, null, Address, image,"admin");
                call.enqueue(new Callback<Log_in>() {
                    @Override
                    public void onResponse(Call<Log_in> call, Response<Log_in> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "EditingSuccess!!", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("Name",response.body().getName());
                            editor.putString("Image",response.body().getImage());
                            editor.putString("Address",response.body().getAddress());
                            editor.apply();
                            finish();
                            Intent intent = new Intent(getApplicationContext(),Home.class);
                            startActivity(intent);
                        }else{
                            System.out.println("Errorrrrrrrrrr : " + response.message());
                        }
                    }
                    @Override
                    public void onFailure(Call<Log_in> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Not Connected!!", Toast.LENGTH_LONG).
                                show();
                        System.out.println("ERROR : " + t.getMessage());
                    }
                });
            }
        });
    }
    private void getData() {
    name.setHint(sharedPreferences.getString("Name",""));
    address.setHint(sharedPreferences.getString("Address",""));
    String imagee = sharedPreferences.getString("Image",null);
    if(imagee==null){
        image.setImageResource(R.drawable.user_circle);
    }else{
        /*String previouslyEncodedImage = sharedPreferences.getString("Image", null);
        assert previouslyEncodedImage != null;
        if( !previouslyEncodedImage.equalsIgnoreCase("") ) {
            byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            image.setImageBitmap(bitmap);
        }*/
        Picasso.with(getApplicationContext()).load(imagee).into(image);
    }
    }

    private void EditImage() {
    change_image.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent,GET_FROM_GALLERY);

        }
    });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
             selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                image.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    private void init(){
        sharedPreferences = getSharedPreferences("SaveMyData", Context.MODE_PRIVATE);
        name = findViewById(R.id.EditingName);
        address = findViewById(R.id.EditingAddress);
        image = findViewById(R.id.Image_profile_Editing);
        change_image = findViewById(R.id.EditingImage);
        edit = findViewById(R.id.Edit_button);
    }
}