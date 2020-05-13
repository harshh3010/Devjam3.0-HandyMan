package com.devjam.handyman;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.devjam.handyman.Model.Service;
import com.devjam.handyman.Ui.ServiceAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        recyclerView = findViewById(R.id.category_service_recycler_view);

        city = getIntent().getStringExtra("city");
        category = getIntent().getStringExtra("category");
        ((TextView)findViewById(R.id.category_title_text)).setText(category);

        loadServices();

        findViewById(R.id.category_back_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void loadServices(){
        db.collection("Cities")
                .document(city)
                .collection("Services")
                .document(category)
                .collection("Service")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        services = new ArrayList<>();
                        for(QueryDocumentSnapshot ds : queryDocumentSnapshots){
                            services.add(ds.toObject(Service.class));
                        }
                        adapter = new ServiceAdapter(services,city);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(CategoryActivity.this
                        ));
                    }
                });
    }
}
