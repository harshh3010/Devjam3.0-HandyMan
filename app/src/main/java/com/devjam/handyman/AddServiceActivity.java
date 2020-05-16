package com.devjam.handyman;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.devjam.handyman.Model.Service;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class AddServiceActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RadioGroup radioGroup;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);

        radioGroup = findViewById(R.id.add_service_category_rg);

        findViewById(R.id.add_service_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addService();
            }
        });

        findViewById(R.id.add_service_logout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddServiceActivity.this,LoginActivity.class));
                finish();
            }
        });
    }

    private void addService() {

        pd = new ProgressDialog(AddServiceActivity.this, R.style.AppCompatAlertDialogStyle);
        pd.setMessage("Please wait...");
        pd.show();

        if (!((EditText) findViewById(R.id.add_service_city_text)).getText().toString().isEmpty()
                && !((EditText) findViewById(R.id.add_service_name_text)).getText().toString().isEmpty()
                && !((EditText) findViewById(R.id.add_service_desc_text)).getText().toString().isEmpty()
                && !((EditText) findViewById(R.id.add_service_price_text)).getText().toString().isEmpty()) {

            final Map<String, Object> dummyMap = new HashMap<>();

            db.collection("Cities")
                    .document(((EditText) findViewById(R.id.add_service_city_text)).getText().toString().trim())
                    .set(dummyMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Map<String, Object> data = new HashMap<>();
                            data.put("zone","green");

                            db.collection("Cities")
                                    .document(((EditText) findViewById(R.id.add_service_city_text)).getText().toString().trim())
                                    .collection("Details")
                                    .document("Zone")
                                    .set(data);

                            db.collection("Cities")
                                    .document(((EditText) findViewById(R.id.add_service_city_text)).getText().toString().trim())
                                    .collection("Services")
                                    .document(((RadioButton) findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString())
                                    .set(dummyMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Service service = new Service();
                                    service.setName(((EditText) findViewById(R.id.add_service_name_text)).getText().toString().trim());
                                    service.setDescription(((EditText) findViewById(R.id.add_service_desc_text)).getText().toString().trim());
                                    service.setCost(((EditText) findViewById(R.id.add_service_price_text)).getText().toString().trim());
                                    service.setId(((EditText) findViewById(R.id.add_service_name_text)).getText().toString().trim());

                                    db.collection("Cities")
                                            .document(((EditText) findViewById(R.id.add_service_city_text)).getText().toString().trim())
                                            .collection("Services")
                                            .document(((RadioButton) findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString())
                                            .collection("Service")
                                            .document(((EditText) findViewById(R.id.add_service_name_text)).getText().toString().trim())
                                            .set(service)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    pd.dismiss();
                                                    Toast.makeText(AddServiceActivity.this, "Service added successfully!", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            Toast.makeText(AddServiceActivity.this, "Unable to add service!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(AddServiceActivity.this, "An error occurred!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(AddServiceActivity.this, "An error occurred!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            if (((EditText) findViewById(R.id.add_service_city_text)).getText().toString().isEmpty())
                ((EditText) findViewById(R.id.add_service_city_text)).setError("This field cannot be empty.");

            if (((EditText) findViewById(R.id.add_service_name_text)).getText().toString().isEmpty())
                ((EditText) findViewById(R.id.add_service_name_text)).setError("This field cannot be empty.");

            if (((EditText) findViewById(R.id.add_service_desc_text)).getText().toString().isEmpty())
                ((EditText) findViewById(R.id.add_service_desc_text)).setError("This field cannot be empty.");

            if (((EditText) findViewById(R.id.add_service_price_text)).getText().toString().isEmpty())
                ((EditText) findViewById(R.id.add_service_price_text)).setError("This field cannot be empty.");
        }
    }
}
