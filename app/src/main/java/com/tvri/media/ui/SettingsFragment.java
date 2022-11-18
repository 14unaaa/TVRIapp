package com.tvri.media.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tvri.media.LoginActivity;
import com.tvri.media.R;

public class SettingsFragment extends AppCompatActivity {
    private Switch darkModeSwitch;
    private TextView textView;
    private Button btnLogout;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(new DarkModePrefManager(this).isNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        setContentView(R.layout.fragment_settings);
        btnLogout=findViewById(R.id.btn_logout);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        btnLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });

        //function for enabling dark mode
        setDarkModeSwitch();
    }
    private void setDarkModeSwitch(){
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        darkModeSwitch.setChecked(new DarkModePrefManager(this).isNightMode());
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DarkModePrefManager darkModePrefManager = new DarkModePrefManager(SettingsFragment.this);
                darkModePrefManager.setDarkMode(!darkModePrefManager.isNightMode());
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                recreate();
            }

        });
    }
}