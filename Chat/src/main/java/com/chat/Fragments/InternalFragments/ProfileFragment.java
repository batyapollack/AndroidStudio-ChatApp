package com.chat.Fragments.InternalFragments;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chat.Model.User;
import com.chat.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    private TextView userName;
    private CircleImageView profileImage;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;
    private static final int IMAGE_REQUEST=1;
    private Uri imageUri;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        bindWithXml(view);

        readUserNameFromDB();
        listeners();
        // Inflate the layout for this fragment
        return view;
    }

    private void listeners()
    {
        profileImage.setOnClickListener(v ->
        {
            openImage();
        });
    }

    private void openImage()
    {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST) ;
    }

    private String getFileExtension(Uri uri){

        ContentResolver contentResolver= getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private  void uploadImage()
    {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if(imageUri!=null)
        {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "."+ getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(task -> {
                if(!task.isSuccessful())
                    throw task.getException();

                return fileReference.getDownloadUrl();

            }).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Uri uri = task.getResult();
                    String mUri = uri.toString();

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("imageURL",mUri);
                    reference.updateChildren(hashMap);
                    pd.dismiss();

                }
                else
                {
                    Toast.makeText(getContext(),"Failed!" , Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
        else
        {
            Toast.makeText(getContext(),"No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMAGE_REQUEST && resultCode == RESULT_OK
            &&data != null && data.getData()!= null)
            imageUri = data.getData();
        else
            uploadImage();
    }

    private void readUserNameFromDB()
    {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(getContext()==null)
                    return;

                User user = snapshot.getValue(User.class);
                userName.setText(user.getUserName());
                if(user.getImageURL().equals("default"))
                    profileImage.setImageResource(R.mipmap.ic_launcher_circle);
                else
                    Glide.with(getContext()).load(user.getImageURL()).into(profileImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void bindWithXml(View view)
    {
        userName = view.findViewById(R.id.tv_username_profile);
        profileImage = view.findViewById(R.id.image_profile_profile);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

    }

}
