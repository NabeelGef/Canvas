package com.example.canvas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Adapter.Message_Me;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import models.API;
import models.ChatData;
import models.Message;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Chat extends AppCompatActivity {
  Bundle bundle;
  EditText send;
  Message_Me message_me;
  RecyclerView recyclerViewOther;
  Socket socket;
  ArrayList<Message>AllMessages;
  LinearLayoutManager linearLayoutManager;
  int idOther , myID;
  String TokenOther;
  SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        bundle = getIntent().getExtras();
        init();
        getAllMessages();
        ClickSendingMessage();
        SocketConnection();
        ListenAboutMessages();

    }

    private void getAllMessages() {
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        API api =retrofit.create(API.class);
        ChatData chatData = new ChatData(myID,idOther,"");
        Call<ArrayList<ChatData>> call = api.getAllMessages(chatData);
        call.enqueue(new Callback<ArrayList<ChatData>>() {
            @Override
            public void onResponse(Call<ArrayList<ChatData>> call, Response<ArrayList<ChatData>> response) {
                if(response.isSuccessful())
                {
                    setAdapter(response.body());
                }
            }
            @Override
            public void onFailure(Call<ArrayList<ChatData>> call, Throwable t) {
               Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setAdapter(ArrayList<ChatData> body) {
        for(int i = 0 ; i<body.size();i++){
            if(body.get(i).getIdme()==idOther) {
                System.out.println("IDOther = " + body.get(i).getIdme());
                AllMessages.add(new Message(body.get(i).getMessage(), bundle.getString("Image"), false));
            }else
            {
                System.out.println("MY ID = " + body.get(i).getIdme());
                AllMessages.add(new Message(body.get(i).getMessage(),"",true));
            }
        }
        message_me.setList(AllMessages); // put the list in Adapter
        message_me.notifyDataSetChanged();// changing adapter after put it
        recyclerViewOther.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false) );// make recyclerView
        recyclerViewOther.setAdapter(message_me);//push adapter into recyclerView
        recyclerViewOther.scrollToPosition(AllMessages.size()-1);
    }
    private void ListenAboutMessages() {
    socket.on("message", new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("ARGS = " + args[0]);
                    JSONObject jsonObject = (JSONObject)args[0];
                    try {
                        if(jsonObject.getInt("id")==myID){
                            String message = jsonObject.getString("message");

                            if(bundle.getString("Image")!=null)
                            {
                                AllMessages.add(new Message(message,bundle.getString("Image"),false));//add message into ArrayList
                            }else{
                                AllMessages.add(new Message(message,"",false));
                            }
                            message_me.setList(AllMessages);//put the list into Adapter
                            message_me.notifyDataSetChanged();
                            recyclerViewOther.setLayoutManager(new  LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));//making recycleView
                            recyclerViewOther.setAdapter(message_me);//push adapter into recyclerView
                            recyclerViewOther.scrollToPosition(AllMessages.size()-1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    });
    }
    private void SocketConnection() {
        try {
            socket = IO.socket(Constant.URL);
            socket.connect();
         } catch (Exception e) {
            Log.d("fail", "Failed to connect");
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void ClickSendingMessage() {
    send.setOnTouchListener(new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (send.getRight() - send.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    SENDMESSAGE();
                    return true;
                }
            }
            return false;
        }
    });
    }
        private void SENDMESSAGE() {
        AllMessages.add(new Message(send.getText().toString(),"",true)); // ArrayList
            String myImage = sharedPreferences.getString("Image","");
                socket.emit("sendmessage",myImage,TokenOther,idOther,send.getText().toString());// Send To Socket Server
            message_me.setList(AllMessages); // put the list in Adapter
        message_me.notifyDataSetChanged();// changing adapter after put it
        recyclerViewOther.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false) );// make recyclerView
        recyclerViewOther.setAdapter(message_me);//push adapter into recyclerView
        recyclerViewOther.scrollToPosition(AllMessages.size()-1);
        send.setHint("send Message");
            Gson gson = new GsonBuilder().setLenient().create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            API api =retrofit.create(API.class);
            ChatData chatData = new ChatData(myID,idOther,send.getText().toString());
            Call<ResponseBody> call = api.putMessages(chatData);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(!response.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"ERROR IN RESPONSE!!",Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                  Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
            send.setText("");
            //messages_me.clear();
        }
    private void init() {
        send = findViewById(R.id.send_message);
        recyclerViewOther = findViewById(R.id.recycler_other);
        message_me = new Message_Me();
        AllMessages = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        idOther = bundle.getInt("ID");
        myID = bundle.getInt("MYID");
        sharedPreferences = getSharedPreferences("SaveMyData", Context.MODE_PRIVATE);
        TokenOther = bundle.getString("TOKENOther");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View view = layoutInflater.inflate(R.layout.info_chat_actionbar, null);
        TextView title_chat = view.findViewById(R.id.title_chat);
        ImageView image_chat = view.findViewById(R.id.image_chat);
        title_chat.setText(bundle.getString("Name"));
        if(bundle.getString("Image")==null)
            image_chat.setImageResource(R.drawable.user_circle);
        else
            Picasso.with(getApplicationContext()).load(bundle.getString("Image")).into(image_chat);
        actionBar.setCustomView(view);
        actionBar.setDisplayShowCustomEnabled(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_item, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         int id = item.getItemId();
         switch (id){
             case R.id.search:
                 System.out.println("Search!!");
                 break;
             case R.id.InfoUser:
                 System.out.println("InfoUser!!");
                 break;
             case R.id.CallVoice:
                 System.out.println("Voice!!");
                 break;
             case R.id.CallVideo:
                 System.out.println("Video!!");
                 break;
             default:
                 finish();
                 break;
         }
        return true;
    }
}