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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(firebaseAuth.getCurrentUser() != null){
                    db.collection("Users")
                            .document(firebaseAuth.getCurrentUser().getEmail())
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

                                    startActivity(new Intent(SplashScreen.this,HomeActivity.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SplashScreen.this,"Unable to load user data!",Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    startActivity(new Intent(SplashScreen.this,LoginActivity.class));
                    finish();
                }
            }
        },1000);
    }
}
