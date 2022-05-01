package Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canvas.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import models.Message;

public class Message_Me extends RecyclerView.Adapter<Message_Me.ViewHolder> {
    ArrayList<Message>list = new ArrayList<>();
    @Override
    public int getItemViewType(int position) {
       return position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(list.get(viewType).isState()){
            return new ViewHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.message,parent,false));
        }
        else{
            return new ViewHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.message_other,parent,false));
        }
    }
    @Override
    public void onBindViewHolder(@NonNull Message_Me.ViewHolder holder, int position) {
        if(list.get(position).isState())
        {
            holder.message.setText(list.get(position).getMessage());
        }
        else {
            holder.messageOther.setText(list.get(position).getMessage());
            if(list.get(position).getImage()!=null){
                Picasso.with(holder.itemView.getContext()).load(list.get(position).getImage()).into(holder.circleImageView);
                System.out.println("GET IMAGE = " + list.get(position).getImage());
            }
            else{
                holder.circleImageView.setImageResource(R.drawable.user_circle);
            }
        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<Message>messages) {
        this.list=messages;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message ;
        TextView messageOther;
        CircleImageView circleImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message_me);
            messageOther = itemView.findViewById(R.id.message_other);
            circleImageView=itemView.findViewById(R.id.image_other);
        }
    }
}
