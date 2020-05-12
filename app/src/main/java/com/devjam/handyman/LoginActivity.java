package com.devjam.handyman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.devjam.handyman.Model.User;
import com.devjam.handyman.Util.UserApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private EditText email_txt,pass_txt;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UserApi userApi = UserApi.getInstance();
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_txt = findViewById(R.id.login_email_text);
        pass_txt = findViewById(R.id.login_password_text);

        findViewById(R.id.login_login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd = new ProgressDialog(LoginActivity.this);
                pd.setMessage("Please wait...");
                pd.show();

                firebaseAuth
                        .signInWithEmailAndPassword(email_txt.getText().toString().trim(),pass_txt.getText().toString().trim())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                loginUser();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(LoginActivity.this,"Unable to login!",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        findViewById(R.id.login_register_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
            }
        });
    }

    private void loginUser(){
        db.collection("Users")
                .document(email_txt.getText().toString().trim())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        userApi.setId(user.getId());
                        userApi.setName(user.getName());
                        userApi.setEmail(user.getEmail());
                        userApi.setAddress(user.getAddress());
                        userApi.setContact(user.getContact());
                        userApi.setPincode(user.getPincode());

                        pd.dismiss();
                        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(LoginActivity.this,"Unable to load user data!",Toast.LENGTH_LONG).show();
            }
        });
    }

}
