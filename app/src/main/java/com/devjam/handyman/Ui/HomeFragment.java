package com.devjam.handyman.Ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.devjam.handyman.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Spinner spinner;
    private ArrayList<String> cities;
    private ArrayAdapter<String> citiesAdapter;
    private TextView zone_txt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        spinner = view.findViewById(R.id.home_fragment_spinner);
        zone_txt = view.findViewById(R.id.home_fragment_zone_text);

        loadCities();

        return  view;
    }

    private void loadCities(){
        cities = new ArrayList<>();

        db.collection("Cities")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot ds : queryDocumentSnapshots){
                    cities.add(ds.getId());
                }
                citiesAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,cities);
                spinner.setAdapter(citiesAdapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedItem = parent.getItemAtPosition(position).toString();
                        db.collection("Cities")
                                .document(selectedItem)
                                .collection("Details")
                                .document("Zone")
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String zone = documentSnapshot.get("zone").toString();
                                        if(zone.equals("green")){
                                            zone_txt.setText("You are in " + zone + " zone");
                                        }
                                        if(zone.equals("orange")){
                                            zone_txt.setText("You are in " + zone + " zone");
                                        }
                                        if(zone.equals("red")){
                                            zone_txt.setText("You are in " + zone + " zone");
                                        }
                                    }
                                });

                        //TODO : change text color, load services in array.
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Unable to load cities!",Toast.LENGTH_LONG).show();
            }
        });
    }
}
