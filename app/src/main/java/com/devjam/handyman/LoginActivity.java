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

        // Initializing the input EditTexts
        email_txt = findViewById(R.id.login_email_text);
        pass_txt = findViewById(R.id.login_password_text);

        // Setting the on click action of login button
        findViewById(R.id.login_login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Displaying a progress dialog while background tasks are performed
                pd = new ProgressDialog(LoginActivity.this,R.style.AppCompatAlertDialogStyle);
                pd.setMessage("Please wait...");
                pd.show();

                // Logging-in the user with entered email and password using Firebase Authentication
                firebaseAuth
                        .signInWithEmailAndPassword(email_txt.getText().toString().trim(),pass_txt.getText().toString().trim())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                // On successfully logging-in calling the loginUser() method
                                loginUser();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Dismissing the progress dialog and displaying a toast message on failure
                        pd.dismiss();
                        Toast.makeText(LoginActivity.this,"Unable to login!",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // Redirecting the user to Registration Screen on clicking the register now text
        findViewById(R.id.login_register_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
            }
        });

        // calling the loginAsAdmin() method on clicking the login as admin text
        findViewById(R.id.login_admin_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAsAdmin();
            }
        });
    }

    private void loginUser(){

        // Loading user's data from Firebase Firestore according to the email provided
        db.collection("Users")
                .document(email_txt.getText().toString().trim())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        // Storing the data in userApi object after success
                        User user = documentSnapshot.toObject(User.class);
                        assert user != null;
                        userApi.setId(user.getId());
                        userApi.setName(user.getName());
                        userApi.setEmail(user.getEmail());
                        userApi.setAddress(user.getAddress());
                        userApi.setContact(user.getContact());
                        userApi.setPincode(user.getPincode());

                        // Dismissing the progress dialog and redirecting to the Home Screen
                        pd.dismiss();
                        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                // On failure, dismissing the dialog and displaying a toast message
                pd.dismiss();
                Toast.makeText(LoginActivity.this,"Unable to load user data!",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loginAsAdmin(){

        // Redirecting the user to AddServiceActivity upon providing specific sign-in credentials
        if(email_txt.getText().toString().trim().equals("harsh.gyanchandani@gmail.com") && pass_txt.getText().toString().equals("harsh123")){
            startActivity(new Intent(LoginActivity.this,AddServiceActivity.class));
            finish();
        }else{
            Toast.makeText(LoginActivity.this,"Please enter valid email and password.",Toast.LENGTH_LONG).show();
        }
    }
}
