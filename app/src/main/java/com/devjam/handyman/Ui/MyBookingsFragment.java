package com.devjam.handyman.Ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devjam.handyman.Model.Booking;
import com.devjam.handyman.Model.User;
import com.devjam.handyman.R;
import com.devjam.handyman.Util.UserApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyBookingsFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookingsAdapter adapter;
    private ArrayList<Booking> bookings;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UserApi userApi = UserApi.getInstance();
    private TextView textView;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookings,container,false);

        recyclerView = view.findViewById(R.id.my_bookings_recycler_view);
        textView = view.findViewById(R.id.bookings_fragment_text_view);
        progressBar = view.findViewById(R.id.bookings_fragment_progress_bar);

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);

        loadBookings();

        return view;
    }

    private void loadBookings(){
        bookings = new ArrayList<>();
        db.collection("Users")
                .document(userApi.getEmail())
                .collection("Bookings")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot ds : queryDocumentSnapshots){
                            bookings.add(ds.toObject(Booking.class));
                        }
                        adapter = new BookingsAdapter(bookings);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                        if(bookings.isEmpty()){
                            textView.setVisibility(View.VISIBLE);
                        }else{
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Unable to load bookings!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
