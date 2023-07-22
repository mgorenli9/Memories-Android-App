package com.mehmet.memories.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mehmet.memories.R;
import com.mehmet.memories.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
//yedek
    Button btnSignIn,btnSignUp;

    private ActivityMainBinding binding;
    //1.
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //3.
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // initilaze
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        //2.
        firebaseAuth = FirebaseAuth.getInstance();

        //6. kontrol : kullanıcı log in ise tekrar giriş yapmak gerekmez
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null){
            Intent intent = new Intent(MainActivity.this, FeedActivity.class);
            startActivity(intent);
            finish();
        }

        //5.
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = binding.editTxtEmail.getText().toString();
                String password = binding.editTxtPassword.getText().toString();

                if(email.equals("") || email.equals("")){
                    Toast.makeText(MainActivity.this,"Email or password cannot be empty",Toast.LENGTH_SHORT).show();
                }
                else{
                    firebaseAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(MainActivity.this, "Signed in successfully!",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, FeedActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Couldn't Sign up!",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        //4.
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = binding.editTxtEmail.getText().toString();
                String password = binding.editTxtPassword.getText().toString();

                if (email.equals("") || password.equals(""))
                {
                    Toast.makeText(MainActivity.this,"Email or password cannot be empty",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(MainActivity.this, "Signed up successfully!",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, FeedActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Couldn't Sign up!",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

}