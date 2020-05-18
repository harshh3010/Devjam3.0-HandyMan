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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class RegistrationActivity extends AppCompatActivity {

    private EditText name_txt,email_txt,pwd_txt,confirm_pwd_txt,address_txt,pincode_txt,contact_txt;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initializing required fields
        name_txt = findViewById(R.id.registration_name_text);
        email_txt = findViewById(R.id.registration_email_text);
        pwd_txt = findViewById(R.id.registration_password_text);
        confirm_pwd_txt = findViewById(R.id.registration_confirm_password_text);
        address_txt = findViewById(R.id.registration_address_text);
        pincode_txt = findViewById(R.id.registration_pincode_text);
        contact_txt = findViewById(R.id.registration_contact_text);

        // Adding on click listener to the SignUp button
        findViewById(R.id.registration_signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Checking for empty fields and accordingly displaying an error message
                if(!name_txt.getText().toString().isEmpty()
                        && !email_txt.getText().toString().isEmpty()
                        && !pwd_txt.getText().toString().isEmpty()
                        && !confirm_pwd_txt.getText().toString().isEmpty()
                        && !pincode_txt.getText().toString().isEmpty()
                        && !address_txt.getText().toString().isEmpty()
                        && !contact_txt.getText().toString().isEmpty()
                ){
                    // Matching the confirmation password with the created one
                    if(confirm_pwd_txt.getText().toString().equals(pwd_txt.getText().toString())){
                        // In case of no issues, calling the registerUser() method
                        registerUser();
                    }else{
                        confirm_pwd_txt.setError("Confirmation password doesn't  match with the one you created.");
                    }
                }
                if(name_txt.getText().toString().isEmpty())
                    name_txt.setError("This field cannot be left empty.");
                if(email_txt.getText().toString().isEmpty())
                    email_txt.setError("This field cannot be left empty.");
                if(pwd_txt.getText().toString().isEmpty())
                    pwd_txt.setError("This field cannot be left empty.");
                if(confirm_pwd_txt.getText().toString().isEmpty())
                    confirm_pwd_txt.setError("This field cannot be left empty.");
                if(address_txt.getText().toString().isEmpty())
                    address_txt.setError("This field cannot be left empty.");
                if(pincode_txt.getText().toString().isEmpty())
                    pincode_txt.setError("This field cannot be left empty.");
                if(contact_txt.getText().toString().isEmpty())
                    contact_txt.setError("This field cannot be left empty.");
            }
        });
    }

    private void registerUser(){

        // Displaying a progress dialog until the background tasks get completed
        pd = new ProgressDialog(RegistrationActivity.this,R.style.AppCompatAlertDialogStyle);
        pd.setMessage("Please wait...");
        pd.show();

        // Registering the user with Firebase Authentication
        firebaseAuth
                .createUserWithEmailAndPassword(email_txt.getText().toString().trim(),pwd_txt.getText().toString().trim())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        // On success, storing user's data to Firebase Firestore
                        FirebaseUser firebaseUser = authResult.getUser();
                        User user = new User();
                        assert firebaseUser != null;
                        user.setId(firebaseUser.getUid());
                        user.setName(name_txt.getText().toString().trim());
                        user.setPincode(Integer.parseInt(pincode_txt.getText().toString()));
                        user.setContact(Long.parseLong(contact_txt.getText().toString()));
                        user.setEmail(email_txt.getText().toString().trim());
                        user.setAddress(address_txt.getText().toString().trim());

                        db.collection("Users")
                                .document(user.getEmail())
                                .set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        // On successfully storing the data, dismissing the dialog, displaying a toast message and redirecting the user to login screen
                                        pd.dismiss();
                                        Toast.makeText(RegistrationActivity.this,"Unable to register!",Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // On failure, dismissing the dialog and displaying a toast message
                                pd.dismiss();
                                Toast.makeText(RegistrationActivity.this,"An error occured!",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // On failure in registering the user, displaying a toast and dismissing the progress dialog
                pd.dismiss();
                Toast.makeText(RegistrationActivity.this,"Unable to register!",Toast.LENGTH_LONG).show();
            }
        });
    }
}
