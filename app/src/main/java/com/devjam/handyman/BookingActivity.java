package com.devjam.handyman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        service = (Service) getIntent().getSerializableExtra("service");

        ((TextView) findViewById(R.id.booking_title_text)).setText(service.getName());
        ((TextView) findViewById(R.id.booking_service_name_text)).setText(service.getName());
        ((TextView) findViewById(R.id.booking_service_description_text)).setText(service.getDescription());
        ((TextView) findViewById(R.id.booking_service_price_text)).setText(service.getCost());

        findViewById(R.id.booking_choose_date_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDate();
            }
        });

        findViewById(R.id.booking_choose_time_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Date != null) {
                    chooseTime();
                } else {
                    Toast.makeText(BookingActivity.this, "Please select a date first.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        findViewById(R.id.booking_confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Time != null) {
                    confirmBooking();
                } else {
                    Toast.makeText(BookingActivity.this, "Please select a time first.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.booking_back_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

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

        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                Calendar today = Calendar.getInstance();
                Calendar myDate = Calendar.getInstance();

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

    private void chooseDate() {
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

    private void chooseTime() {
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

    private void confirmBooking() {

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

        String id = db.collection("Users")
                .document(userApi.getEmail())
                .collection("Bookings")
                .document()
                .getId();

        booking.setId(id);

        db.collection("Users")
                .document(userApi.getEmail())
                .collection("Bookings")
                .document(id)
                .set(booking).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd.dismiss();
                Toast.makeText(BookingActivity.this, "Booking confirmed!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(BookingActivity.this, "An error occurred!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static boolean isAfterToday(int year, int month, int day) {
        Calendar today = Calendar.getInstance();
        Calendar myDate = Calendar.getInstance();

        myDate.set(year, month, day);

        return !myDate.before(today);
    }

}
