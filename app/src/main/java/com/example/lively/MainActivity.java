package com.example.lively;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public BottomNavigationView bottomNavigationView;
    public FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadFragment(new HomeFragment(), true);

        Bundle intent = getIntent().getExtras();
        if(intent != null) {
            String publisher = intent.getString("publisherid");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,
                    new ProfileFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,
                    new HomeFragment()).commit();
        }

        frameLayout = findViewById(R.id.frameLayout);
        bottomNavigationView = findViewById(R.id.bottomNavBar);

        bottomNavigationView.setOnItemSelectedListener((BottomNavigationView.OnNavigationItemSelectedListener) item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navHome) {
                loadFragment(new HomeFragment(), false);

            } else if(itemId == R.id.navSearch) {
                loadFragment(new SearchFragment(), false);

            } else if(itemId == R.id.navNotifications) {
                loadFragment(new NotificationsFragment(), false);

            } else if(itemId == R.id.navProfile) {
                loadFragment(new ProfileFragment(), false);

            } else if(itemId == R.id.navUpload) {
                Intent intent1 = new Intent(getApplicationContext(), PostActivity.class);
                startActivity(intent1);

            }else{
                loadFragment(new HomeFragment(), false);

            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment, boolean IsAppInitialized){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(IsAppInitialized){
            fragmentTransaction.add(R.id.frameLayout, fragment);

        }else{
            fragmentTransaction.replace(R.id.frameLayout, fragment);

        }
        fragmentTransaction.commit();
    }
}