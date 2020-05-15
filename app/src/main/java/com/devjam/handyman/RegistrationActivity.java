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

        name_txt = findViewById(R.id.registration_name_text);
        email_txt = findViewById(R.id.registration_email_text);
        pwd_txt = findViewById(R.id.registration_password_text);
        confirm_pwd_txt = findViewById(R.id.registration_confirm_password_text);
        address_txt = findViewById(R.id.registration_address_text);
        pincode_txt = findViewById(R.id.registration_pincode_text);
        contact_txt = findViewById(R.id.registration_contact_text);

        findViewById(R.id.registration_signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!name_txt.getText().toString().isEmpty()
                        && !email_txt.getText().toString().isEmpty()
                        && !pwd_txt.getText().toString().isEmpty()
                        && !confirm_pwd_txt.getText().toString().isEmpty()
                        && !pincode_txt.getText().toString().isEmpty()
                        && !address_txt.getText().toString().isEmpty()
                        && !contact_txt.getText().toString().isEmpty()
                ){
                    if(confirm_pwd_txt.getText().toString().equals(pwd_txt.getText().toString())){
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

        pd = new ProgressDialog(RegistrationActivity.this,R.style.AppCompatAlertDialogStyle);
        pd.setMessage("Please wait...");
        pd.show();

        firebaseAuth
                .createUserWithEmailAndPassword(email_txt.getText().toString().trim(),pwd_txt.getText().toString().trim())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser firebaseUser = authResult.getUser();
                        User user = new User();
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
                                        pd.dismiss();
                                        Toast.makeText(RegistrationActivity.this,"Unable to register!",Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(RegistrationActivity.this,"An error occured!",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(RegistrationActivity.this,"Unable to register!",Toast.LENGTH_LONG).show();
            }
        });
    }
}
