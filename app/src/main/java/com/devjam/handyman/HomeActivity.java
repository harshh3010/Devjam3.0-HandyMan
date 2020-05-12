package com.devjam.handyman;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.devjam.handyman.Ui.HomeFragment;
import com.devjam.handyman.Ui.NotificationFragment;
import com.devjam.handyman.Ui.ProfileFragment;
import com.devjam.handyman.Util.UserApi;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private UserApi userApi = UserApi.getInstance();
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView name_txt,email_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.home_drawer_layout);
        navigationView = findViewById(R.id.home_nav_view);
        View view = navigationView.getHeaderView(0);
        name_txt = view.findViewById(R.id.nav_header_name_text);
        email_txt = view.findViewById(R.id.nav_header_email_text);

        navigationView.setNavigationItemSelectedListener(this);

        findViewById(R.id.home_drawer_menu_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment_container,new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.navigation_home);
        }

    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(Gravity.LEFT)){
            drawerLayout.closeDrawer(Gravity.LEFT);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.navigation_home :
                getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment_container,new HomeFragment()).commit();
                break;
            case R.id.navigation_notifications :
                getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment_container, new NotificationFragment()).commit();
                break;
            case R.id.navigation_profile :
                getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment_container, new ProfileFragment()).commit();
                break;
        }

        drawerLayout.closeDrawer(Gravity.LEFT);
        return true;
    }
}
