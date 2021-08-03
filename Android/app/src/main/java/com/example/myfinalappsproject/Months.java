package com.example.myfinalappsproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class Months extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    PieChart mPieChart;
    BarChart mBarChart;
    BottomNavigationView mBottomNavigation;
    LinearLayout mCardOne,mCardTwo;
    TextView mPowerTxt,mPowerDescTxt,mProductivityTxt,mProductivityDescTxt,mNotificationNumber;
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
        setContentView(R.layout.activity_months);
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
        BarChartDisPlay();
        BottomNavigation();
        PieChartDisPlay();
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
        handler.postDelayed(runnable,500); // Original time value
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

    private void BarChartDisPlay() {
        ArrayList<BarEntry> power = new ArrayList<>();

        power.add(new BarEntry(1,1000));
        power.add(new BarEntry(2,1200));
        power.add(new BarEntry(3,1400));
        power.add(new BarEntry(4,900));

        BarDataSet barDataSet = new BarDataSet(power,"Power");
        barDataSet.setColor(getResources().getColor(R.color.ButtonColor_four));
        barDataSet.setValueTextColor(getResources().getColor(R.color.TextColor_three));
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
        mBarChart.getAxisLeft().setEnabled(false);
        mBarChart.getAxisRight().setEnabled(false);
        mBarChart.getLegend().setEnabled(false);
        mBarChart.getXAxis().setTextColor(getResources().getColor(R.color.TextColor_four_opt_two));


    }

    private void PieChartDisPlay() {
        ArrayList<PieEntry> products = new ArrayList<>();
        products.add(new PieEntry(90,"Week1"));
        products.add(new PieEntry(80,"Week2"));
        products.add(new PieEntry(90,"Week3"));
        products.add(new PieEntry(70,"Week4"));


        PieDataSet pieDataSet = new PieDataSet(products,"Productivity");
        pieDataSet.setColors(new int[]{getResources().getColor(R.color.ButtonColor_four),getResources().getColor(R.color.PieChart_color_one),getResources().getColor(R.color.IconColor_three),getResources().getColor(R.color.PieChart_color_two)});
        pieDataSet.setValueTextColor(getResources().getColor(R.color.TextColor_four_opt_two));
        pieDataSet.setValueTextSize(10f);

        PieData pieData = new PieData(pieDataSet);
        mPieChart.setData(pieData);
        mPieChart.getDescription().setEnabled(false);
        mPieChart.animate();
        mPieChart.setHoleColor(R.color.CardColor_three);
        mPieChart.getLegend().setEnabled(false);
    }

    private void AnimationDisPlay() {
        Animation mTextAnim,mCardAnim;
        mTextAnim = AnimationUtils.loadAnimation(Months.this,R.anim.text_anim);
        mCardAnim = AnimationUtils.loadAnimation(Months.this,R.anim.card_anim);
        mCardOne.setAnimation(mCardAnim);
        mCardTwo.setAnimation(mCardAnim);
        mPowerTxt.setAnimation(mTextAnim);
        mPowerDescTxt.setAnimation(mTextAnim);
        mProductivityTxt.setAnimation(mTextAnim);
        mProductivityDescTxt.setAnimation(mTextAnim);
    }

    private void hook() {
        mBarChart = findViewById(R.id.Months_BarChart);
        mPieChart = findViewById(R.id.Months_PieChart);
        mBottomNavigation = findViewById(R.id.Months_Bottom_nav);
        mCardOne = findViewById(R.id.Months_Card_one);
        mCardTwo = findViewById(R.id.Months_Card_two);
        mPowerTxt = findViewById(R.id.Months_Power);
        mPowerDescTxt = findViewById(R.id.Months_PowerDesc);
        mProductivityTxt = findViewById(R.id.Months_Productivity);
        mProductivityDescTxt = findViewById(R.id.Months_ProductivityDesc);
        mNotificationBell = findViewById(R.id.Months_notificationBell);
        mNotificationNumber = findViewById(R.id.Months_NotificationNumber);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Pair[] pairs = new Pair[2];
        pairs[0] = new Pair<View, String>(mNotificationBell, "Notification_transition");
        pairs[1] = new Pair<View, String>(mNotificationNumber, "NotificationNumber_transition");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Months.this, pairs);
        switch (menuItem.getItemId()){
            case R.id.nav_home:
            {
                Intent intent = new Intent(getApplicationContext(),DashBoard.class);
                startActivity(intent,options.toBundle());
                finish();
                break;
            }
            case R.id.nav_days:
            {
                Intent intent = new Intent(getApplicationContext(),Days.class);
                startActivity(intent,options.toBundle());
                break;
            }
            case R.id.nav_weeks: {
                Intent intent = new Intent(getApplicationContext(), Weeks.class);
                startActivity(intent,options.toBundle());
                break;
            }
        }
        return true;
    }
}
