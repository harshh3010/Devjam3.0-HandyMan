package com.devjam.handyman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.devjam.handyman.Model.User;
import com.devjam.handyman.Util.UserApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UserApi userApi = UserApi.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Delaying the process by 1 second

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // Checking is the user is already logged-in from current device

                if(firebaseAuth.getCurrentUser() != null){

                    // Loading user's data from Firebase firestore

                    db.collection("Users")
                            .document(firebaseAuth.getCurrentUser().getEmail())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    // On successfully loading the data, storing it in the userApi object

                                    User user = documentSnapshot.toObject(User.class);
                                    userApi.setId(user.getId());
                                    userApi.setName(user.getName());
                                    userApi.setEmail(user.getEmail());
                                    userApi.setAddress(user.getAddress());
                                    userApi.setContact(user.getContact());
                                    userApi.setPincode(user.getPincode());

                                    // Redirecting the user to Home Screen

                                    startActivity(new Intent(SplashScreen.this,HomeActivity.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Displaying a toast message and closing the app on failure in loading data

                            Toast.makeText(SplashScreen.this,"Unable to load user data!",Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });

                }else{

                    // If user is not logged-in redirecting him to login screen

                    startActivity(new Intent(SplashScreen.this,LoginActivity.class));
                    finish();
                }
            }
        },1000);
    }
}
