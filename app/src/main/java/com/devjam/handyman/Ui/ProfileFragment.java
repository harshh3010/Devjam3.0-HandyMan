package com.devjam.handyman.Ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.devjam.handyman.LoginActivity;
import com.devjam.handyman.R;
import com.devjam.handyman.Util.UserApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private UserApi userApi = UserApi.getInstance();
    private EditText address_txt, pincode_txt, contact_txt;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private ProgressDialog pd ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initializing the required fields
        address_txt = view.findViewById(R.id.profile_fragment_address_text);
        pincode_txt = view.findViewById(R.id.profile_fragment_pincode_text);
        contact_txt = view.findViewById(R.id.profile_fragment_contact_text);

        // Displaying user's info in their respective fields
        ((TextView) view.findViewById(R.id.profile_fragment_name_text)).setText(userApi.getName());
        ((TextView) view.findViewById(R.id.profile_fragment_email_text)).setText(userApi.getEmail());
        address_txt.setText(userApi.getAddress());
        pincode_txt.setText(String.valueOf(userApi.getPincode()));
        contact_txt.setText(String.valueOf(userApi.getContact()));

        // on click listener for make changes button
        view.findViewById(R.id.profile_fragment_edit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "You can edit the fields now.", Toast.LENGTH_SHORT).show();

                address_txt.setEnabled(true);
                pincode_txt.setEnabled(true);
                contact_txt.setEnabled(true);

                view.findViewById(R.id.profile_fragment_save_button).setVisibility(View.VISIBLE);
                view.findViewById(R.id.profile_fragment_edit_button).setVisibility(View.GONE);

            }
        });

        // On click listener for save changes button
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

        // On click listener for logout button
        view.findViewById(R.id.profile_fragment_logout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        // On click listener for delete account button
        view.findViewById(R.id.profile_fragment_delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });
        return view;
    }

    // Method to update user's data
    private void updateData() {

        // Fetch new data from fields and store in userApi object
        String address = address_txt.getText().toString().trim();
        int pincode = Integer.parseInt(pincode_txt.getText().toString());
        long contact = Long.parseLong(contact_txt.getText().toString());
        userApi.setAddress(address);
        userApi.setPincode(pincode);
        userApi.setContact(contact);

        // Update the data to firestore
        db.collection("Users")
                .document(userApi.getEmail())
                .update("address", userApi.getAddress())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Unable to update address!", Toast.LENGTH_SHORT).show();
                    }
                });
        db.collection("Users")
                .document(userApi.getEmail())
                .update("contact", userApi.getContact())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Unable to update contact!", Toast.LENGTH_SHORT).show();
                    }
                });
        db.collection("Users")
                .document(userApi.getEmail())
                .update("pincode", userApi.getPincode())
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Unable to update pincode!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to logout user
    private void logoutUser() {

        // Show confirmation dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()), R.style.MyAlertDialogStyle);
        builder.setTitle("Sign-out");
        builder.setMessage("Do you really wish to sign out?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                // Signing-out the firebase authentication user and redirecting to login screen
                firebaseAuth.signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                Objects.requireNonNull(getActivity()).finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    // Method to delete account
    private void deleteAccount() {
        // Displaying a dialog to ask user for account password
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle);
        builder.setTitle("Delete Account");
        builder.setMessage("Please enter your password to continue.");
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(5, 5, 5, 5);
        input.setLayoutParams(lp);
        builder.setView(input);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                String password = input.getText().toString().trim();
                if (password.isEmpty()) {
                    input.setError("Please enter password to proceed.");
                } else {
                    // Reauthenticate the user with email and entered password
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(userApi.getEmail(), password);
                    Objects.requireNonNull(firebaseAuth.getCurrentUser()).reauthenticate(credential)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Deleting the account on success
                                    dialog.dismiss();
                                    removeUserData();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(getContext(), "Incorrect password!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    //  Method to remove user's data
    private void removeUserData(){

        // Displaying a progress dialog until the background tasks are performed
        pd = new ProgressDialog(getContext(),R.style.AppCompatAlertDialogStyle);
        pd.setMessage("Please wait...");
        pd.show();

        // Delete the firebase user
        Objects.requireNonNull(firebaseAuth.getCurrentUser())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // Deleting the user's data from firestore
                        db.collection("Users")
                                .document(userApi.getEmail())
                                .delete();
                        pd.dismiss();
                        Objects.requireNonNull(getActivity()).finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                // On failure in deleting the user, displayig a toast message
                pd.dismiss();
                Toast.makeText(getContext(),"Unable to remove account!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}


