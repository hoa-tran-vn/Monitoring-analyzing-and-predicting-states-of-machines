package com.example.myfinalappsproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ImageView mImage;
    private int TimeDelay = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Hooks
        mImage = findViewById(R.id.flashScreen_img);
        AnimationDisplay();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(),SignIn.class);
//                intent = new Intent(getApplicationContext(),GanttChartActivity.class);
                startActivity(intent);
                finish();
            }
        },TimeDelay);
    }

    private void AnimationDisplay() {
        Animation mfadeAnim,mScaleAnim;
        mfadeAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.faded_anim);
        mScaleAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.top_anim);
        mImage.setAnimation(mScaleAnim);
    }
}
