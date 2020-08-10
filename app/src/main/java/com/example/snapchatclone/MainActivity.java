package com.example.snapchatclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText emailEditText;
    EditText passwordEditText;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser()!=null)
        {
            login();
        }
    }

    public void loginClicked(View view)
    {

        if(emailEditText.getText().toString().equals("") || passwordEditText.getText().toString().equals(""))
        {
            Toast.makeText(this, "Incorrect Credentials", Toast.LENGTH_LONG).show();
            return;
        }
        //check if we can login the user

        mAuth.signInWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            login();

                        }
                        else {

                            signup();

                        }
                    }
                });


    }

    public void login()
    {
        Intent intent = new Intent(getApplicationContext(), SnapsActivity.class);
        startActivity(intent);
    }

    public void signup()
    {
        mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid()).child("Email").setValue(emailEditText.getText().toString());
                            login();

                        } else {

                            Toast.makeText(MainActivity.this, "Login Failed, Try Again", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }


}