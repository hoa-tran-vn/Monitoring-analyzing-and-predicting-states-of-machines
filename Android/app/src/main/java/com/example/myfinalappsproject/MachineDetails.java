package com.example.myfinalappsproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.github.mikephil.charting.animation.Easing;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.odata4j.consumer.ODataClientRequest;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.ODataConsumers;
import org.odata4j.consumer.behaviors.OClientBehavior;
import org.odata4j.core.ODataVersion;
import org.odata4j.core.OEntity;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MachineDetails extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    // Variables
    ImageView backBtn;
    BarChart mBarChart,mProcessChart;
    PieChart mPieChart;
    BottomNavigationView mbottomNavigation;
    Button mButton,mBottomSheetBtn;
    TextView mDateCardOne, mDateCardTwo, mDateCardThree,mDateCardFour, mMachineID, mMachineStatus, mMachineProductivity, mWorkerName, mProductName, mMachineStartTime,mCardOneCaution,mProcessBarCaution,mNotificationNumber;
    ODataConsumer consumer;
    LocalDateTime dateTime;
    Integer MachineIDfromDashBoard,IDNhanVien,IDCongViec;
    SharedPreferences sharedPreferences;
    LazyLoader mLazyLoaderOne,mLazyLoaderTwo,mLazyLoaderThree,mLazyLoaderFour;

    private long TimeReMaining = 3600000;// Time limit to execute the DataDisPlays
    private long TimeRepeatTask = 60000; // Repeat the task with cycle of 60.000 milliSecond
    Runnable runnable;
    Handler handler;
    String TAG = MachineDetails.class.getSimpleName();
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_details);
        sharedPreferences = getSharedPreferences("MessageCounting",MODE_PRIVATE);
        /////////////////////////////////////////////////////////////////////////////////////////////////////

        dateTime = new LocalDateTime();

        // Get current day and month
        /////////////////////////////////////////////////////////////////////////////////////////////////////

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

        MachineIDfromDashBoard = Integer.parseInt(getIntent().getStringExtra("MachineIdentity").substring(35));

        hook();
        BottomNavifation();
        DateDisPlay();
        InformationDisPlay();
        mDateCardTwo.setClickable(false);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){

            case R.id.nav_home:
            {
                Intent intent = new Intent(getApplicationContext(),DashBoard.class);
                startActivity(intent);
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
    } // Select icon in Navigation Bottom

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void InformationDisPlay() {
        mPieChart.setVisibility(View.INVISIBLE);
        mBarChart.setVisibility(View.INVISIBLE);
        new InformationTask().execute(dateTime);
        new MachinePowerTask().execute(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth());
    }// Make machine ID and execute InformationTask

    private void DateDisPlay() {
        mDateCardOne.setText("Ngày "+(dateTime.getDayOfMonth())+" Tháng "+(dateTime.getMonthOfYear())+" Năm "+dateTime.getYear()+"");
        mDateCardTwo.setText("Ngày "+(dateTime.getDayOfMonth())+" Tháng "+(dateTime.getMonthOfYear())+" Năm "+dateTime.getYear()+"");
        mDateCardThree.setText("Ngày "+(dateTime.getDayOfMonth())+" Tháng "+(dateTime.getMonthOfYear())+" Năm "+dateTime.getYear()+"");
        mDateCardFour.setText("Ngày "+(dateTime.getDayOfMonth())+" Tháng "+(dateTime.getMonthOfYear())+" Năm "+dateTime.getYear()+"");
    } // Display Instance Day and Month

    private void BottomNavifation() {
        mbottomNavigation.setOnNavigationItemSelectedListener(this);
    }

    private void hook() {
        backBtn  = findViewById(R.id.MachineDetail_backBtn);
        mPieChart = findViewById(R.id.MachineDetail_PipeChart);
        mbottomNavigation = findViewById(R.id.MachineDetail_bottom_nav);
        mButton = findViewById(R.id.MachineDetail_moreInforBtn);
        mDateCardOne = findViewById(R.id.MachineDetail_Date_Card_One);
        mDateCardTwo = findViewById(R.id.MachineDetail_Date_Card_Two);
        mDateCardThree = findViewById(R.id.MachineDetail_Date_Card_Three);
        mDateCardFour = findViewById(R.id.MachineDetail_Date_Card_Four);
        mCardOneCaution = findViewById(R.id.MachineDetail_Graph_Caution);
        mMachineID = findViewById(R.id.MachineDetail_MachineID);
        mMachineStatus = findViewById(R.id.MachineDetail_MachineStatus);
        mMachineProductivity = findViewById(R.id.MachineDetail_ProductivityTxt);
        mWorkerName = findViewById(R.id.MachineDetail_WorkerName);
        mProductName = findViewById(R.id.MachineDetail_ProductName);
        mMachineStartTime = findViewById(R.id.MachineDetail_TimeStart);
        mLazyLoaderOne = findViewById(R.id.LazyLoaderOne);
        mLazyLoaderTwo = findViewById(R.id.LazyLoaderTwo);
        mLazyLoaderThree = findViewById(R.id.LazyLoaderThree);
        mLazyLoaderFour = findViewById(R.id.LazyLoaderFour);
        mBarChart = findViewById(R.id.MachineDetail_barChart);
//        mProcessChart = findViewById(R.id.MachineDetail_ProcessChart);
        mProcessBarCaution = findViewById(R.id.MachineDetail_ProcessChart_Caution);
        mNotificationNumber = findViewById(R.id.MachineDetail_notificationNumber);

    } // Hook view with their ID

    public void Back(View view) {
        onBackPressed();
    } // BackPress function

    public void MoreInforMation(View view){
        Intent intent = new Intent(getApplicationContext(),DetailInformation.class);
        intent.putExtra("ID May",MachineIDfromDashBoard);
        intent.putExtra("IDNhanVien",IDNhanVien);
        intent.putExtra("IDCongViec",IDCongViec);
        startActivity(intent);
    } // Jump to DitailInformation Activity

    private void BartChartDisPlay(ArrayList<MachineClass> MachinePower) {
        Float mTotalPower = 0.0f;
        ArrayList<BarEntry> power = new ArrayList<>();
        ArrayList<Double> Data = new ArrayList<Double>();
        ArrayList<Integer> Samples =  new ArrayList<Integer>();
        for (int u= 0 ;u < 24 ;u++){
            Data.add(u,0.0);
        }
        for (int w = 0; w<24;w++ ){
            Samples.add(w,1);
        }
        for (MachineClass machineClass: MachinePower) {
            try {
                Integer t = Integer.parseInt(machineClass.getMachineDateTime().toString().substring(11, 13));
                Samples.set(t, Samples.get(t) + 1);
                Data.set(t, Data.get(t) + machineClass.getMachinePowerPharseC() + machineClass.getMachinePowerPharseB() + machineClass.getMachinePowerPharseC());
            }catch (Exception e){}
        }
        for (int j = 0; j < 24 ; j++){
            try {
                Float powerFloat = Float.parseFloat(Data.get(j).toString());
                mTotalPower = mTotalPower + powerFloat / Samples.get(j);
                power.add(new BarEntry(j, powerFloat / (Samples.get(j))));
            }catch (Exception e){}
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
    } // Draw BarChart

    private void ProcessBarDisPlay(MyGanttChartObject myGanttChartObject){
        List<MyGanttChartObject> myGanttChartObjectList = new ArrayList<>();
        myGanttChartObjectList.add(myGanttChartObject);
        List<Integer> listInt = new ArrayList<>();
        LocalDateTime dateTimeStart = new LocalDateTime();
        for (MyGanttChartObject myGanttChartObject1: myGanttChartObjectList){
            try {
                if (Minutes.minutesBetween(dateTimeStart, myGanttChartObject1.getDateTimeStart()).getMinutes() < 0) {
                    dateTimeStart = myGanttChartObject1.getDateTimeStart();
                }
            }catch (Exception e){}
        }
        for (MyGanttChartObject myGanttChartObject1: myGanttChartObjectList){
            myGanttChartObject1.setDateTimeStart(dateTimeStart);
            listInt.add(myGanttChartObject1.getListTrangThai().size());
        }
        List<String> stringsTitle = new ArrayList<>();
        for (int i = 0; (i*60)<Collections.max(listInt); i++){
            stringsTitle.add(( (int) dateTimeStart.getHourOfDay()+i) +":00");
        }
        myGanttChartObjectList.add(0,new MyGanttChartObject((ArrayList<String>) stringsTitle,true));
        ((ListView) findViewById(R.id.listViewGanttChart)).setAdapter(new MyGanttChartAdapter(myGanttChartObjectList,this, Collections.max(listInt)));
    }// Draw GanttChart

    private void GanttChartByHoang(List<MachineClass> MachinePower){
        ArrayList<BarEntry> OffPatch = new ArrayList<BarEntry>();
        ArrayList<BarEntry> WaitingPatch = new ArrayList<BarEntry>();
        ArrayList<BarEntry> ReadyPatch = new ArrayList<BarEntry>();
        ArrayList<BarEntry> SetUpPatch = new ArrayList<BarEntry>();
        ArrayList<BarEntry> RunningPatch = new ArrayList<BarEntry>();
        ArrayList<BarEntry> BreakDownPatch = new ArrayList<BarEntry>();
        for (MachineClass machineClass: MachinePower){
            Integer mProHour = machineClass.getMachineDateTime().getHourOfDay();
//            String mProMinute = MachinePower.get(i).getMachineDateTime().toString().substring(14,16);
            Integer x = machineClass.getMachineDateTime().getMinuteOfHour();
            Float ww = (float) Math.round((float) x/60*1000)/1000;
            Float mProTime = mProHour + ww;
            switch (machineClass.getMachineStatus()){

                case 1:{
                    //Off
                    OffPatch.add(new BarEntry(mProTime,1));
                    break;
                }
                case 2:{
                    //Waiting
                    WaitingPatch.add(new BarEntry(mProTime,1));
                    break;
                }
                case 3:{
                    //Ready
                    ReadyPatch.add(new BarEntry(mProTime,1));
                    break;
                }
                case 4:{
                    //SetUp
                    SetUpPatch.add(new BarEntry(mProTime,1));
                    break;
                }
                case 5:{
                    //Running
                    RunningPatch.add(new BarEntry(mProTime,1));
                    break;
                }
                case 6:{
                    //BreakDown
                    BreakDownPatch.add(new BarEntry(mProTime,1));
                    break;
                }
            }
        }
        BarDataSet OffDataSet = new BarDataSet(OffPatch,"Off");
        BarDataSet WaitingDataSet = new BarDataSet(WaitingPatch,"Waiting");
        BarDataSet ReadyDataSet = new BarDataSet(ReadyPatch,"Ready");
        BarDataSet SetUpDataSet = new BarDataSet(SetUpPatch,"SetUp");
        BarDataSet RunningDataSet = new BarDataSet(RunningPatch,"Running");
        BarDataSet BreakDownDataSet = new BarDataSet(BreakDownPatch,"BreakDown");
        OffDataSet.setColor(getResources().getColor(R.color.Off_Color));
        WaitingDataSet.setColor(getResources().getColor(R.color.Waiting_Color));
        ReadyDataSet.setColor(getResources().getColor(R.color.Ready_Color));
        SetUpDataSet.setColor(getResources().getColor(R.color.SetUp_Color));
        RunningDataSet.setColor(getResources().getColor(R.color.Running_Color));
        BreakDownDataSet.setColor(getResources().getColor(R.color.BreakDown_Color));
        BarData barData = new BarData(OffDataSet,WaitingDataSet,ReadyDataSet,SetUpDataSet,RunningDataSet,BreakDownDataSet);
        mProcessChart.setData(barData);
        mProcessChart.setVisibility(View.VISIBLE);
        mProcessChart.setFitBars(true);
        mProcessChart.getDescription().setEnabled(false);
        mProcessChart.animateX(4000);
        mProcessChart.getXAxis().setDrawGridLines(false);
        mProcessChart.setFitBars(true);
        mProcessChart.getAxisLeft().setDrawGridLines(false);
        mProcessChart.getAxisRight().setDrawGridLines(false);
        mProcessChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mProcessChart.getAxisRight().setEnabled(false);
        mProcessChart.getAxisLeft().setEnabled(false);
        mProcessChart.getLegend().setEnabled(false);
        mProcessChart.getXAxis().setTextColor(getResources().getColor(R.color.TextColor_four_opt_two));
    }

    private void GanttChartTestByDuy(){

    }

    private void PieChartDisPlay(MachineClass machineClass) {
        ArrayList<PieEntry> products = new ArrayList<>();
        if (0!= machineClass.getMachineRunTime()) {
            products.add(new PieEntry(machineClass.getMachineRunTime(), "Run"));
        }
        if (0!= machineClass.getMachineOffTime()) {
            products.add(new PieEntry(machineClass.getMachineOffTime(), "Stop"));
        }
        if (0!= machineClass.getMachineWaitTime()) {
            products.add(new PieEntry(machineClass.getMachineWaitTime(), "Wait"));
        }
        if (0!= machineClass.getMachineSetUpTime()) {
            products.add(new PieEntry(machineClass.getMachineSetUpTime(), "Setup"));
        }
        PieDataSet pieDataSet = new PieDataSet(products,"Productivity");
        pieDataSet.setColors(new int[]{getResources().getColor(R.color.ButtonColor_four),getResources().getColor(R.color.PieChart_color_one),getResources().getColor(R.color.PieChart_color_three),getResources().getColor(R.color.PieChart_color_two)});
        pieDataSet.setValueTextColor(getResources().getColor(R.color.TextColor_three));
        pieDataSet.setValueTextSize(16f);
        PieData pieData = new PieData(pieDataSet);
        mPieChart.setData(pieData);
        mPieChart.setCenterTextColor(R.color.IconColor_three);
        mPieChart.getDescription().setEnabled(false);
        mPieChart.animate();
        mPieChart.spin(2500,-300f,0f, Easing.EaseInOutQuad);
        mPieChart.setHoleColor(R.color.CardColor_three);
        mPieChart.getLegend().setEnabled(false);
    }  // Draw PieChart

    private class InformationTask extends AsyncTask<LocalDateTime,Long,Integer>{
        MachineClass machineClass;
        @Override
        protected Integer doInBackground(LocalDateTime...localDateTimes) {
             try{
                for (OEntity DataFromDataBase : consumer.getEntities("ThoiGianMays")
                        .filter("NgayCapNhat ge datetime'"+ localDateTimes[0].toString("yyyy-MM-dd")+"T00:00:00.000' and NgayCapNhat le datetime'"+localDateTimes[0].toString("yyyy-MM-dd")+"T23:59:59.999' and may eq "+ MachineIDfromDashBoard).orderBy("may").orderBy("NgayCapNhat desc").top(1).execute()){
                    machineClass = new MachineClass();
                    machineClass.setMachineStatus(DataFromDataBase.getProperty("Trangthaimay", Integer.class).getValue());
                    machineClass.setMachineID(DataFromDataBase.getProperty("may", Integer.class).getValue());
                    machineClass.setMachineEnergy(DataFromDataBase.getProperty("NangLuong", Double.class).getValue());
                    machineClass.setMachineWaitTime(DataFromDataBase.getProperty("WaitTime", Integer.class).getValue());
                    machineClass.setMachineRunTime(DataFromDataBase.getProperty("RunningTime", Integer.class).getValue());
                    machineClass.setMachineOffTime(DataFromDataBase.getProperty("OffTime", Integer.class).getValue());
                    machineClass.setMachineSetUpTime(DataFromDataBase.getProperty("SetUpTime", Integer.class).getValue());
                }
                if (machineClass==null)
                    return 0;
                else return 1;
            }catch (Exception ex){
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mLazyLoaderOne.setVisibility(View.INVISIBLE);
            switch (integer) {
                    case 1: {
                        new MachineInformationTask().execute(machineClass.getMachineID());
                        mLazyLoaderThree.setVisibility(View.INVISIBLE);
                        ResultDisplay(machineClass);
                        break;
                    }
                    case 0: {
                        Toast.makeText(getApplicationContext(), "Xin lỗi dữ liệu chưa cập nhật!", Toast.LENGTH_LONG).show();
                        mLazyLoaderThree.setVisibility(View.INVISIBLE);
                        break;
                    }
                    case -1: {
                        mLazyLoaderThree.setVisibility(View.INVISIBLE);
                        break;
                    }
                }
        }
    }  // Load data form entity "ThoiGianMay"

    private void ResultDisplay(MachineClass machineClass) {
                switch (machineClass.getMachineStatus()){
                    case 1:{
                        mMachineStatus.setText("Off");
                        break;
                    }
                    case 2:{
                        mMachineStatus.setText("Waiting");
                        break;
                    }
                    case 3: {
                        mMachineStatus.setText("Ready");
                        break;
                    }

                    case 4:{
                        mMachineStatus.setText("SetUp");
                        break;
                    }
                    case 5:{
                        mMachineStatus.setText("Run");
                        break;
                    }
                    case 6:{
                        mMachineStatus.setText("BreakDown");
                        break;
                    }

                }
                Integer Productivity = ( machineClass.getMachineRunTime()+machineClass.getMachineSetUpTime())*100/( machineClass.getMachineRunTime()+machineClass.getMachineSetUpTime()+machineClass.getMachineWaitTime()+machineClass.getMachineOffTime());
                mMachineProductivity.setText(Productivity+"%");
                mPieChart.setVisibility(View.VISIBLE);
                PieChartDisPlay(machineClass);
    } // Display machine Status,calculate, display machine productivity and draw pipeChart

    private class MachineInformationTask extends AsyncTask<Integer,Long,Integer>{
        MachineClass machineClass;
        @Override
        protected Integer doInBackground(Integer... integers) {
            try{
                machineClass = new MachineClass();
                for (OEntity DataFromDataBaseTwo : consumer
                        .getEntities("Mays").filter(" Id eq "+ integers[0]).execute()){
                    machineClass.setMachineName(DataFromDataBaseTwo.getProperty("MaSo",String.class).getValue());
                    machineClass.setMachineOrigin(DataFromDataBaseTwo.getProperty("NhanHieu",String.class).getValue());
                    machineClass.setMachineDuty(DataFromDataBaseTwo.getProperty("Ten",String.class).getValue());
                }
                if (machineClass == null)
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
            mLazyLoaderOne.setVisibility(View.INVISIBLE);
            switch (integer){
                case 1:{
                    mMachineID.setText(machineClass.getMachineName());
                    new WorkerInformationTask().execute();
                    break;
                }
                case 0:break;
                case -1:{
                    break;
                }

            }
        }
    } // Load data from entity "Mays"

    private class WorkerInformationTask extends AsyncTask<LocalDateTime,Long,Integer>{
        UserObject workerClass;
        ProductObject productObject;
        @Override
        protected Integer doInBackground(LocalDateTime... localDateTimes) {

            try{
                for (OEntity DataFromDataBaseThree : consumer.getEntities("CongViecs")
//                        .filter("CongViec_May1 eq " + MachineIDfromDashBoard +" and year(GioBD) eq "+ integers[0] +" and month(GioBD) eq "+ integers[1] +"and day(GioBD) eq " + integers[2] +"and hour(GioBD) le "+ integers[3] +"and GioKT eq null")
                        .filter("CongViec_May1 eq " + MachineIDfromDashBoard +" and GioBD ge dateTime'"+ (new LocalDateTime()).minusHours(12).toString("yyyy-MM-dd") +"T00:00:00.000' and GioKT eq null and CongViec_CongViecPhu eq null")
                        .orderBy("GioBD desc")
                        .top(1)
                        .execute()){
                    IDCongViec = DataFromDataBaseThree.getProperty("Id",Integer.class).getValue();
                    IDNhanVien = DataFromDataBaseThree.getProperty("CongViec_NhanVien",Integer.class).getValue();
                    productObject = new ProductObject();
                    workerClass = new UserObject();
                    workerClass.setID(DataFromDataBaseThree.getProperty("CongViec_NhanVien",Integer.class).getValue());
                    productObject.setProductName(DataFromDataBaseThree.getProperty("MaChiTiet",String.class).getValue());
                    productObject.setProductID(null!=DataFromDataBaseThree.getProperty("CongViec_ChiTiet2",Integer.class).getValue()?DataFromDataBaseThree.getProperty("CongViec_ChiTiet2",Integer.class).getValue():0);
                    productObject.setProductStartTime(DataFromDataBaseThree.getProperty("GioBD",LocalDateTime.class).getValue());
                }
                if (workerClass == null)
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
            mLazyLoaderOne.setVisibility(View.INVISIBLE);
            switch (integer){
                case 1:
                    mProductName.setText(productObject.getProductName());
                    mMachineStartTime.setText(productObject.getProductStartTime().toString().substring(11,16));
                    new WorkerTask().execute(workerClass.getID());
                    break;
                case 0:
                    Toast.makeText(getApplicationContext(),"Cant found worker",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    } // Load data form entity "CongViecs"

    private class WorkerTask extends AsyncTask<Integer,Long,Integer>{
        UserObject workerClasstwo;
        @Override
        protected Integer doInBackground(Integer... integers) {
            try{
                for (OEntity DataFromDataBaseFour : consumer.getEntities("NhanViens").filter( "Id eq " +  integers[0] + " and NghiViec ne true").execute()){
                    workerClasstwo = new UserObject();
                    workerClasstwo.setUserName(DataFromDataBaseFour.getProperty("HoTen",String.class).getValue());
                    workerClasstwo.setIdentityName(DataFromDataBaseFour.getProperty("MaSo",String.class).getValue());
                    workerClasstwo.setEmail(DataFromDataBaseFour.getProperty("Email",String.class).getValue());
                    workerClasstwo.setGroupId(DataFromDataBaseFour.getProperty("NhanVien_ToSX",Integer.class).getValue());
                }
                if (workerClasstwo == null)
                    return 0;
                else return 1;
            }catch (Exception ex){
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mLazyLoaderOne.setVisibility(View.INVISIBLE);
            switch (integer){
                case 1:
                    Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_SHORT).show();
                    mButton.setVisibility(View.VISIBLE);
                    mWorkerName.setText(workerClasstwo.getUserName());
                    break;
            }
        }
    } // Load data from entity "NhanViens"

    private class MachinePowerTask extends AsyncTask<Integer,Long,Integer>{
        ArrayList<MachineClass> MachinePower;
        String TenMay;
        @Override
        protected void onPreExecute() {
            mBarChart.setVisibility(View.INVISIBLE);
//            mProcessChart.setVisibility(View.INVISIBLE);
            mCardOneCaution.setText("Đang xử lý dữ liệu...");
            mProcessBarCaution.setText("Đang xử lý dữ liệu..,");
        }
        @Override
        protected Integer doInBackground(Integer... integers) {
        try{
            MachinePower = new ArrayList<>();

            for(OEntity DataFromDataBase : consumer.getEntities("LichSuMays")
                    .filter(" year(ThoiGianCapNhat) eq "+ integers[0] +" and month(ThoiGianCapNhat) eq "+ integers[1]
                            +" and day(ThoiGianCapNhat) eq "+ integers[2] +" and GiamSatMay eq "+ MachineIDfromDashBoard +"").orderBy("ThoiGianCapNhat asc").execute()) {
                MachineClass machinePowerClass = new MachineClass();
                machinePowerClass.setMachinePowerPharseA(DataFromDataBase.getProperty("CongSuatPhaA",Double.class).getValue());
                machinePowerClass.setMachinePowerPharseB(DataFromDataBase.getProperty("CongSuatPhaB",Double.class).getValue());
                machinePowerClass.setMachinePowerPharseC(DataFromDataBase.getProperty("CongSuatPhaC",Double.class).getValue());
                machinePowerClass.setMachineStatus(DataFromDataBase.getProperty("trangThai",Integer.class).getValue());
                machinePowerClass.setMachineDateTime(DataFromDataBase.getProperty("ThoiGianCapNhat",LocalDateTime.class).getValue());
                MachinePower.add(machinePowerClass);
            }
            OEntity DataFromDataBase = consumer.getEntity("Mays",MachineIDfromDashBoard).execute();
            TenMay = DataFromDataBase.getProperty("MaSo",String.class).getValue();
            if (MachinePower.isEmpty())
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
            try {
                switch (integer) {
                    case 0: {
                        mLazyLoaderTwo.setVisibility(View.INVISIBLE);
                        mLazyLoaderFour.setVisibility(View.INVISIBLE);
                        mDateCardTwo.setClickable(true);
                        mProcessBarCaution.setText("Xin lỗi dữ liệu chưa được cập nhật!");
                        mCardOneCaution.setText("Xin lỗi dữ liệu chưa được cập nhật!");
                        break;
                    }
                    case 1: {
                        BartChartDisPlay(MachinePower);
                        ProcessBarDisPlay(new MyGanttChartObject(MachineIDfromDashBoard,TenMay,MachinePower));
                        mLazyLoaderTwo.setVisibility(View.INVISIBLE);
                        mLazyLoaderFour.setVisibility(View.INVISIBLE);
                        mCardOneCaution.setText("Dữ liệu cập nhật thành công!");
                        mProcessBarCaution.setText("Dữ liệu cập nhật thành công!");
                        mDateCardTwo.setClickable(true);
                        break;
                    }
                    case -1: {
                        mLazyLoaderTwo.setVisibility(View.INVISIBLE);
                        mLazyLoaderFour.setVisibility(View.INVISIBLE);
                        mDateCardTwo.setClickable(true);
                        mProcessBarCaution.setText("Lỗi hệ thống! Vui lòng thử lại.");
                        mCardOneCaution.setText("Lỗi hệ thống! Vui lòng thử lại.");
                        break;
                    }
                }
            }catch (Exception exception){
            }
        }
    } // Load Power from entity "LichSuMays"

    public void DateOptional(View view) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MachineDetails.this);
        bottomSheetDialog.setContentView(R.layout.layout_bottom_sheet);
        bottomSheetDialog.show();
        DatePicker mDatePicker = bottomSheetDialog.findViewById(R.id.Date_picker);
        mBottomSheetBtn = bottomSheetDialog.findViewById(R.id.Bottom_sheet_btn);
        mDatePicker.init(dateTime.getYear() , dateTime.getMonthOfYear()-1 , dateTime.getDayOfMonth() , new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year,monthOfYear,dayOfMonth);
                dateTime = LocalDateTime.fromCalendarFields(calendar);
            }
        });
        if (mBottomSheetBtn.isClickable()){
            mBottomSheetBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.dismiss();
                    mDateCardTwo.setText("Ngày "+ dateTime.getDayOfMonth() +" Tháng "+ dateTime.getMonthOfYear() +" Năm "+ dateTime.getYear());
                    mDateCardThree.setText("Ngày "+(dateTime.getDayOfMonth())+" Tháng "+(dateTime.getMonthOfYear())+" Năm "+dateTime.getYear()+"");
                    mDateCardFour.setText("Ngày "+(dateTime.getDayOfMonth())+" Tháng "+(dateTime.getMonthOfYear())+" Năm "+dateTime.getYear()+"");
                    new MachinePowerTask().execute(dateTime.getYear(),dateTime.getMonthOfYear(),dateTime.getDayOfMonth());
                    new InformationTask().execute(dateTime);
                }
            });
        }
    }

}
