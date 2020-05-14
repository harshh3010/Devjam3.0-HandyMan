package com.devjam.handyman;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.devjam.handyman.Model.Service;

import java.util.Calendar;

public class BookingActivity extends AppCompatActivity {

    private Service service;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    private String Date,Time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        service = (Service) getIntent().getSerializableExtra("service");

        ((TextView)findViewById(R.id.booking_title_text)).setText(service.getName());
        ((TextView)findViewById(R.id.booking_service_name_text)).setText(service.getName());
        ((TextView)findViewById(R.id.booking_service_description_text)).setText(service.getDescription());
        ((TextView)findViewById(R.id.booking_service_price_text)).setText(service.getCost());

        findViewById(R.id.booking_choose_date_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDate();
            }
        });

        findViewById(R.id.booking_choose_time_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseTime();
            }
        });

        findViewById(R.id.booking_confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmBooking();
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
                Date = dayOfMonth + "/" + (month + 1) + "/" + year ;
                ((Button)findViewById(R.id.booking_choose_date_button)).setText(Date);
            }
        };

        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Time = hourOfDay + ":" + minute;
                ((Button)findViewById(R.id.booking_choose_time_button)).setText(Time);
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
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();

    }

    private void chooseTime(){
        Calendar calendar = Calendar.getInstance();
        int hour  = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                BookingActivity.this,
                android.R.style.Theme_Holo_Light_Dialog,
                timeSetListener,
                hour,minute,android.text.format.DateFormat.is24HourFormat(BookingActivity.this)
        );
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePickerDialog.show();

    }

    private void confirmBooking(){
        //TODO : code
    }
}
