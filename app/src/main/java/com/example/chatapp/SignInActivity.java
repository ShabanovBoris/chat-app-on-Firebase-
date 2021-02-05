package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp.userlist.User;
import com.example.chatapp.userlist.UserListActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

    public static final String TAG  = "SignInActivity";

    private FirebaseAuth auth;
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignUp;
    private TextView textViewToggleLogIn;
    private EditText editTextConfirmPassword;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private boolean loginModeActive = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users");
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null) {
        startActivity(new Intent(this, UserListActivity.class));
        }
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextName  = findViewById(R.id.editTextName);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignUp = findViewById(R.id.buttonLoginSignUp);
        textViewToggleLogIn = findViewById(R.id.textViewToLogIN);
        editTextConfirmPassword = findViewById(R.id.editTextRepeatPassword);





        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginSignUpUser(
                        editTextEmail.getText().toString().trim(),
                        editTextPassword.getText().toString().trim()
                );
            }
        });



        //auth.createUserWithEmailAndPassword()

    }
    private void loginSignUpUser(String email, String password){

        if(loginModeActive){

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = auth.getCurrentUser();

                                //updateUI(user);
                                startActivity(new Intent(SignInActivity.this, UserListActivity.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(SignInActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                                // ...
                            }

                            // ...
                        }
                    });

        } else {

            if (!password.equals(editTextConfirmPassword.getText().toString().trim())) {
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            } else if (editTextPassword.getText().toString().trim().length() < 6) {
                Toast.makeText(this, "Passwords must be at least 6 characters", Toast.LENGTH_SHORT).show();
            } else if (editTextEmail.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please input your Email", Toast.LENGTH_SHORT).show();
            } else if (editTextName.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please input your name or nickname", Toast.LENGTH_SHORT).show();

            } else {
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    createUser(user);
                                    //updateUI(user);
                                    Intent intent = new Intent(SignInActivity.this, UserListActivity.class);
                                    intent.putExtra("userName", editTextName.getText().toString().trim());
                                    startActivity(intent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignInActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }

                                // ...
                            }


                        });
            }

        }


    }

    private void createUser(FirebaseUser firebaseUser) {
        User user = new User();
        user.setId(firebaseUser.getUid());
        user.setEmail(firebaseUser.getEmail());
        user.setName(editTextName.getText().toString().trim());

        databaseReference.push().setValue(user);

    }

    public void onClickLogIn(View view) {
        if(loginModeActive)
        {
            loginModeActive = false;
            buttonSignUp.setText("Sign Up");
            textViewToggleLogIn.setText("Log IN");
            editTextName.setVisibility(View.VISIBLE);
            editTextConfirmPassword.setVisibility(View.VISIBLE);


        } else
        {
            loginModeActive = true;
            buttonSignUp.setText("Log In");
            textViewToggleLogIn.setText("Sign Up");
            editTextName.setVisibility(View.GONE);
            editTextConfirmPassword.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(this, UserListActivity.class));
        }
    }
}
