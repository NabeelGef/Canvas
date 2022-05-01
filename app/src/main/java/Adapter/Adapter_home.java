package Adapter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canvas.Chat;
import com.example.canvas.R;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import models.User;

public class Adapter_home extends RecyclerView.Adapter<Adapter_home.ViewHolder> {
    ArrayList<User> users = new ArrayList<>();
    int MYID;
    Bundle bundle = new Bundle();
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate
                (R.layout.recycler_home,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(users.get(position).getImage()==null){
            holder.profile_image.setImageResource(R.drawable.user_circle);
        }else {
            Picasso.with(holder.itemView.getContext()).load(Uri.parse(users.get(position).getImage()))
                    .into(holder.profile_image);
        }
        holder.profile_name.setText(users.get(position).getName());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("My Id In Adapter : "+MYID);
                bundle.putInt("MYID",MYID);
                bundle.putInt("ID",users.get(position).getId());
                bundle.putString("Image",users.get(position).getImage());
                System.out.println("IMAGE: ?????????????" + bundle.getString("Image"));
                bundle.putString("Name",users.get(position).getName());
                bundle.putString("Email",users.get(position).getEmail());
                bundle.putString("TOKENOther",users.get(position).getTokenMessage());
                System.err.println("TokenInAdapter : " + users.get(position).getTokenMessage());
                Intent intent = new Intent(holder.itemView.getContext(), Chat.class);
                intent.putExtras(bundle);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return users.size();
    }
    public void setUsers(ArrayList<User>users, int myid){

        this.users = users;
        this.MYID=myid;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_image;
        TextView profile_name;
        LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.profileImage_home);
            profile_name = itemView.findViewById(R.id.profileName_home);
            linearLayout = itemView.findViewById(R.id.item);
        }
    }
}
