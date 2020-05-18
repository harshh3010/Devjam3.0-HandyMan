package com.devjam.handyman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.devjam.handyman.Model.Booking;
import com.devjam.handyman.Model.Service;
import com.devjam.handyman.Util.UserApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Objects;

public class BookingActivity extends AppCompatActivity {

    private Service service;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    private String Date, Time;
    private int d, m, y;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UserApi userApi = UserApi.getInstance();
    private ProgressDialog pd;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Fetching the service object from category activity
        service = (Service) getIntent().getSerializableExtra("service");

        // Displaying the service details in respective fields
        ((TextView) findViewById(R.id.booking_title_text)).setText(service.getName());
        ((TextView) findViewById(R.id.booking_service_name_text)).setText(service.getName());
        ((TextView) findViewById(R.id.booking_service_description_text)).setText("Description : " + service.getDescription());
        if(service.getCost().equals("0")){
            ((TextView) findViewById(R.id.booking_service_price_text)).setText("Cost : Not Fixed");
        }else{
            ((TextView) findViewById(R.id.booking_service_price_text)).setText("Cost : Rs. " + service.getCost());
        }

        // on click listener for choose date button
        findViewById(R.id.booking_choose_date_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDate();
            }
        });

        // on click listener for choose time button
        findViewById(R.id.booking_choose_time_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Allowing choosing the time only after date has been chosen
                if (Date != null) {
                    chooseTime();
                } else {
                    Toast.makeText(BookingActivity.this, "Please select a date first.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // on click listener for Confirm Booking button
        findViewById(R.id.booking_confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Performing task only after time has been chosen
                if (Time != null) {
                    confirmBooking();
                } else {
                    Toast.makeText(BookingActivity.this, "Please select a time first.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Closing the activity on pressing back button
        findViewById(R.id.booking_back_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Creating listener for Date picker dialog
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                // Displaying the chosen date on the button
                if (isAfterToday(year, month, dayOfMonth)) {
                    Date = dayOfMonth + "/" + (month + 1) + "/" + year;
                    ((Button) findViewById(R.id.booking_choose_date_button)).setText(Date);
                    d = dayOfMonth;
                    m = month;
                    y = year;
                } else {
                    Toast.makeText(BookingActivity.this, "Seems like the date you picked would never come.", Toast.LENGTH_LONG).show();
                }

            }
        };

        // Creating listener for Time picker dialog
        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                Calendar today = Calendar.getInstance();
                Calendar myDate = Calendar.getInstance();

                // Displaying the chosen time on the button after ensuring that the time chosen is 1 hour later from current time
                myDate.set(y, m, d);
                if (myDate.equals(today)) {
                    Calendar datetime = Calendar.getInstance();
                    Calendar c = Calendar.getInstance();
                    datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    datetime.set(Calendar.MINUTE, minute);

                    if (datetime.getTimeInMillis() > c.getTimeInMillis() + 1000 * 60 * 60) {
                        Time = hourOfDay + ":" + minute;
                        ((Button) findViewById(R.id.booking_choose_time_button)).setText(Time);
                    } else {
                        Toast.makeText(BookingActivity.this, "Please pick a time 1 hour or greater from now.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Time = hourOfDay + ":" + minute;
                    ((Button) findViewById(R.id.booking_choose_time_button)).setText(Time);
                }
            }
        };
    }

    // Method to choose a date
    private void chooseDate() {

        // Displaying a dialog for choosing a date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                BookingActivity.this,
                android.R.style.Theme_Holo_Light_Dialog,
                dateSetListener,
                year, month, day);
        Objects.requireNonNull(datePickerDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();

    }

    // Method to choose a time
    private void chooseTime() {

        // Displaying a dialog for choosing a time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                BookingActivity.this,
                android.R.style.Theme_Holo_Light_Dialog,
                timeSetListener,
                hour, minute, android.text.format.DateFormat.is24HourFormat(BookingActivity.this)
        );
        Objects.requireNonNull(timePickerDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.show();

    }

    // Method to confirm the booking
    private void confirmBooking() {

        // Displaying a progress dialog until background tasks are over
        pd = new ProgressDialog(BookingActivity.this, R.style.AppCompatAlertDialogStyle);
        pd.setMessage("Please wait...");
        pd.show();

        Booking booking = new Booking();
        booking.setId("docId");
        booking.setServiceId(service.getName());
        booking.setTime(Time);
        booking.setDate(Date);
        booking.setStatus("Pending");
        booking.setPrice(service.getCost());

        // Getting the id of newly created firestore document and updating the booking object
        String id = db.collection("Users")
                .document(userApi.getEmail())
                .collection("Bookings")
                .document()
                .getId();

        booking.setId(id);

        // Uploading the data to firebase firestore
        db.collection("Users")
                .document(userApi.getEmail())
                .collection("Bookings")
                .document(id)
                .set(booking).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // On success, dismissing the dialog, displaying a toast and closing the activity
                pd.dismiss();
                Toast.makeText(BookingActivity.this, "Booking confirmed!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // On failure, dismissing the dialog and displaying a toast message
                pd.dismiss();
                Toast.makeText(BookingActivity.this, "An error occurred!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to check if the entered date is after today or not
    private static boolean isAfterToday(int year, int month, int day) {
        Calendar today = Calendar.getInstance();
        Calendar myDate = Calendar.getInstance();

        myDate.set(year, month, day);

        return !myDate.before(today);
    }

}
