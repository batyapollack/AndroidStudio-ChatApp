package com.chat.Fragments.InternalFragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.chat.Adapter.UserAdapter;
import com.chat.Model.User;
import com.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.tasks.Tasks.await;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment
{
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    private NavController navController;
    private EditText search;
    private FirebaseUser firebaseUser;


    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_users, container, false);
        navController = NavHostFragment.findNavController(this);
        buildRecyclerView(view);

        mUsers = new ArrayList<>();
        readUsers();
        search = view.findViewById(R.id.search);
        searchUsers(view);
        return view;

    }

    private void searchUsers(View view)
    {
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                Query query = FirebaseDatabase.getInstance().getReference("Users").
                        orderByChild("search").startAt(s.toString().toLowerCase()).endAt(s.toString().toLowerCase()+"\uf8ff");

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mUsers.clear();
                        for(DataSnapshot dataSnapshot: snapshot.getChildren())
                        {
                            User user = dataSnapshot.getValue(User.class);
                            assert user != null;
                            if(!(user.getId().equals(firebaseUser.getUid())))
                                mUsers.add(user);

                        }
                        userAdapter = new UserAdapter(mUsers,false,getContext(),navController);
                        recyclerView.setAdapter(userAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {


                    }
                });

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void buildRecyclerView(View view)
    {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private  void readUsers()
    {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

            reference.addValueEventListener(new ValueEventListener()
            {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mUsers.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        assert firebaseUser != null;
                        assert user != null;
                        if (!user.getId().equals(firebaseUser.getUid()))
                            mUsers.add(user);

                    }
                    userAdapter = new UserAdapter(mUsers,false, getContext(),navController );
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(userAdapter);


                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


