package com.devjam.handyman.Ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.devjam.handyman.R;
import com.devjam.handyman.Util.UserApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private UserApi userApi = UserApi.getInstance();
    private EditText address_txt,pincode_txt,contact_txt;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile,container,false);

        address_txt = view.findViewById(R.id.profile_fragment_address_text);
        pincode_txt = view.findViewById(R.id.profile_fragment_pincode_text);
        contact_txt = view.findViewById(R.id.profile_fragment_contact_text);

        ((TextView)view.findViewById(R.id.profile_fragment_name_text)).setText(userApi.getName());
        ((TextView)view.findViewById(R.id.profile_fragment_name_text)).setText(userApi.getName());
        ((TextView)view.findViewById(R.id.profile_fragment_name_text)).setText(userApi.getName());
        address_txt.setText(userApi.getAddress());
        pincode_txt.setText(String.valueOf(userApi.getPincode()));
        contact_txt.setText(String.valueOf(userApi.getContact()));

        view.findViewById(R.id.profile_fragment_edit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"You can edit the fields now.",Toast.LENGTH_SHORT).show();

                address_txt.setEnabled(true);
                pincode_txt.setEnabled(true);
                contact_txt.setEnabled(true);

                view.findViewById(R.id.profile_fragment_save_button).setVisibility(View.VISIBLE);
                view.findViewById(R.id.profile_fragment_edit_button).setVisibility(View.GONE);

            }
        });

        view.findViewById(R.id.profile_fragment_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();

                address_txt.setEnabled(false);
                pincode_txt.setEnabled(false);
                contact_txt.setEnabled(false);

                view.findViewById(R.id.profile_fragment_save_button).setVisibility(View.GONE);
                view.findViewById(R.id.profile_fragment_edit_button).setVisibility(View.VISIBLE);
            }
        });

        view.findViewById(R.id.profile_fragment_logout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        return  view;
    }

    private void updateData(){
        String address = address_txt.getText().toString().trim();
        int pincode = Integer.parseInt(pincode_txt.getText().toString());
        long contact = Long.parseLong(contact_txt.getText().toString());

        userApi.setAddress(address);
        userApi.setPincode(pincode);
        userApi.setContact(contact);

        db.collection("Users")
                .document(userApi.getEmail())
                .update("address",userApi.getAddress())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"Unable to update address!",Toast.LENGTH_SHORT).show();
                    }
                });
        db.collection("Users")
                .document(userApi.getEmail())
                .update("contact",userApi.getContact())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"Unable to update contact!",Toast.LENGTH_SHORT).show();
                    }
                });
        db.collection("Users")
                .document(userApi.getEmail())
                .update("pincode",userApi.getPincode())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),"Unable to update pincode!",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void logoutUser(){
        //TODO : code
    }
}
