package com.devjam.handyman.Ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private TextView zone_txt,available_txt;
    private ServiceCatergoryAdapter serviceCatergoryAdapter;
    private ArrayList<String> serviceCategories;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private ProgressBar progressBar1,progressBar2;
    private ScrollView scrollView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        // Initializing the required fields
        spinner = view.findViewById(R.id.home_fragment_spinner);
        zone_txt = view.findViewById(R.id.home_fragment_zone_text);
        recyclerView = view.findViewById(R.id.home_service_recycler_view);
        searchView = view.findViewById(R.id.home_service_search_view);
        progressBar1 = view.findViewById(R.id.home_fragment_progress_bar);
        scrollView = view.findViewById(R.id.home_fragment_scroll_view);
        progressBar2 = view.findViewById(R.id.home_fragment_categories_progress_bar);
        available_txt = view.findViewById(R.id.home_fragment_available_text);

        // Displaying a progress bar only until the cities get loaded
        scrollView.setVisibility(View.GONE);
        progressBar1.setVisibility(View.VISIBLE);

        // Displaying a progress bar only until the service categories under the selected city get loaded
        progressBar2.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        zone_txt.setVisibility(View.GONE);
        searchView.setVisibility(View.GONE);
        available_txt.setVisibility(View.GONE);

        // Loading the cities in a spinner
        loadCities();

        return  view;
    }

    // Method to load cities in the spinner
    private void loadCities(){

        // Initializing cities array list
        cities = new ArrayList<>();

        // Loading the cities from firebase firestore
        db.collection("Cities")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot ds : queryDocumentSnapshots){
                    // Storing the cities in the array list
                    cities.add(ds.getId());
                }
                // Displaying the cities array list in the spinner
                citiesAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,cities);
                spinner.setAdapter(citiesAdapter);

                // Hiding the progress bar when the cities get loaded
                scrollView.setVisibility(View.VISIBLE);
                progressBar1.setVisibility(View.GONE);

                // On item selected listener for spinner
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        // Displaying the progress bar until the categories get loaded
                        progressBar2.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        zone_txt.setVisibility(View.GONE);
                        searchView.setVisibility(View.GONE);
                        available_txt.setVisibility(View.GONE);

                        // Obtaining the covid-19 zone of the selected city from firestore and displaying it in a textview
                        final String selectedItem = parent.getItemAtPosition(position).toString();
                        db.collection("Cities")
                                .document(selectedItem)
                                .collection("Details")
                                .document("Zone")
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String zone = documentSnapshot.get("zone").toString();

                                        // changing the color of textview according to the zone
                                        if(zone.equals("green")){
                                            zone_txt.setText("You are in covid-19 " + zone + " zone");
                                            zone_txt.setTextColor(Color.rgb(0,128,0));
                                        }
                                        if(zone.equals("orange")){
                                            zone_txt.setText("You are in covid-19 " + zone + " zone");
                                            zone_txt.setTextColor(Color.rgb(255,165,0));
                                        }
                                        if(zone.equals("red")){
                                            zone_txt.setText("You are in covid-19 " + zone + " zone");
                                            zone_txt.setTextColor(Color.rgb(255,0,0));
                                        }
                                    }
                                });

                        // Loading the categories in the selected city from firestore
                        db.collection("Cities")
                                .document(selectedItem)
                                .collection("Services")
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                        serviceCategories = new ArrayList<>();
                                        for(QueryDocumentSnapshot ds : queryDocumentSnapshots){
                                            // Loading the categories in the array list
                                            serviceCategories.add(ds.getId());
                                        }
                                        // Displaying the data in array list in recyclerview with grid layout
                                        serviceCatergoryAdapter = new ServiceCatergoryAdapter(serviceCategories,selectedItem);
                                        recyclerView.setAdapter(serviceCatergoryAdapter);
                                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

                                        // Hiding the progress bar when the categories get loaded
                                        recyclerView.setVisibility(View.VISIBLE);
                                        progressBar2.setVisibility(View.GONE);
                                        searchView.setVisibility(View.VISIBLE);
                                        available_txt.setVisibility(View.VISIBLE);
                                        zone_txt.setVisibility(View.VISIBLE);

                                        // On query text listener for searchview to filter items in categories recyclerview
                                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                            @Override
                                            public boolean onQueryTextSubmit(String query) {
                                                serviceCatergoryAdapter.getFilter().filter(query);
                                                return false;
                                            }

                                            @Override
                                            public boolean onQueryTextChange(String newText) {
                                                serviceCatergoryAdapter.getFilter().filter(newText);
                                                return false;
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Displaying a toast message on failure in loading the categories
                                Toast.makeText(getContext(),"Unable to load services!",Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Displaying a toast message on failure in loading the cities
                Toast.makeText(getContext(),"Unable to load cities!",Toast.LENGTH_LONG).show();
            }
        });
    }
}
