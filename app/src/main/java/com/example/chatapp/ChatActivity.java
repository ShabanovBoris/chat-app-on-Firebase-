package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.chatapp.userlist.User;
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
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private String recipientUserName;

    private FirebaseAuth auth;

    private ListView messageListView;
    private ChatCustomAdapter chatCustomAdapter;
    private ProgressBar progressBar;
    private ImageView sendImageButton;
    private Button sendMessageButton;
    private EditText sendEdittext;
    private String userName;
    private String recipientUserID;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener messagesListener;

    private DatabaseReference databaseReferenceUsers;
    private ChildEventListener usersChildEventListener;

    private FirebaseStorage storage;
    private StorageReference imagesReferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        auth = FirebaseAuth.getInstance();




        progressBar = findViewById(R.id.progressBar);
        sendImageButton = findViewById(R.id.imageViewButtonSendImage);
        sendMessageButton = findViewById(R.id.buttonSendMessage);
        sendEdittext = findViewById(R.id.editTextMessage);
        messageListView = findViewById(R.id.listViewChat);


        Intent intent = getIntent();
        if(intent != null ){
            recipientUserName = intent.getStringExtra("RecipientUsrName");
            userName = intent.getStringExtra("userName");
        } else {
            userName = "DefaultUser";
        }

        setTitle("Chat with "+recipientUserName );

        List<ChatMessage> chatMessageList = new ArrayList<>();
        chatCustomAdapter = new ChatCustomAdapter(this,R.layout.message_item,chatMessageList);
        messageListView.setAdapter(chatCustomAdapter);

        recipientUserID = intent.getStringExtra("RecipientUserID");
        userName = intent.getStringExtra("userName");



        /** DATABASE INITIALISATION*/
        storage = FirebaseStorage.getInstance();
        imagesReferences = storage.getReference().child("ChatImages");



        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReferenceUsers = firebaseDatabase.getReference().child("users");
        databaseReference = firebaseDatabase.getReference().child("Messages");
        messagesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatMessage message = snapshot.getValue(ChatMessage.class);
                if(message.getSender().equals(auth.getCurrentUser().getUid())
                && message.getRecipient().equals(recipientUserID)){
                    message.setMine(true);
                    chatMessageList.add(message);
                    chatCustomAdapter.add(message);
                }else  if(message.getRecipient().equals(auth.getCurrentUser().getUid())
                        && message.getSender().equals(recipientUserID)){
                    message.setMine(false);
                    chatMessageList.add(message);
                    chatCustomAdapter.add(message);
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

        messageListView.scrollTo(0,messageListView.getMaxScrollAmount());

        usersChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                if(user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                   userName =  user.getName();
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

        databaseReference.addChildEventListener(messagesListener);
        databaseReferenceUsers.addChildEventListener(usersChildEventListener);





        progressBar.setVisibility(ProgressBar.INVISIBLE);

        sendEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().trim().length() > 0)
                    sendMessageButton.setEnabled(true);
                else sendMessageButton.setEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        /** bonus max length edit text  */
        sendEdittext.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(55)
        });




        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setText(sendEdittext.getText().toString());
                chatMessage.setName(userName);
                chatMessage.setImageURL(null);
                chatMessage.setSender(auth.getUid());
                chatMessage.setRecipient(recipientUserID);

                databaseReference.push().setValue(chatMessage);

                sendEdittext.setText("",null);
            }
        });

        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(Intent.createChooser(intent,"Choose image"),123);


            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123 && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();


            StorageReference imageReferenceUri = imagesReferences
                    .child(selectedImageUri.getLastPathSegment());

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
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setImageURL(downloadUri.toString());
                        chatMessage.setName(userName);
                        chatMessage.setSender(auth.getUid());
                        chatMessage.setRecipient(recipientUserID);
                        databaseReference.push().setValue(chatMessage);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });


        }



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
                startActivity(new Intent(ChatActivity.this,SignInActivity.class));
                return true;
            default: return super.onOptionsItemSelected(item);
        }

    }
}