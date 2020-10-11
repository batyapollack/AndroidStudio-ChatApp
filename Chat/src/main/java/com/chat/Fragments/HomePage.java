package com.chat.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chat.Fragments.InternalFragments.ChatsFragment;
import com.chat.Fragments.InternalFragments.ProfileFragment;
import com.chat.Fragments.InternalFragments.UsersFragment;
import com.chat.Model.Chat;
import com.chat.Model.User;
import com.chat.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomePage extends Fragment
{
    private StorageReference mStorageRef;
    private NavController navController;
    private CircleImageView profileImage;
    private TextView userName;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    public void onStart() {
        super.onStart();

    }





    public HomePage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = NavHostFragment.findNavController(this);

        toolbarFunc(view);
        bindAppbarXML(view);
        storage();

        dbConnection(view);




    }

    private void toolbarFunc(View view)
    {
        Toolbar toolbar = view.findViewById(R.id.toolbar_home_page);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(" ");
    }

    private void bindAppbarXML(View view)
    {
        profileImage = view.findViewById(R.id.profileImage_appbar);
        userName = view.findViewById(R.id.username_appbar);
    }

    private void storage() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    private void createFragments(View view )
    {
        if(getActivity() == null)
        {
            return;
        }
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPager viewPager =view.findViewById(R.id.view_pager);


        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(getActivity()==null||firebaseUser==null)
                    return;
                 viewPagerAdapter = new ViewPagerAdapter(((getActivity())).getSupportFragmentManager());
                 int unread = 0;
                 for(DataSnapshot snapshot1: snapshot.getChildren())
                     for(DataSnapshot snapshot2: snapshot1.getChildren())
                     {
                         Chat chat = snapshot2.getValue(Chat.class);
                         if(chat.getReceiver().equals(firebaseUser.getUid())
                                 && !chat.isSeen() )
                             unread++;

                     }
                 if(unread == 0)
                     viewPagerAdapter.addFragment(new ChatsFragment(), "Chats");
                 else
                     viewPagerAdapter.addFragment(new ChatsFragment(), "("+ unread + ")Chats");


                viewPagerAdapter.addFragment(new UsersFragment(), "Users");
                viewPagerAdapter.addFragment(new ProfileFragment(), "Profile");

                viewPager.setAdapter(viewPagerAdapter);

                tabLayout.setupWithViewPager(viewPager);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void dbConnection(View view)
    {

         firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser== null)
            return;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                User user = snapshot.getValue(User.class);
                if(user!=null)
                {
                    doWork(user);

                }

            }

            private void doWork(User user)
            {
                updateName(user);
                updateImgURL(user);
                createFragments(view);
                createFragments(view);
            }

            private void updateImgURL(User user)
            {
                if (getActivity() == null||getContext()==null)
                    return;
                if(user.getImageURL().equals("default"))
                    profileImage.setImageResource(R.mipmap.ic_launcher_circle);
                else
                    Glide.with(getContext()).load(user.getImageURL()).into(profileImage);
            }

            private void updateName(User user)
            {
                if(user.getUserName()==null)
                    userName.setText(" ");
                else
                    userName.setText(user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    class ViewPagerAdapter extends FragmentPagerAdapter
    {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;


        ViewPagerAdapter(FragmentManager fm )
        {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();


        }



        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);

        }

        public void addFragment(Fragment fragment , String title)
        {
            fragments.add(fragment);
            titles.add(title);

        }
    }

    private void status(String status) {

      firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser== null)
            return;
      DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());


      HashMap<String,Object> hashMap = new HashMap();
      hashMap.put("status",status);

      reference.updateChildren(hashMap);

    }

    @Override
    public void onResume() {
        super.onResume();
        status("online");
    }


//    @Override
//    public void onPause() {
//        super.onPause();
//        status("offline");
//    }
}
