package com.chat.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResetPassword extends Fragment
{
   private Toolbar toolbar;
   private EditText textEmail;
   private Button btnReset;
   private NavController navController;

   private FirebaseAuth firebaseAuth;


    public ResetPassword() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        navController = NavHostFragment.findNavController(this);
        bindWithXML(view);
        buildToolbar();
        listenerReset();
    }

    private void listenerReset()
    {
        btnReset.setOnClickListener(v -> {
            String email = textEmail.getText().toString();
            if(email.equals(""))
                Toast.makeText(getContext(),"All fields are required!", Toast.LENGTH_SHORT).show();
            else
            {
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful()) {
                            Toast.makeText(getContext(), "Please check your email", Toast.LENGTH_SHORT).show();
                            navController.popBackStack();
                        }
                        else
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void bindWithXML(View view)
    {
        toolbar = view.findViewById(R.id.toolBar_reset);
        textEmail = view.findViewById(R.id.send_email);
        btnReset = view.findViewById(R.id.btn_reset);
    }

    private void buildToolbar()
    {

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Reset Password");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
