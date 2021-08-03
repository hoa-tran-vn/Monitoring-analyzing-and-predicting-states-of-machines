package com.example.myfinalappsproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;

public class Weeks extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    ImageView backBtn;
    LineChart mLineChart;
    BarChart mBarChart;
    BottomNavigationView mBottomNavigation;
    LinearLayout mCardOne,mCardTwo;
    TextView mPowerTxt,mPowerDescTxt,mProductivityTxt,mProductivityDescTxt, mNotificationNumber;
    ImageView mNotificationBell;
    private long TimeReMaining = 3600000;// Time limit to execute the DataDisPlays
    private long TimeRepeatTask = 60000; // Repeat the task with cycle of 60.000 milliSecond
    SharedPreferences sharedPreferences;
    Runnable runnable;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weeks);
        sharedPreferences = getSharedPreferences("MessageCounting",MODE_PRIVATE);

        hook();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (sharedPreferences.getInt("NumberOfMessage",0) != 0){
                    mNotificationNumber.setVisibility(View.VISIBLE);
                    mNotificationNumber.setText(""+sharedPreferences.getInt("NumberOfMessage",0)+"");
                }
                TimeReMaining = TimeReMaining - TimeRepeatTask; // Reduce time by TimeRepeatTask
                handler.postDelayed(this,TimeRepeatTask);  // Do the task after TimeRepeatTask
            }
        };
//        handler.postDelayed(runnable,500); // Original time value
        BarChartDisPlay();
        LineChartDisPlay();
        BottomNavigation();
        AnimationDisPlay();
        Notification();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    @Override
    protected void onResume() {
        handler.postDelayed(runnable,500);
        super.onResume();
    }

    private void Notification() {
        mNotificationBell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Notification_Activity.class);
                startActivity(intent);
            }
        });


    }

    private void BottomNavigation() {
        mBottomNavigation.setOnNavigationItemSelectedListener(this);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void Back(View view){
        onBackPressed();
    }

    private void BarChartDisPlay(){
        ArrayList<BarEntry> power = new ArrayList<>();

        power.add(new BarEntry(2,550));
        power.add(new BarEntry(3,600));
        power.add(new BarEntry(4,400));
        power.add(new BarEntry(5,450));
        power.add(new BarEntry(6,400));
        power.add(new BarEntry(7,550));

        BarDataSet barDataSet = new BarDataSet(power,"Power");
        barDataSet.setColor(getResources().getColor(R.color.ButtonColor_four));
        barDataSet.setValueTextColor(getResources().getColor(R.color.TextColor_four_opt_two));
        barDataSet.setValueTextSize(10f);


        BarData barData = new BarData(barDataSet);
        mBarChart.setData(barData);
        mBarChart.setFitBars(true);
        mBarChart.getDescription().setEnabled(false);
        mBarChart.animateX(2000);
        mBarChart.animateY(1500);
        mBarChart.getXAxis().setDrawGridLines(false);
        mBarChart.getAxisLeft().setDrawGridLines(false);
        mBarChart.getAxisRight().setDrawGridLines(false);
        mBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mBarChart.getAxisRight().setEnabled(false);
        mBarChart.getAxisLeft().setEnabled(false);
        mBarChart.getLegend().setEnabled(false);
        mBarChart.getXAxis().setTextColor(getResources().getColor(R.color.TextColor_four_opt_two));
    }

    private void LineChartDisPlay() {

        ArrayList<Entry> products = new ArrayList<>();

        products.add(new Entry(2,50));
        products.add(new Entry(3,70));
        products.add(new Entry(4,80));
        products.add(new Entry(5,40));
        products.add(new Entry(6,90));
        products.add(new Entry(7,10));

        LineDataSet lineDataSet = new LineDataSet(products,"Productivity");
        lineDataSet.setValueTextSize(10f);
        lineDataSet.setCircleRadius(5);
        lineDataSet.setLineWidth(3);
        lineDataSet.setColor(getResources().getColor(R.color.ButtonColor_four));
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillAlpha(20);
        lineDataSet.setFillColor(getResources().getColor(R.color.ButtonColor_four));
        lineDataSet.setCircleColor(Color.YELLOW);
        lineDataSet.setValueTextColor(getResources().getColor(R.color.TextColor_four_opt_two));
        lineDataSet.setDrawCircleHole(false);

        LineData lineData = new LineData(lineDataSet);
        mLineChart.setData(lineData);
        mLineChart.getDescription().setEnabled(false);
        mLineChart.animateX(2000);
        mLineChart.setDrawGridBackground(false);
        mLineChart.getXAxis().setDrawGridLines(false);
        mLineChart.getAxisLeft().setDrawGridLines(false);
        mLineChart.getAxisRight().setDrawGridLines(false);
        mLineChart.getAxisRight().setEnabled(false);
        mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mLineChart.getAxisLeft().setEnabled(false);
        mLineChart.getLegend().setEnabled(false);
        mLineChart.getXAxis().setTextColor(getResources().getColor(R.color.TextColor_four_opt_two));
     }

    private void hook() {
        backBtn = findViewById(R.id.Weeks_menuBtn);
        mLineChart = findViewById(R.id.Weeks_LineChart);
        mBarChart = findViewById(R.id.Weeks_BarChart);
        mBottomNavigation = findViewById(R.id.Weeks_Bottom_nav);
        mCardOne = findViewById(R.id.Weeks_Card_one);
        mCardTwo = findViewById(R.id.Weeks_Card_two);
        mPowerTxt = findViewById(R.id.Weeks_Power);
        mPowerDescTxt = findViewById(R.id.Weeks_PowerDesc);
        mProductivityTxt = findViewById(R.id.Weeks_Productivity);
        mProductivityDescTxt = findViewById(R.id.Weeks_ProductivityDesc);
        mNotificationBell = findViewById(R.id.Weeks_notificationBell);
        mNotificationNumber = findViewById(R.id.Weeks_NotificationNumber);
    }

    private void AnimationDisPlay() {
        Animation mTextAnim,mCardAnim;
        mTextAnim = AnimationUtils.loadAnimation(Weeks.this,R.anim.text_anim);
        mCardAnim = AnimationUtils.loadAnimation(Weeks.this,R.anim.card_anim);
        mCardOne.setAnimation(mCardAnim);
        mCardTwo.setAnimation(mCardAnim);
        mPowerTxt.setAnimation(mTextAnim);
        mPowerDescTxt.setAnimation(mTextAnim);
        mProductivityTxt.setAnimation(mTextAnim);
        mProductivityDescTxt.setAnimation(mTextAnim);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){

            case R.id.nav_home:
            {
                Intent intent = new Intent(getApplicationContext(),DashBoard.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.nav_days:
            {
                Intent intent = new Intent(getApplicationContext(),Days.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_weeks:
                Intent intent = new Intent(getApplicationContext(),Weeks.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
