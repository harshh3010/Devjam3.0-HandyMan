package com.devjam.handyman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.devjam.handyman.Ui.HomeFragment;
import com.devjam.handyman.Ui.MyBookingsFragment;
import com.devjam.handyman.Ui.ProfileFragment;
import com.devjam.handyman.Util.UserApi;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private UserApi userApi = UserApi.getInstance();
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initializing the required fields
        drawerLayout = findViewById(R.id.home_drawer_layout);
        NavigationView navigationView = findViewById(R.id.home_nav_view);
        View view = navigationView.getHeaderView(0);
        TextView name_txt = view.findViewById(R.id.header_home_nav_name_text);
        TextView email_txt = view.findViewById(R.id.header_home_nav_email_text);

        // Setting the Name and Email of the user in Navigation Header
        name_txt.setText(userApi.getName());
        email_txt.setText(userApi.getEmail());

        // Attaching item click listener to Navigation View
        navigationView.setNavigationItemSelectedListener(this);

        // Opening the drawer on clicking the menu icon
        findViewById(R.id.home_drawer_menu_image).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RtlHardcoded")
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.navigation_home);
        }

    }

    // Closing the opened drawer on pressing back button
    @SuppressLint("RtlHardcoded")
    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        // Performing the corresponding tasks on clicking the Navigation menu items
        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment_container, new HomeFragment()).commit();
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.navigation_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment_container, new ProfileFragment()).commit();
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.navigation_bookings:
                getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment_container, new MyBookingsFragment()).commit();
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.navigation_share:
                shareApp();
                break;
            case R.id.navigation_help:
                Toast.makeText(HomeActivity.this,"For any queries, you can mail us at harsh.gyanchandani@gmail.com",Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }

    // A function for sharing the handyman app
    private void shareApp() {

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");

        String message = "Unable to get basic home services during this lockdown?\nDownload HandyMan App, we will be ready for help 24x7.\n\ndownloadlinkforhandyman.com";
        String subject = "Download HandyMan App";

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(android.content.Intent.EXTRA_TEXT, message);

        startActivity(Intent.createChooser(intent, "Choose an app"));
    }
}
