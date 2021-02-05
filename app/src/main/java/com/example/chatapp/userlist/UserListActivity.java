package com.example.chatapp.userlist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.chatapp.ChatActivity;
import com.example.chatapp.ChatMessage;
import com.example.chatapp.R;
import com.example.chatapp.SignInActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.widget.GridLayout.HORIZONTAL;

public class UserListActivity extends AppCompatActivity {
    private StorageReference avatarStorageReference;

    private String userName;
    private String currentUserKey;
    private User currentUser;

    private FirebaseAuth auth;
    private DatabaseReference userDatabaseReference;
    private ChildEventListener childEventListener;

    private ArrayList<User> usersArrayList;
    private RecyclerView userRecyclerView;
    private UserAdapter userAdapter;
    private RecyclerView.LayoutManager userLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        avatarStorageReference = FirebaseStorage.getInstance().getReference().child("avatarImages");


        auth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        if(intent != null) {
            userName = intent.getStringExtra("userName");
            if(userName != null) {
                setTitle(userName);
            }
        }
        buildRecyclerView();
        attachUserDatabaseReferenceListener();

    }

    private void attachUserDatabaseReferenceListener() {

        userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        if(childEventListener == null){
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    User user = snapshot.getValue(User.class);

                    

                    if(!user.getId().equals(auth.getCurrentUser().getUid())) {
                        if(!user.isHasAvatar()) {
                            user.setAvatarMockUpResourse(R.drawable.user_image_full);
                        }

                        usersArrayList.add(user);
                        userAdapter.notifyDataSetChanged();
                    }else {
                        currentUser = user;
                        currentUserKey = snapshot.getKey();
                        setTitle( currentUser.getName());
                    }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };

            userDatabaseReference.addChildEventListener(childEventListener);
        }

    }

    private void buildRecyclerView() {



        usersArrayList = new ArrayList<>();
        userRecyclerView = findViewById(R.id.recyclerViewUserList);
        userRecyclerView.setHasFixedSize(true);
        userLayoutManager = new LinearLayoutManager(this);
        userAdapter = new UserAdapter(usersArrayList,this);
        userRecyclerView.setAdapter(userAdapter);
        userRecyclerView.setLayoutManager(userLayoutManager);
        /** DEVIVDER*/
        DividerItemDecoration itemDecor = new DividerItemDecoration(userRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        userRecyclerView.addItemDecoration(itemDecor);
        /***/


        userAdapter.setOnUserClickListener(new UserAdapter.OnUserClickListener() {
            @Override
            public void OnUserClick(int position) {
                goToChat(position);
            }
        });



    }

    private void goToChat(int position) {
        Intent intent = new Intent(UserListActivity.this, ChatActivity.class);
        intent.putExtra("userName",userName);
        intent.putExtra("RecipientUsrName",usersArrayList.get(position).getName());
        intent.putExtra("RecipientUserID",usersArrayList.get(position).getId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);

        return true;


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.signOutPriperty:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(UserListActivity.this, SignInActivity.class));
                return true;

            case R.id.selectAvatarButton:
                     loadAvatar();
                // TODO: 22.01.2021

            default: return super.onOptionsItemSelected(item);
        }

    }

    private void loadAvatar() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
        startActivityForResult(Intent.createChooser(intent,"Choose avatar"),0001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0001 && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();

            StorageReference imageReferenceUri = avatarStorageReference
                    .child(selectedImageUri.getLastPathSegment());
            userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
            UploadTask uploadTask = imageReferenceUri.putFile(selectedImageUri);



            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL

                    return imageReferenceUri.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                       // Toast.makeText(UserListActivity.this,"onComplete",Toast.LENGTH_LONG).show();

                        currentUser.setAvatarUrl(downloadUri.toString());
                        currentUser.setHasAvatar(true);
                        Map<String, Object> putValues = currentUser.toMap();

                        Map<String, Object> childUpdate = new HashMap<>();
                        childUpdate.put(currentUserKey,putValues);

                        userDatabaseReference.updateChildren(childUpdate);

                        Toast.makeText(UserListActivity.this,"CHANGED",Toast.LENGTH_LONG).show();







                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });





        }
        // TODO: 22.01.2021  
    }
}