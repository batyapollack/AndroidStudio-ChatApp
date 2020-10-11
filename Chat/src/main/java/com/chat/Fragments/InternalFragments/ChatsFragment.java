package com.chat.Fragments.InternalFragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chat.Adapter.UserAdapter;
import com.chat.Model.Chat;
import com.chat.Model.User;
import com.chat.Notifications.Token;
import com.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment
{
    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<User> mUsers; // forAdapter

    private Set<String> setUsersId;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    private NavController navController;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        buildRecyclerView(view);
        initialize();
        listener();
        tokenFunc();
        return view ;
    }

    private void tokenFunc()
    {
        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    private void listener()
    {

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                setUsersId.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren())
                {

                    String key = snapshot1.getKey();
                    assert key != null;
                    String[] packet = key.split("eranbatya");
                    String sender = packet[0];
                    String receiver = packet[1];


                    if(sender.equals(firebaseUser.getUid())
                            ||receiver.equals(firebaseUser.getUid()))
                    {
                        setUsersId.add(sender);
                        setUsersId.add(receiver);
                    }

                }
                setUsersId.remove(firebaseUser.getUid());
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readChats()
    {

        mUsers= new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {

                mUsers.clear();

                //usersList --array<String>
                for(DataSnapshot snapshot1 : snapshot.getChildren() )
                {
                    User user = snapshot1.getValue(User.class);// user from db
                    for(String id : setUsersId)
                    {
                        if(user.getId().equals(id))
                        {
                            if(mUsers.size()!=0 )
                            {
                                boolean contain = false;
                                for (int i=0; i < mUsers.size(); i++ )
                                {
                                    if(user.getId().equals(mUsers.get(i).getId()))
                                    {
                                       contain = true;
                                    }
                                }
                                if(!contain)
                                {
                                    mUsers.add(user);
                                }
                            }
                            else{
                                mUsers.add(user);
                            }

                        }
                    }
                }

                userAdapter= new UserAdapter(mUsers,true,getContext(),navController);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void updateToken(String token)
    {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }

    private void initialize()
    {
        navController = NavHostFragment.findNavController(this);
        setUsersId = new HashSet<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
    }

    private void buildRecyclerView(View view)
    {
        recyclerView = view.findViewById(R.id.recyclerViewChat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

}
