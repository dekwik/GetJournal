package com.example.getjournal.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.getjournal.DetailRiwayatPost;
import com.example.getjournal.Model.Posts;
import com.example.getjournal.Model.User;
import com.example.getjournal.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.ViewHolder> {

    ArrayList<Posts> Listposts;
    ArrayList<User> listUser;
    private SharedPreferences userPref;
    int status;

    Context context;

    public AdapterPosts(ArrayList<Posts> Listposts, ArrayList<User> listUser,  Context context) {
        this.Listposts = Listposts;
        this.listUser = listUser;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_posts_menu, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Posts posts = Listposts.get(position);
        User user = listUser.get(position);

//        Log.d("cccc", String.valueOf(status));
//        Log.d("DDD", posts.getId_user());
//        Log.d("EEE", userPref.getString("id",null));

        holder.textView.setText(""+posts.getJudul());
        holder.txtDate.setText("Published : "+posts.getDate());
        holder.textView1.setText("DOI : "+posts.getDoi());
        holder.name.setText("Posted by : "+user.getName()+" "+user.getLastname());
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(posts.getId_user().equals(userPref.getString("id", null))){
                    status = 1;
//                    Log.d("1aaa", "if berhasil ");
                }else{
                    status = 0;
//                    Log.d("2aaa", "if gagal ");
                }

                Intent intent =  new Intent(context, DetailRiwayatPost.class);
                intent.putExtra("position", position);
                intent.putExtra("status",status);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {

        return Listposts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView, textView1,name,txtDate ;
        Button button1, button2;
        ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userPref =  context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
            txtDate = itemView.findViewById(R.id.txtPostDate);
            imageView = itemView.findViewById(R.id.bukgedebuk);
            textView = itemView.findViewById(R.id.JudulJurnal);
            name = itemView.findViewById(R.id.postby);
            textView1 = itemView.findViewById(R.id.DoiJurnal);
            button1 = itemView.findViewById(R.id.editrwtrgs);
            button2 = itemView.findViewById(R.id.delrwtrgs);
            constraintLayout = itemView.findViewById(R.id.consrwtrgs);
        }
    }
}
