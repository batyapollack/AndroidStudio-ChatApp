package com.chat.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chat.MainActivity;
import com.chat.Model.User;
import com.chat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Start extends Fragment
{

    private Button btnLogin;
    private Button btnRegister;
    private NavController navController;
    private FirebaseUser firebaseUser;


    public Start() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.fragClass = getClass();
        navController = NavHostFragment.findNavController(this);
        init();
        bindWithXML(view);
        listeners(view);



    }

    private void init()
    {
         firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null)
        {
            firebaseUser.getUid();
            navController.navigate(R.id.action_startFragment_to_homePage);
        }
    }


    private void bindWithXML(View view)
    {
        btnLogin = view.findViewById(R.id.login);
        btnRegister = view.findViewById(R.id.register);

    }

    private void listeners(View view)
    {
        btnLogin.setOnClickListener(view1 -> {
            navController.navigate(R.id.action_startFragment_to_Login);
        });
        btnRegister.setOnClickListener(view1 -> {
            navController.navigate(R.id.action_startFragment_to_register);
        });
    }
}
