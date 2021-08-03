package com.example.myfinalappsproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.joda.time.LocalDateTime;
import org.odata4j.consumer.ODataClientRequest;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.ODataConsumers;
import org.odata4j.consumer.behaviors.OClientBehavior;
import org.odata4j.core.ODataVersion;
import org.odata4j.core.OEntity;
import java.util.ArrayList;

public class Notification_Activity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,RecycleViewClickInterface {

    RecyclerView mRecycleView;
    Notification_news_Adapter NewsAdapter;
    ImageView mBackBtn;
    BottomNavigationView mBottomNavigation;
    ODataConsumer consumer;
    Handler handler;
    Runnable runnable;
    private long TimeReMaining = 3600000;// Time limit to execute the DataDisPlays
    private long TimeRepeatTask = 60000; // Repeat the task with cycle of 60.000 milliSecond
    String TAG = Notification_Activity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String serviceUrl = ServiceAddress.serviceURL;
        OClientBehavior bacsicAthur = new OClientBehavior() {
            @Override
            public ODataClientRequest transform(ODataClientRequest request) {
                //return request.header("Accept", "application/json");
                return request.header("MaxDataServiceVersion", ODataVersion.V2.asString);
            }
        };
        consumer = ODataConsumers.newBuilder(serviceUrl).setClientBehaviors(bacsicAthur).build();

        Hooks();
        BottomNavigation();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                TimeReMaining = TimeReMaining - TimeRepeatTask; // Reduce time by TimeRepeatTask
                LocalDateTime localDateTime = new LocalDateTime();
                new NotificationTask().execute(localDateTime.getYear(),localDateTime.getMonthOfYear(),localDateTime.getDayOfMonth());
                handler.postDelayed(this,TimeRepeatTask);  // Do the task after TimeRepeatTask
            }
        };
//        handler.postDelayed(runnable,500); // Original time value
    }

    private void BottomNavigation() {
        mBottomNavigation.setOnNavigationItemSelectedListener(this);
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

    private void NotificationContent(ArrayList<NewsDataObject>  mData) {
        NewsAdapter = new Notification_news_Adapter(this,mData,this);
        NewsAdapter.notifyDataSetChanged();
        mRecycleView.setAdapter(NewsAdapter);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void Hooks() {
        mBackBtn = findViewById(R.id.notification_backBtn);
        mRecycleView = findViewById(R.id.Notification_news);
        mBottomNavigation = findViewById(R.id.Notification_BottomNav);
    }

    public void Back(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_home:
            {
                Intent intent = new Intent(Notification_Activity.this,DashBoard.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.nav_days:
            {
                Intent intent = new Intent(Notification_Activity.this,Days.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_weeks:
            {
                Intent intent = new Intent(Notification_Activity.this, Weeks.class);
                startActivity(intent);
                break;
            }
        }
        return true;
    }

    class NotificationTask extends AsyncTask<Integer,Long,Integer>{
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
                    mNewsData.setCategory(DataFromDataBase.getProperty("CapDo",Integer.class).getValue());
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
                    NotificationContent(mData);
                    Toast.makeText(Notification_Activity.this,"Cập nhật thông báo!",Toast.LENGTH_LONG).show();
                    break;
                }
                case 0:{
                    Toast.makeText(Notification_Activity.this,"Không có thông báo nào!",Toast.LENGTH_LONG).show();
                    break;
                }
                case -1:{
                    break;
                }
            }
        }
    }

    @Override
    public void OnItemClick(int position) {
        NewsAdapter.mData.remove(position);
        NewsAdapter.notifyItemRemoved(position);
    }
}
