package com.example.myfinalappsproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import org.joda.time.LocalDateTime;
import org.odata4j.consumer.ODataClientRequest;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.ODataConsumers;
import org.odata4j.consumer.behaviors.OClientBehavior;
import org.odata4j.core.ODataVersion;
import org.odata4j.core.OEntity;
import java.util.ArrayList;

public class DashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    DrawerLayout mdrawerLayout;
    NavigationView mnavigationview;
    ImageView menuBtn, mExitBtn, mCloseButton, mNotificationBell;
    TextView mNotificationNumber;
    Button  mExitDialogBtn;
    LinearLayout mMap, mRelatedLayout;
    RelativeLayout contentView;
    ODataConsumer consumer;
    Handler handler;
    Runnable runnable;
    SharedPreferences mSharedPreferences;

    private long TimeReMaining = 3600000;// Time limit to execute the DataDisPlays
    private long TimeRepeatTask = 60000; // Repeat the task with cycle of 60.000 milliSecond

    String TAG = DashBoard.class.getSimpleName();

    final float END_SCALE = 0.7f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        mSharedPreferences = getSharedPreferences("MessageCounting",MODE_PRIVATE);

////////////////////////////////////////////////////////////////////////////////////////////////////

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
        Hooks(); // Hàm ánh xạ các view
        AnimationDisplay(); // Hàm trình bày các animation
        NavigationDrawer(); // Hàm cho Navigation drawer
        AnimateNavigationDrawer();
        BottomNavigation(); // Hàm cho Bottom Navigation
        Exit();             // Hàm cho Exit dialog
        Notification();
////////////////////////////////////////////////////////////////////////////////////////////////////

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                TimeReMaining = TimeReMaining - TimeRepeatTask; // Reduce time by TimeRepeatTask
                DataDisplay();
                LocalDateTime localDateTime = new LocalDateTime();
                new NotificationTask().execute(localDateTime.getYear(),localDateTime.getMonthOfYear(),localDateTime.getDayOfMonth());
                handler.postDelayed(this,TimeRepeatTask);  // Do the task after TimeRepeatTask
            }
        };
    }

    private void Notification() {
        mNotificationBell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoard.this,Notification_Activity.class);
                startActivity(intent);
            }
        });

    }

///////////////////////////////////////////////////////////////////////////////////////////////////

    private void DataDisplay() {
        new MachineDataTask().execute();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                TransferToGanttChart(getCurrentFocus());
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void AnimateNavigationDrawer() {

        mdrawerLayout.setScrimColor(getResources().getColor(R.color.ButtonColor_four));

        mdrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);
                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });
    }

    public void TransferToGanttChart(View view){
        Intent intent = new Intent(DashBoard.this,GanttChartActivity.class);
        startActivity(intent);
    }

    private void Exit() {
        mExitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(DashBoard.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.exit_dialog);
                dialog.show();
                mExitDialogBtn = dialog.findViewById(R.id.exit_dialog_Exit);
                mCloseButton = dialog.findViewById(R.id.exit_dialog_close);
                mCloseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                mExitDialogBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handler.removeCallbacks(runnable);
                        finish();
                        finishAffinity();
                        System.exit(0);
                    }
                });
            }
        });
    }

    private void BottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    public void Detail(View view) {
        String viewID = getResources().getResourceName(view.getId());
        Intent intent = new Intent(getApplicationContext(), MachineDetails.class);
        intent.putExtra("MachineIdentity",viewID);
        Pair[] pairs = new Pair[2];
        pairs[0] = new Pair<View, String>(mNotificationBell, "Notification_transition");
        pairs[1] = new Pair<View, String>(mNotificationNumber, "NotificationNumber_transition");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(DashBoard.this, pairs);
        startActivity(intent, options.toBundle());
    }

    private void Hooks() {
        contentView = findViewById(R.id.content);
        mExitBtn = findViewById(R.id.Dashboard_exitBtn);
        mRelatedLayout = findViewById(R.id.Relate_layout_one);
        mMap = findViewById(R.id.Dashboard_maps);
        bottomNavigationView = findViewById(R.id.Dashboard_Bottom_nav);
        mdrawerLayout = findViewById(R.id.Dashboard_drawerLayout);
        mnavigationview = findViewById(R.id.Dashboard_drawer_nav);
        menuBtn = findViewById(R.id.Dashboard_menuBtn);
        mNotificationBell = findViewById(R.id.Dashboard_Notification_Bell);
        mNotificationNumber = findViewById(R.id.Dashboard_NotificationNumber);
    }

    private void NavigationDrawer() {
        mnavigationview.bringToFront();
        mnavigationview.setNavigationItemSelectedListener(this);
        mnavigationview.setCheckedItem(R.id.nav_home);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mdrawerLayout.isDrawerVisible(GravityCompat.START))
                    mdrawerLayout.closeDrawer(GravityCompat.START);
                else
                    mdrawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private class MachineDataTask extends AsyncTask<String, Long, Integer> {
        ArrayList<MachineClass> MachineData;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String...params) {

            try{
                MachineData = new ArrayList<MachineClass>();
                for (OEntity DataFromDataBase : consumer.getEntities("TinhTrangMays").orderBy("may").execute())
                {
                    MachineClass machineClass = new MachineClass();
                    machineClass.setMachineID(DataFromDataBase.getProperty("may", Integer.class).getValue());
                    machineClass.setMachineStatus(DataFromDataBase.getProperty("trangThai", Integer.class).getValue()==null?0:DataFromDataBase.getProperty("trangThai", Integer.class).getValue());
                    MachineData.add(machineClass);
                }
                if (MachineData.isEmpty())
                    return 0;
                else
                    return 1;
            }catch (Exception ex){
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            switch (integer){
                case 1:{
                    try{
                        Display( MachineData);
                    }
                    catch (Exception ex){
                    }
                    break;
                }
                case 0:{
                    for (int i=0;i<300;i++){
                        String string_one = "M" + i;
                        String string_two = "T"+ i;
                        String string_three = "N" + i;
                        DisplayImg(string_one,R.drawable.warning_icon);
                        DisplayText(string_two,"Disable",null);
                        DisplayText(string_three,null,"#808080");
                    }
                    Toast.makeText(getApplicationContext(),"Dữ liệu chưa được cập nhật!",Toast.LENGTH_LONG).show();
                    break;
                }
                case -1:{
                    for (int i=0;i<300;i++){
                        String string_one = "M" + i;
                        String string_two = "T"+ i;
                        String string_three = "N" + i;
                        DisplayImg(string_one,R.drawable.warning_icon);
                        DisplayText(string_two,"Disable",null);
                        DisplayText(string_three,null,"#808080");
                    }
                    Toast.makeText(getApplicationContext(),"Kiểm tra kết nối mạng!",Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }

    private class NotificationTask extends AsyncTask<Integer,Long,Integer>{
        ArrayList<NewsDataObject>  mData;
        @Override
        protected Integer doInBackground(Integer... integers) {
            try {
                mData = new ArrayList<>();
                for (OEntity DataFromDataBase: consumer
                        .getEntities("NhanXet_CanhBao").filter(" year(NgayCapNhat) eq "+ integers[0] +" and month(NgayCapNhat) eq "+ integers[1] +" and day(NgayCapNhat) eq "+ integers[2] +"").execute())
                {
                    NewsDataObject mNewsData = new NewsDataObject();
                    mNewsData.setSystemNotification(DataFromDataBase.getProperty("NoiDung",String.class).getValue());
                    mNewsData.setCategory(DataFromDataBase.getProperty("CapDo",Integer.class).getValue()==null?0:DataFromDataBase.getProperty("CapDo",Integer.class).getValue());
                    mData.add(mNewsData);
                }
                if(mData.isEmpty())
                    return 0;
                else
                    return 1;
            }
            catch (Exception ex)
            {
                return -1;
            }

        }

        @Override
        protected void onPostExecute(Integer integer) {

            switch (integer){
                case 1:{
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putInt("NumberOfMessage",mData.size());
                    editor.commit();
                    if( (mSharedPreferences.getInt("NumberOfMessage",0) != 0) && (mData.size()!= 0)){
                    mNotificationNumber.setVisibility(View.VISIBLE);
                    mNotificationNumber.setText(""+mData.size());}
                    break;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mdrawerLayout.isDrawerVisible(GravityCompat.START))
            mdrawerLayout.closeDrawer(GravityCompat.START);
        else{
            final Dialog dialog = new Dialog(DashBoard.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.exit_dialog);
            dialog.show();

            mExitDialogBtn = dialog.findViewById(R.id.exit_dialog_Exit);
            mCloseButton = dialog.findViewById(R.id.exit_dialog_close);

            mCloseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            mExitDialogBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishAffinity();
                }
            });

        }
        handler.removeCallbacks(runnable);
    }

    private void AnimationDisplay() {
        Animation topAnim, BotAnim, RightAnim, RightAnimTwo, RightAnimThree;
        topAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_anim);
        BotAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_anim);
        RightAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_anim);
        RightAnimTwo = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_anim_two);
        RightAnimThree = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_anim_three);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRelatedLayout.setAnimation(topAnim);
            mMap.setAnimation(RightAnimThree);
            bottomNavigationView.setAnimation(BotAnim);
        }
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(mNotificationBell, "Notification_transition");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(DashBoard.this, pairs);
        switch (menuItem.getItemId()) {
            case R.id.nav_days: {
                startActivity(new Intent(getApplicationContext(), Days.class), options.toBundle());
                break;
            }
            case R.id.nav_weeks: {
                startActivity(new Intent(getApplicationContext(), Weeks.class), options.toBundle());
                break;
            }
            case R.id.nav_months: {
                startActivity(new Intent(getApplicationContext(), Months.class), options.toBundle());
                break;
            }
            case R.id.nav_SignIn: {
                startActivity(new Intent(getApplicationContext(), SignIn.class), options.toBundle());
                break;
            }
            case R.id.nav_profile: {
                startActivity( new Intent(getApplicationContext(), UserProfile.class), options.toBundle());
                break;
            }
            case R.id.nav_notification:{
                startActivity(new Intent(getApplicationContext(),Notification_Activity.class),options.toBundle());
                break;
            }
            case R.id.nav_gantt_chart:
                startActivity(new Intent(getApplicationContext(),GanttChartActivity.class),options.toBundle());
                break;
            case R.id.nav_danhgia:
                InformationApp();
                break;
        }
        mdrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void InformationApp(){
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.information_app_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        alertDialog.show();
    }

    private void Display( ArrayList<MachineClass> MachineData) {
        int avaint = 0;
        int offint = 0;
        int waitint = 0;
        int readyint = 0;
        int setupint = 0;
        int runint = 0;
        int breint = 0;
        int disint = 100;
        for (MachineClass machineClass:MachineData){
            MachineValidation(machineClass);
            try {
                int id = getResources().getIdentifier("T"+machineClass.getMachineID(),"id",getPackageName());
                switch (machineClass.getMachineStatus()) {
                    case 0:
                        avaint ++;
                        break;
                    case 1:
                        offint++;
                        break;
                    case 2:
                        waitint++;
                        break;
                    case 3:
                        readyint++;
                        break;
                    case 4:
                        setupint++;
                        break;
                    case 5:
                        runint++;
                        break;
                    case 6:
                        breint++;
                        break;
                }
            }catch (Exception e){}
        }
        disint = disint -(offint+waitint+readyint+setupint+runint+breint+avaint);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ((TextView) findViewById(R.id.textNumberAvai)).setText(avaint+"");
            ((TextView) findViewById(R.id.textNumberOff)).setText(offint+"");
            ((TextView) findViewById(R.id.textNumberWait)).setText(waitint+"");
            ((TextView) findViewById(R.id.textNumberReady)).setText(readyint+"");
            ((TextView) findViewById(R.id.textNumberSetUp)).setText(setupint+"");
            ((TextView) findViewById(R.id.textNumberRun)).setText(runint+"");
            ((TextView) findViewById(R.id.textNumberBreDown)).setText(breint+"");
            ((TextView) findViewById(R.id.textNumberDisable)).setText(disint+"");
        }
    }

    private void DisplayText(String stringToId,String content,String color){
        try{
            int id = getResources().getIdentifier(stringToId,"id",getPackageName());
            findViewById(id).setVisibility(View.VISIBLE);
            if (content != null) {
                ((TextView) findViewById(id)).setText(content == null ? "" : content);
            }
            if (color !=null) {
                ((TextView) findViewById(id)).setTextColor(Color.parseColor(color));
            }
        }catch (Exception exception){}
    }

    private void DisplayImg(String stringToId,int imgAddress){
        try {
            int id = getResources().getIdentifier(stringToId, "id", getPackageName());
            findViewById(id).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(id)).setImageResource(imgAddress);
        } catch (Exception e){}
    }

    private void MachineValidation(MachineClass machineClass){
        String string_one = "M" + machineClass.getMachineID();
        String string_two = "T"+ machineClass.getMachineID();
        String string_three = "N" + machineClass.getMachineID();
        // Defining Machine Status : 0-Available ; 1-Off ; 2-Waiting ; 3-Ready ; 4-SetUp ; 5-Run ; 6-BreakDown
        switch (machineClass.getMachineStatus()){
            case 0:
                DisplayImg(string_one,R.drawable.available);
                DisplayText(string_two,"Available",null);
                DisplayText(string_three,null,"#964B00");
                break;
            case 1:
                DisplayImg(string_one,R.drawable.icon_cnc_off);
                DisplayText(string_two,"Off",null);
                DisplayText(string_three,null,"#808080");
                break;
            case 2:
                DisplayImg(string_one,R.drawable.icon_cnc_ready);
                DisplayText(string_two,"Wait",null);
                DisplayText(string_three,null,"#FF9A00");
                break;
            case 3:
                DisplayImg(string_one,R.drawable.icon_cnc_wait);
                DisplayText(string_two,"Ready",null);
                DisplayText(string_three,null,"#ffff00");
                break;
            case 4:
                DisplayImg(string_one,R.drawable.icons_setup);
                DisplayText(string_two,"SetUp",null);
                DisplayText(string_three,null,"#5DB9FA");
                break;
            case 5:
                DisplayImg(string_one,R.drawable.cnc_working);
                DisplayText(string_two,"Run",null);
                DisplayText(string_three,null,"#00ff00");
                break;
            default:
                DisplayImg(string_one,R.drawable.error_icon);
                DisplayText(string_two,"BreDown",null);
                DisplayText(string_three,null,"#ff0000");
                break;
        }
    }

///////////////////////////////////////////////////////////////////////////////////////////////////
}



