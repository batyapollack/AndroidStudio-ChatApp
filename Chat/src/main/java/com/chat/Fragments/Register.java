package com.chat.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.chat.R;
import com.facebook.appevents.ml.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class Register extends Fragment
{
    private MaterialEditText userName, email, password;
    private Button btnRegister;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private NavController navController;
    private Toolbar toolbar;


    public Register() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.register_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindWithXml(view);
        toolbar();
        auth = FirebaseAuth.getInstance();
        navController = NavHostFragment.findNavController(this);

        btnRegister.setOnClickListener(view1 ->
                listenerBtnRegister());
    }

    private void listenerBtnRegister()
    {
        String txtUsername = Objects.requireNonNull(userName.getText()).toString();
        String txtEmail = Objects.requireNonNull(email.getText()).toString();
        String txtPassword = Objects.requireNonNull(password.getText()).toString();

        if(TextUtils.isEmpty(txtUsername) || TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword))
            Toast.makeText(getContext(),"All fields are required", Toast.LENGTH_SHORT).show();
        else if(txtPassword.length() < 6 )
            Toast.makeText(getContext(),"password must be must be a least 6 characters", Toast.LENGTH_SHORT).show();
        else
            register(txtUsername,txtEmail,txtPassword);

    }

    private void toolbar()
    {
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Register");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void register(String userName,String email, String password)
    {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                FirebaseUser firebaseUser = auth.getCurrentUser();

                String userId = firebaseUser.getUid();
                reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                HashMap<String, String > hashMap = new HashMap<>();
                hashMap.put("id", userId);
                hashMap.put("userName", userName);
                hashMap.put("imageURL", "default");
                hashMap.put("status","offline");
                hashMap.put("search",userName.toLowerCase());

                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            //navigate
                            navController.navigate(R.id.action_register_to_homePage);
                        }

                    }
                });
            }
            else
                Toast.makeText(getContext(), "register not success", Toast.LENGTH_SHORT).show();
        });
    }

    private void bindWithXml(View view)
    {
        userName = view.findViewById(R.id.username);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        btnRegister = view.findViewById(R.id.btn_register);
        toolbar = view.findViewById(R.id.toolBar);
    }
}
