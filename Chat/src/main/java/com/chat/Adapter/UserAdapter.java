package com.chat.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chat.MainActivity;
import com.chat.Model.Chat;
import com.chat.Model.User;
import com.chat.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import javax.xml.transform.Templates;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter  extends RecyclerView.Adapter<UserAdapter.ViewHolder>
{
    private List<User> listUsers;
    private Context context;
    private NavController navController;
    private boolean isChat;
    private FirebaseUser firebaseUser;

    private String theLastMessage;


    public UserAdapter(List<User> listUsers,boolean isChat, Context context, NavController navController)
    {
        this.navController = navController ;
        this.listUsers = listUsers;
        this.context = context;
        this.isChat=isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {

        User user = listUsers.get(position);
        holder.username.setText(user.getUserName());
        if(user.getImageURL().equals("default"))
            holder.profile_image.setImageResource(R.mipmap.ic_launcher_circle);
        else
            Glide.with(context).load(user.getImageURL()).into(holder.profile_image);

        if(isChat)
            lastMessage(user.getId(),holder.lastMsg);
        else
            holder.lastMsg.setVisibility(View.GONE);

        chooseColorStatus(user,holder);




        holder.itemView.setOnClickListener(v ->
        {
            MainActivity.sharedPreference.setIdUser(user.getId());
            MainActivity.sharedPreference.setUser(user.getId());
            navController.navigate(R.id.action_homePage_to_msgFragment);

        });

    }


    private void chooseColorStatus(User user, ViewHolder holder)
    {

        if(isChat)
        {
            if(user.getStatus().equals("online"))
            {

                holder.imgOn.setVisibility(View.VISIBLE);
                holder.imgOff.setVisibility(View.GONE);
            }
            else
            {
                holder.imgOn.setVisibility(View.GONE);
                holder.imgOff.setVisibility(View.VISIBLE);
            }
        }

        else
        {
            holder.imgOn.setVisibility(View.GONE);
            holder.imgOff.setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private CircleImageView profile_image;
        private TextView username;
        private CircleImageView imgOn;
        private CircleImageView imgOff;
        private TextView lastMsg;



        private ViewHolder(View view)
        {
            super(view);

            profile_image = view.findViewById(R.id.profile_image);
            username = view.findViewById(R.id.username);
            imgOn= view.findViewById(R.id.img_on_item);
            imgOff= view.findViewById(R.id.img_off_item);
            lastMsg=view.findViewById(R.id.last_msg);

        }
    }

    private void lastMessage(String userid, TextView lastMsg )
    {

        theLastMessage="default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {

                for(DataSnapshot snapshot1:snapshot.getChildren() )
                    for(DataSnapshot snapshot2 : snapshot1.getChildren())
                    {

                        Chat chat = snapshot2.getValue(Chat.class);
                        if(chat.getReceiver()==null|| firebaseUser == null || firebaseUser.getUid() == null)
                             return;
                        if((chat.getReceiver().equals(firebaseUser.getUid())&&
                           chat.getSender().equals(userid))||
                                (chat.getReceiver().equals(userid)&&
                                        chat.getSender().equals(firebaseUser.getUid())))
                            theLastMessage= chat.getMessage();

                    }
              if(theLastMessage.equals("default"))
                  lastMsg.setText("No message");
              else
                  lastMsg.setText(theLastMessage);
              theLastMessage = "default";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }


}
