package com.example.myfinalappsproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserProfile extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    BottomNavigationView mBottomNavigation;
    SharedPreferences sharedPreferences;
    TextView mUserName,mUserPassWord,mUserIdentityName,mUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        sharedPreferences =  getSharedPreferences("DataFromUserSignIn",MODE_PRIVATE);
        Hook();
        BottomNavigation();
        UserInformationDisPlay();
    }

    private void UserInformationDisPlay() {
        mUserName.setText(sharedPreferences.getString("UserName","Null"));
        mUserIdentityName.setText(sharedPreferences.getString("UserIndentifyName","Null"));
        mUserPassWord.setText("****");
        mUserEmail.setText(sharedPreferences.getString("UserEmail","Null"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void Back(View view){
        onBackPressed();
    }

    private void BottomNavigation() {
        mBottomNavigation.setOnNavigationItemSelectedListener(this);
    }

    private void Hook() {
        mUserName = findViewById(R.id.UserProfile_fullName_TxtView);
        mUserIdentityName =  findViewById(R.id.UserProfile_ID_TxtView);
        mUserPassWord = findViewById(R.id.UserProfile_passWord_TxtView);
        mUserEmail = findViewById(R.id.UserProfile_Email_TxtView);
        mBottomNavigation = findViewById(R.id.Profile_bottom_nav);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.nav_days:{
                Intent intent = new Intent(getApplicationContext(),Days.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.nav_home:{
                Intent intent = new Intent(getApplicationContext(),DashBoard.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.nav_weeks:{
                Intent intent = new Intent(getApplicationContext(),Weeks.class);
                startActivity(intent);
                finish();
                break;
            }

        }
        return true;
    }
}
