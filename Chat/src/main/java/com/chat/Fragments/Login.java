package com.chat.Fragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.chat.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class Login extends Fragment {

    private MaterialEditText email, password;
    private Button btnLogin;
    private FirebaseAuth auth;
    private Toolbar toolbar;
    private NavController navController;
    private static final String EMAIL = "email";

    private CallbackManager callbackManager;

    private TextView forgotPassword;
    private DatabaseReference reference;

    public Login() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        navController = NavHostFragment.findNavController(this);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        bindWithXML(view);
        buildToolbar();
        //facebookLogin(view);
        listenerLogin();
        listenerForgotPassword();


    }

    private void listenerForgotPassword()
    {
        forgotPassword.setOnClickListener(v -> {
            navController.navigate(R.id.action_Login_to_resetPassword);
        });
    }

    private void listenerLogin()
    {
        btnLogin.setOnClickListener(view1 -> {
            String txtEmail = Objects.requireNonNull(email.getText()).toString();
            String txtPassword = Objects.requireNonNull(password.getText()).toString();

            if(TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword))
                Toast.makeText(getContext(),"All fields are required", Toast.LENGTH_SHORT).show();
            else
            {
                auth.signInWithEmailAndPassword(txtEmail,txtPassword).addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        navController.navigate(R.id.action_Login_to_homePage);
                    }
                    else
                        Toast.makeText(getContext(),"Email or password not valid ", Toast.LENGTH_SHORT).show();

                });
            }

        });

    }

    private void buildToolbar()
    {
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Login");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){



            case R.id.logout:
                navController.navigate(R.id.homePage);
                break;



        }
        return super.onOptionsItemSelected(item);
    }

    private void facebookLogin(View view)
    {
//        LoginButton loginButton = view.findViewById(R.id.loginButtonFacebbook);
//        // If you are using in a fragment, call loginButton.setFragment(this);
//        loginButton.setFragment(this);
//        loginButton.setReadPermissions(Arrays.asList(EMAIL));
//        LoginManager loginManager = LoginManager.getInstance();
//        loginManager.setLoginBehavior(LoginBehavior.DIALOG_ONLY);
//        loginManager.registerCallback(callbackManager,
//                new FacebookCallback<LoginResult>() {
//                    @Override
//                    public void onSuccess(LoginResult loginResult)
//                    {
//                        AccessToken token = loginResult.getAccessToken();
//                        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//                        FirebaseAuth.getInstance().signInWithCredential(credential)
//                                .addOnCompleteListener(task -> {
//                                    if (task.isSuccessful())
//                                    {
//                                        FirebaseUser firebaseUser = auth.getCurrentUser();
//
//                                        String userId = firebaseUser.getUid();
//                                        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
//
//                                        HashMap<String, String > hashMap = new HashMap<>();
//                                        hashMap.put("id", userId);
//                                        hashMap.put("userName", "");
//                                        hashMap.put("imageURL", "default");
//                                        hashMap.put("status","offline");
//                                        hashMap.put("search","");
//                                        navController.navigate(R.id.action_Login_to_homePage);
//                                    } else {
//                                        Toast.makeText(getContext(),"Sign not success", Toast.LENGTH_SHORT).show();
//                                    }
//
//
//                                });
//
//
//                    }
//
//
//                    @Override
//                    public void onCancel() {
//                        // App code
//                    }
//
//                    @Override
//                    public void onError(FacebookException exception) {
//                        // App code
//                    }
//
//
//
//
//                });


    }

    private void bindWithXML(View view)
    {
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        btnLogin = view.findViewById(R.id.btnLogin);
        toolbar = view.findViewById(R.id.toolBar);
        forgotPassword= view.findViewById(R.id.forgot_password);


}

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
