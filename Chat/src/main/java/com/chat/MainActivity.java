package com.chat;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;



import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;


import com.bumptech.glide.Glide;
import com.chat.Model.User;
import com.chat.Utils.SharedPreference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "MainActivity";
    public static SharedPreference sharedPreference;
    private NavController navController;
    public static FragmentManager fragmentManager;
    public static Class fragClass;

    private CircleImageView profileImage;
    private TextView userName;

    FirebaseUser firebaseUser;
    DatabaseReference reference ;




    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreference = new SharedPreference(this);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
        fragmentManager = getSupportFragmentManager();// קבלת תמיכה
        navController = Navigation.findNavController(this,R.id.nav_host_fragment);


    }



    @Override
    protected void onStart()
    {
        super.onStart();

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {// controoller- nvaController , dest- the locate that i am here on map,arg - like put extra on activity
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.menu,menu);
       return true;

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logout:
                listenerLogout();
                sharedPreference.setUser("");

          return true;
        }
        return false;
    }


    private void listenerLogout()
    {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser==null)
            return;
        Task<Void> dbTask = setStatus();

        dbTask.addOnCompleteListener(task -> {// than
            if(task.isSuccessful()){
                FirebaseAuth.getInstance().signOut();
                navController.navigate(R.id.startFragment);
                getSupportActionBar().setTitle(" ");
            }
        });





//        CompletableFuture.supplyAsync(()->
//        {
//            setStatus();
//            return "";
//
//        }).thenRun(()->{
//            FirebaseAuth.getInstance().signOut();
//
//        }).thenRun(()->{
//            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//            navController.navigate(R.id.startFragment);
//            getSupportActionBar().setTitle(" ");
//        });


    }



    private Task<Void> setStatus()
    {
        HashMap<String, Object> hashMap = new HashMap();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            // task is like promise
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                    .child(firebaseUser.getUid());
            hashMap.put("status", "offline");

            return reference.updateChildren(hashMap);



    }

    @Override
    public void onBackPressed()
    {

        int currentDestinationId = navController.getCurrentDestination().getId();

        if(currentDestinationId==R.id.startFragment)
        {
            finish();
            return;
        }
        if(currentDestinationId == R.id.homePage
                ||currentDestinationId == navController.getGraph().getStartDestination()) //if we state in homeFragment-back button affect to finish app
        {
            setStatus();
            finish();
            return;
        }

        if(currentDestinationId == R.id.msgFragment)
        {
            // back to pre fragment
            navController.popBackStack();
            refreshFragment();
            sharedPreference.setUser("");
        return;
      }
        navController.popBackStack();
    }

    private void refreshFragment()
    {
        int currFragmentId = navController.getCurrentDestination().getId();

        NavOptions navOptions = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .build();
        navController.navigate(currFragmentId, null, navOptions);// refresh to same frag
    }


}
