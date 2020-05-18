package com.devjam.handyman;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devjam.handyman.Model.Service;
import com.devjam.handyman.Ui.ServiceAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    private String category;
    private String city;
    private RecyclerView recyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Service> services;
    private ServiceAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Initializing the required fields
        recyclerView = findViewById(R.id.category_service_recycler_view);
        progressBar = findViewById(R.id.category_activity_progress_bar);

        // Displaying the progress bar until data is loaded
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        // Loading the data fetched from Home Activity
        city = getIntent().getStringExtra("city");
        category = getIntent().getStringExtra("category");
        ((TextView)findViewById(R.id.category_title_text)).setText(category);

        // Loading the services under the current category using this method
        loadServices();

        // Closing the activity on pressing back arrow
        findViewById(R.id.category_back_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void loadServices(){

        // Loading the services under given category from firebase firestore
        db.collection("Cities")
                .document(city)
                .collection("Services")
                .document(category)
                .collection("Service")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        // On successfully loading the services adding them into an array list
                        services = new ArrayList<>();
                        for(QueryDocumentSnapshot ds : queryDocumentSnapshots){
                            services.add(ds.toObject(Service.class));
                        }

                        // Displaying the services stored in array list in a recyclerview
                        adapter = new ServiceAdapter(services,city);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(CategoryActivity.this));

                        // Hiding the progress bar after all the data is displayed in recyclerview
                        recyclerView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}
