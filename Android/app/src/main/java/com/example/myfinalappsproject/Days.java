package com.example.myfinalappsproject;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import org.joda.time.LocalDateTime;
import org.odata4j.consumer.ODataClientRequest;
import org.odata4j.consumer.ODataConsumers;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.behaviors.OClientBehavior;
import org.odata4j.core.ODataVersion;
import org.odata4j.core.OEntity;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class Days extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    ODataConsumer consumer;
    BarChart mBarChart;
    LineChart mLineChart;
    BottomNavigationView mBottomNavigation;
    LinearLayout mCardOne,mCardTwo,mCardThree;
    LazyLoader mLazyLoaderOne,mLazyLoaderTwo,mLazyLoaderThree;
    TextView mPowerTxt,mPowerDescTxt,mProductivityDescTxt,mNotificationNumber,mCardCautionOne,mCardCautionTwo,mCardCautionThree,mDateCardOne,mDateCardTwo,mDateCardThree;
    ImageView backBtn,mNotificationBell,mDatePicker;
    LocalDateTime dateTime;
    SharedPreferences sharedPreferences;
    Button mBottomSheetDateBtn;
    PieChart mPieChart;
    private long TimeReMaining = 3600000;// Time limit to execute the DataDisPlays
    private long TimeRepeatTask = 60000; // Repeat the task with cycle of 60.000 milliSecond
    Handler handler;
    Runnable runnable;

    String TAG = Days.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_days);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("MessageCounting",MODE_PRIVATE);
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        dateTime = new LocalDateTime();

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        String serviceUrl = ServiceAddress.serviceURL;
        OClientBehavior bacsicAthur = new OClientBehavior() {
            @Override
            public ODataClientRequest transform(ODataClientRequest request) {
                //return request.header("Accept", "application/json");
                return request.header("MaxDataServiceVersion", ODataVersion.V2.asString);
            }
        };
        consumer = ODataConsumers.newBuilder(serviceUrl).setClientBehaviors(bacsicAthur).build();


////////////////////////////////////////////////////////////////////////////////////////////////////

        hook();
        BottomNavigation();
        AnimationDisPlay();
        Notification();
        DisPlayChartAndGraph();
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

////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    @Override
    protected void onResume() {
        handler.postDelayed(runnable,500);// Original time value
        super.onResume();
    }

    private void DisPlayChartAndGraph() {
        mDateCardOne.setVisibility(View.VISIBLE);
        mDateCardTwo.setVisibility(View.VISIBLE);
        mDateCardThree.setVisibility(View.VISIBLE);
        mDateCardOne.setText(dateTime.getDayOfMonth()+" Tháng "+ dateTime.getMonthOfYear() +" Năm "+dateTime.getYear());
        mDateCardTwo.setText(dateTime.getDayOfMonth()+" Tháng "+ dateTime.getMonthOfYear() +" Năm "+dateTime.getYear());
        mDateCardThree.setText(dateTime.getDayOfMonth()+" Tháng "+ dateTime.getMonthOfYear() +" Năm "+dateTime.getYear());
        new FactoryTotalPower().execute(dateTime);
        new FactoryTotalProductivity().execute(dateTime);
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

    private void AnimationDisPlay() {
        Animation mTextAnim,mCardAnim;
        mTextAnim = AnimationUtils.loadAnimation(Days.this,R.anim.text_anim);
        mCardAnim = AnimationUtils.loadAnimation(Days.this,R.anim.card_anim);
        mCardOne.setAnimation(mCardAnim);
        mCardTwo.setAnimation(mCardAnim);
        mCardThree.setAnimation(mCardAnim);
        mPowerTxt.setAnimation(mTextAnim);
        mPowerDescTxt.setAnimation(mTextAnim);
        mProductivityDescTxt.setAnimation(mTextAnim);
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

    public void DatePicker(View view){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Days.this);
        bottomSheetDialog.setContentView(R.layout.datepicker_bottom_sheet);
        bottomSheetDialog.show();
        DatePicker mSecondDatePicker = bottomSheetDialog.findViewById(R.id.Bottom_Date_Picker);
        mBottomSheetDateBtn =bottomSheetDialog.findViewById(R.id.Bottom_Date_Picker_Btn);

        mSecondDatePicker.init(dateTime.getYear(), dateTime.getMonthOfYear()-1, dateTime.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year,monthOfYear,dayOfMonth);
                dateTime = LocalDateTime.fromCalendarFields(calendar);
            }
        });
        mBottomSheetDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDateCardOne.setText(""+ dateTime.getDayOfMonth() +" tháng "+ dateTime.getMonthOfYear() +" năm "+ dateTime.getYear());
                mDateCardTwo.setText(""+ dateTime.getDayOfMonth() +" tháng "+ dateTime.getMonthOfYear() +" năm "+ dateTime.getYear());
                mDateCardThree.setText(""+ dateTime.getDayOfMonth() +" tháng "+ dateTime.getMonthOfYear() +" năm "+ dateTime.getYear());
                new FactoryTotalPower().execute(dateTime);
                new FactoryTotalProductivity().execute(dateTime);
                bottomSheetDialog.dismiss();
            }
        });
    }

    private void hook() {
        mCardOne = findViewById(R.id.Days_Card_one);
        mCardTwo = findViewById(R.id.Days_Card_two);
        mDatePicker = findViewById(R.id.Days_Date_Picker);
        mCardThree = findViewById(R.id.Days_Card_three);
        mPowerTxt = findViewById(R.id.Days_Power);
        mPowerDescTxt = findViewById(R.id.Days_PowerDecs);
        mProductivityDescTxt = findViewById(R.id.Days_ProductivityDesc);
        backBtn = findViewById(R.id.Days_backBtn);
        mBarChart = findViewById(R.id.Days_BarChart);
        mLineChart = findViewById(R.id.Days_LineChart);
        mPieChart = findViewById(R.id.Days_PieChart);
        mBottomNavigation = findViewById(R.id.Days_Bottom_nav);
        mNotificationBell = findViewById(R.id.Days_notificationBell);
        mNotificationNumber = findViewById(R.id.Days_NotificationNumber);
        mLazyLoaderOne = findViewById(R.id.LazyLoaderOne);
        mLazyLoaderTwo = findViewById(R.id.LazyLoaderTwo);
        mLazyLoaderThree = findViewById(R.id.LazyLoaderThree);
        mDateCardOne = findViewById(R.id.Days_Date_Card_One);
        mDateCardTwo = findViewById(R.id.Days_Date_Card_Two);
        mDateCardThree = findViewById(R.id.Days_Date_Card_Three);
        mCardCautionOne = findViewById(R.id.Days_Card_Caution_one);
        mCardCautionTwo = findViewById(R.id.Days_Card_Caution_two);
        mCardCautionThree = findViewById(R.id.Days_Card_Caution_three);
    }

    private void LineChartDisPlay(int mGraphScale,ArrayList<MachineClass> MachinePower) {
        ArrayList<Entry> yValue = new ArrayList<>();
        int x=1;
        Integer ArraySize;
        if (MachinePower.size()%5 != 0)
            ArraySize = MachinePower.size() - (MachinePower.size()%5);
        else
            ArraySize = MachinePower.size();
        for (int i = 0; i < ArraySize - mGraphScale; i= i + mGraphScale) {
            ArrayList<Double> Data = new ArrayList<>();
            for (int y=0;y< mGraphScale;y++){
                Data.add(MachinePower.get(i+y).getMachinePowerPharseA()+MachinePower.get(i+y).getMachinePowerPharseB()+MachinePower.get(i+y).getMachinePowerPharseC());
            }
            try {
                yValue.add(new Entry(x, Float.parseFloat(Collections.max(Data).toString())));
            }catch (Exception exception){}
            x++;
        }

        LineDataSet lineDataSet = new LineDataSet(yValue,"data");
        ArrayList<ILineDataSet> iLineDataSets =  new ArrayList<>();
        iLineDataSets.add(lineDataSet);
        LineData lineData = new LineData(iLineDataSets);
        mLineChart.setVisibility(View.VISIBLE);
        mLineChart.setData(lineData);
        mLineChart.animateX(2000);
        mLineChart.getXAxis().setDrawGridLines(false);
        mLineChart.getAxisLeft().setDrawGridLines(false);
        mLineChart.getAxisRight().setDrawGridLines(false);
        mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mLineChart.getAxisRight().setEnabled(false);
        mLineChart.getAxisLeft().setEnabled(false);
        mLineChart.getLegend().setEnabled(false);
        mLineChart.getXAxis().setTextColor(getResources().getColor(R.color.TextColor_four_opt_two));
        mLineChart.getDescription().setEnabled(false);

        lineDataSet.setColor(getResources().getColor(R.color.IconColor_three));
        lineDataSet.setCircleColor(Color.YELLOW);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleHoleRadius(1);
        lineDataSet.setCircleRadius(1);
        lineDataSet.setValueTextColor(getResources().getColor(R.color.TextColor_three));
        lineDataSet.setValueTextSize(10);

    } // Draw LineChart

    private void BartChartDisPlay(ArrayList<MachineClass> MachinePower) {
        float mTotalPower = 0.0f;
        ArrayList<BarEntry> power = new ArrayList<>();
        ArrayList<Double> Data = new ArrayList<Double>();
        ArrayList<Integer> Samples =  new ArrayList<Integer>();
        for (int u= 0 ;u < 24 ;u++){
            Data.add(u,0.0);
        }
        for (int w = 0; w<24;w++ ){
            Samples.add(w,1);
        }
        for (int i = 0; i < MachinePower.size() ; i++){
            try {
                Integer t = Integer.parseInt(MachinePower.get(i).getMachineDateTime().toString().substring(11, 13));
                Samples.set(t, Samples.get(t) + 1);
                Data.set(t, Data.get(t) + MachinePower.get(i).getMachinePowerPharseC() + MachinePower.get(i).getMachinePowerPharseB() + MachinePower.get(i).getMachinePowerPharseC());
            }catch (Exception e){}
        }
        for (int j = 0; j < 24 ; j++){
           Float powerFloat = Float.parseFloat(Data.get(j).toString());
           mTotalPower = mTotalPower + powerFloat/Samples.get(j);
           power.add(new BarEntry(j,powerFloat/(Samples.get(j))));
        }

        BarDataSet barDataSet = new BarDataSet(power,"Power");
        barDataSet.setValueTextColor(getResources().getColor(R.color.TextColor_three));
        barDataSet.setValueTextSize(10f);
        barDataSet.setColor(getResources().getColor(R.color.ButtonColor_four));

        BarData barData = new BarData(barDataSet);
        mBarChart.setData(barData);
        mBarChart.setVisibility(View.VISIBLE);
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
        mPowerTxt.setText( String.valueOf(mTotalPower/1000).toString()+" KWh ");
    } // Draw BarChart

    private void PieChartDisPlay(ArrayList<MachineClass> MachineProductivity) {
        ArrayList<PieEntry> products = new ArrayList<>();
        ArrayList<Integer> MachineSpecificTime = new ArrayList<>();
        int waitTime = 0,runTime = 0,setupTime = 0,offTime = 0;
        for (MachineClass machineClass : MachineProductivity){
            runTime+=machineClass.getMachineRunTime();
            waitTime+=machineClass.getMachineWaitTime();
            setupTime+=machineClass.getMachineSetUpTime();
            offTime+=machineClass.getMachineOffTime();
        }
        MachineSpecificTime.add(runTime);
        MachineSpecificTime.add(waitTime);
        MachineSpecificTime.add(setupTime);
        MachineSpecificTime.add(offTime);
        Float mTotalProductivity =(float) (MachineSpecificTime.
                get(0)+MachineSpecificTime.get(2))/(MachineSpecificTime.get(0)+MachineSpecificTime.get(1)+MachineSpecificTime.get(2)+MachineSpecificTime.get(3))*100;
        products.add(new PieEntry(MachineSpecificTime.get(1)+MachineSpecificTime.get(3),"Phút"));
        products.add(new PieEntry(MachineSpecificTime.get(0)+MachineSpecificTime.get(2),"Phút"));
        PieDataSet pieDataSet = new PieDataSet(products,"Productivity");
        pieDataSet.setColors(new int[]{ getResources().getColor(R.color.MainBackRound_two),getResources().getColor(R.color.ButtonColor_four)});
        pieDataSet.setValueTextColor(getResources().getColor(R.color.TextColor_three));
        pieDataSet.setValueTextSize(15f);
        PieData pieData = new PieData(pieDataSet);
        mPieChart.setVisibility(View.VISIBLE);
        mPieChart.setData(pieData);
        mPieChart.setCenterText((mTotalProductivity.toString().substring(0,5)+" % \n"+"Hiệu suất"));
        mPieChart.setCenterTextColor(getResources().getColor(R.color.TextColor_four_opt_two));
        mPieChart.setCenterTextSize(17f);
        mPieChart.setDrawCenterText(true);
        mPieChart.getDescription().setEnabled(false);
        mPieChart.animate();
        mPieChart.spin(2500,-300f,0f, Easing.EaseInOutQuad);
        mPieChart.setHoleColor(R.color.CardColor_three);
        mPieChart.getLegend().setEnabled(false);
    }  // Draw PieChart

    private class FactoryTotalPower extends AsyncTask<LocalDateTime,Long,Integer>{
        ArrayList<MachineClass> MachinePower;
        @Override
        protected void onPreExecute() {
            mLazyLoaderOne.setVisibility(View.VISIBLE);
            mCardCautionOne.setVisibility(View.VISIBLE);
            mLazyLoaderTwo.setVisibility(View.VISIBLE);
            mCardCautionTwo.setVisibility(View.VISIBLE);
            mBarChart.setVisibility(View.INVISIBLE);
            mLineChart.setVisibility(View.INVISIBLE);
            mCardCautionOne.setText("Đang cập nhật dữ liệu!");
            mCardCautionTwo.setText("Dang cập nhật dữ liệu!");
        }
        @Override
        protected Integer doInBackground(LocalDateTime... localDateTimes) {
           try{
               MachinePower = new ArrayList<>();
               for (OEntity DataFromDataBase : consumer.getEntities("LichSuMays")
                       .filter("ThoiGianCapNhat ge datetime'"+ localDateTimes[0].toString("yyyy-MM-dd")+"T00:00:00.000' and ThoiGianCapNhat le datetime'"+localDateTimes[0].toString("yyyy-MM-dd")+"T23:59:59.999' and GiamSatMay eq 252").execute())
               {
                   MachineClass machineClass = new MachineClass();
                   machineClass.setMachineDateTime(DataFromDataBase.getProperty("ThoiGianCapNhat", LocalDateTime.class).getValue());
                   machineClass.setMachinePowerPharseA(DataFromDataBase.getProperty("CongSuatPhaA",Double.class).getValue());
                   machineClass.setMachinePowerPharseB(DataFromDataBase.getProperty("CongSuatPhaB",Double.class).getValue());
                   machineClass.setMachinePowerPharseC(DataFromDataBase.getProperty("CongSuatPhaC",Double.class).getValue());
                   MachinePower.add(machineClass);
               }
               if (MachinePower.isEmpty())
                   return 0;
               else return 1;
           }
           catch (Exception ex){
               return -1;
           }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mLazyLoaderOne.setVisibility(View.INVISIBLE);
            mLazyLoaderTwo.setVisibility(View.INVISIBLE);
            mDatePicker.setClickable(true);
            switch (integer){
                case -1:{
                    mCardCautionOne.setText("Hệ thống lỗi vui lòng thử lại!");
                    mCardCautionTwo.setText("Hệ thống lỗi vui lòng thử lại!");
                    break;
                }
                case 0:{
                    mCardCautionOne.setText("Xin lỗi dữ liệu chưa được cập nhật!");
                    mCardCautionTwo.setText("Xin lỗi dữ liệu chưa được cập nhật!");
                    break;
                }
                case 1:{
                    BartChartDisPlay(MachinePower);
                    LineChartDisPlay(2,MachinePower);
                    mCardCautionOne.setText("Dữ liệu cập nhật thành công!");
                    mCardCautionTwo.setText("Dữ liệu cập nhật thành công!");
                    break;
                }
            }
        }
    }

    private class FactoryTotalProductivity extends AsyncTask<LocalDateTime,Long,Integer>{

        ArrayList<MachineClass> MachineProductivity;

        @Override
        protected void onPreExecute() {
        mCardCautionThree.setVisibility(View.VISIBLE);
        mLazyLoaderThree.setVisibility(View.VISIBLE);
        mPieChart.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Integer doInBackground(LocalDateTime... localDateTimes) {
        try{
            MachineProductivity= new ArrayList<>();
            for (OEntity DataFromDataBaseTwo : consumer.getEntities("ThoiGianMays")
                    .filter("NgayCapNhat ge datetime'"+ localDateTimes[0].toString("yyyy-MM-dd")+"T00:00:00.000' and NgayCapNhat le datetime'"+localDateTimes[0].toString("yyyy-MM-dd")+"T23:59:59.999'").execute()){
                MachineClass machineClass = new MachineClass();
                machineClass.setMachineRunTime(DataFromDataBaseTwo.getProperty("RunningTime",Integer.class).getValue());
                machineClass.setMachineWaitTime(DataFromDataBaseTwo.getProperty("WaitTime",Integer.class).getValue());
                machineClass.setMachineSetUpTime(DataFromDataBaseTwo.getProperty("SetUpTime",Integer.class).getValue());
                machineClass.setMachineOffTime(DataFromDataBaseTwo.getProperty("OffTime",Integer.class).getValue());
                MachineProductivity.add(machineClass);
            }
            if (MachineProductivity.isEmpty())
                return 0;
            else
                return 1;
        }
        catch (Exception ex){
            return -1;
        }
    }

        @Override
        protected void onPostExecute(Integer integer) {
        mLazyLoaderThree.setVisibility(View.INVISIBLE);
        switch (integer){
            case -1:{
                mCardCautionThree.setText("Hệ thống lỗi vui lòng thủ lại!");
                break;
            }
            case 0:{
                mCardCautionThree.setText("Xin lỗi dữ liệu chưa được cập nhật!");
                break;
            }
            case 1:{
                mCardCautionThree.setText("Dữ liệu cập nhật thành công!");
                PieChartDisPlay(MachineProductivity);
                break;
            }
        }

    }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Pair[] pairs = new Pair[2];
        pairs[0] = new Pair<View, String>(mNotificationBell, "Notification_transition");
        pairs[1] = new Pair<View, String>(mNotificationNumber, "NotificationNumber_transition");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Days.this, pairs);
        switch (menuItem.getItemId()){
            case R.id.nav_home:
            {
                Intent intent = new Intent(getApplicationContext(),DashBoard.class);
                startActivity(intent, options.toBundle());
            break;
            }
            case R.id.nav_days:
            {
                Intent intent = new Intent(getApplicationContext(),Days.class);
                startActivity(intent,options.toBundle());
                break;
            }
            case R.id.nav_weeks:
                Intent intent = new Intent(getApplicationContext(),Weeks.class);
                startActivity(intent,options.toBundle());
                break;
        }

        return true;
    }
}
