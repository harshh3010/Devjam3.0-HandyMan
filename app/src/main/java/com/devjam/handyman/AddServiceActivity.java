package com.devjam.handyman;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.FirebaseFirestore;

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

        // Initializing the required fields
        radioGroup = findViewById(R.id.add_service_category_rg);

        // on click listener for Add Service Button
        findViewById(R.id.add_service_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addService();
            }
        });


        // Redirecting to login activity after clicking the logout button
        findViewById(R.id.add_service_logout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddServiceActivity.this,LoginActivity.class));
                finish();
            }
        });
    }

    // method to add service to firebase firestore
    private void addService() {

        // Displaying a progress dialog until background tasks are over
        pd = new ProgressDialog(AddServiceActivity.this, R.style.AppCompatAlertDialogStyle);
        pd.setMessage("Please wait...");
        pd.show();

        // Ensuring that the required fields are not left empty
        if (!((EditText) findViewById(R.id.add_service_city_text)).getText().toString().isEmpty()
                && !((EditText) findViewById(R.id.add_service_name_text)).getText().toString().isEmpty()
                && !((EditText) findViewById(R.id.add_service_desc_text)).getText().toString().isEmpty()
                && !((EditText) findViewById(R.id.add_service_price_text)).getText().toString().isEmpty()) {

            // Creating a dummy data to upload while creating documents
            final Map<String, Object> dummyMap = new HashMap<>();

            // Creating a document with the city's name
            db.collection("Cities")
                    .document(((EditText) findViewById(R.id.add_service_city_text)).getText().toString().trim())
                    .set(dummyMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Map<String, Object> data = new HashMap<>();
                            data.put("zone","green");

                            //TODO : load zone from firestore collection

                            // Initially setting the city as green zone
                            db.collection("Cities")
                                    .document(((EditText) findViewById(R.id.add_service_city_text)).getText().toString().trim())
                                    .collection("Details")
                                    .document("Zone")
                                    .set(data);

                            // Creating a document based on selected category
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

                                    // Creating a document with service name and storing the service object in this document
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

                                                    // On successfully storing data, dismissing the dialog and displaying a toast message
                                                    pd.dismiss();
                                                    Toast.makeText(AddServiceActivity.this, "Service added successfully!", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            // On failure, dismissing the dialog and displaying a toast message
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

            // Displaying error message if required fields are not filled
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
