package com.example.myfinalappsproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.odata4j.consumer.ODataClientRequest;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.ODataConsumers;
import org.odata4j.consumer.behaviors.OClientBehavior;
import org.odata4j.core.ODataVersion;
import org.odata4j.core.OEntity;
import org.joda.time.LocalDateTime;

public class DetailInformation extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    ODataConsumer consumer;
    String TAG = DetailInformation.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_information);

        String serviceUrl = ServiceAddress.serviceURL;
        OClientBehavior bacsicAthur = new OClientBehavior() {
            @Override
            public ODataClientRequest transform(ODataClientRequest request) {
                //return request.header("Accept", "application/json");
                return request.header("MaxDataServiceVersion", ODataVersion.V2.asString);
            }
        };
        consumer = ODataConsumers.newBuilder(serviceUrl).setClientBehaviors(bacsicAthur).build();

        ((BottomNavigationView) findViewById(R.id.DetailInfor_bottom_nav)).setOnNavigationItemSelectedListener(this);
        DisplayInformation();
    }

    private void DisplayInformation() {
        ((TextView)findViewById(R.id.DetailInfor_DayCardOne)).setText(new LocalDateTime().toString("dd-MMM"));
        ((TextView)findViewById(R.id.DetailInfor_DayCardTwo)).setText(new LocalDateTime().toString("dd-MMM"));
        ((TextView)findViewById(R.id.DetailInfor_DayCardThree)).setText(new LocalDateTime().toString("dd-MMM"));
        Intent intent = getIntent();
        new LoadDataCongViecTask().execute(intent.getIntExtra("IDCongViec",0));
        new LoadDataNhanVienTask().execute(intent.getIntExtra("IDNhanVien",0));
        new LoadDataMayTask().execute(intent.getIntExtra("ID May",0));
    } // Display all information

    private class LoadDataCongViecTask extends AsyncTask<Integer,Integer,Integer>{
        ProductObject productObject;
        @Override
        protected Integer doInBackground(Integer... integers) {
            try{
                OEntity entityCongViec = consumer.getEntity("CongViecs",integers[0]).execute();
                productObject = new ProductObject();
                productObject.setProductStartTime(entityCongViec.getProperty("GioBD",LocalDateTime.class).getValue());
                productObject.setProductName(entityCongViec.getProperty("MaChiTiet",String.class).getValue());
                if (null!= entityCongViec.getProperty("CongViec_ChiTiet2",Integer.class).getValue()) {
                    OEntity entityChiTiet = consumer.getEntity("ChiTiets", entityCongViec.getProperty("CongViec_ChiTiet2", Integer.class).getValue()).execute();
                    productObject.setProductID(entityCongViec.getProperty("CongViec_ChiTiet2",Integer.class).getValue());
                    productObject.setProductName(entityChiTiet.getProperty("MaSo",String.class).getValue());
                    productObject.setProductDiscp(entityChiTiet.getProperty("Ten",String.class).getValue());
                    productObject.setProductAmmount(null==entityChiTiet.getProperty("SoLuong",Integer.class).getValue()?0:entityChiTiet.getProperty("SoLuong",Integer.class).getValue());
                }
                return 1;
            }catch (Exception exception){
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            switch (integer){
                case 1:
                    ((TextView) findViewById(R.id.DetailInfor_MachineStartTime)).setText(productObject.getProductStartTime().toString("HH:mm"));
                    ((TextView) findViewById(R.id.DetailInfor_ProductName)).setText(null==productObject.getProductDiscp()||productObject.getProductDiscp().equals("")?"NULL":productObject.getProductDiscp());
                    ((TextView) findViewById(R.id.DetailInfor_ProductID)).setText(null==productObject.getProductName()||productObject.getProductName().equals("")?"NULL":productObject.getProductName());
                    ((TextView) findViewById(R.id.DetailInfor_ProductAmount)).setText(productObject.getProductAmmount()+"");
            }
        }
    }

    private class LoadDataNhanVienTask extends AsyncTask<Integer,Integer,Integer>{
        String hoTen,maNV,sdt,email,tenTo;
        @Override
        protected Integer doInBackground(Integer... integers) {
            try{
                OEntity entityNhanVien = consumer.getEntity("NhanViens",integers[0]).execute();
                hoTen = entityNhanVien.getProperty("HoTen",String.class).getValue();
                maNV = entityNhanVien.getProperty("MaSo",String.class).getValue();
                email = entityNhanVien.getProperty("Email",String.class).getValue();
                if (null!=entityNhanVien.getProperty("NhanVien_ToSX",Integer.class).getValue()){
                    OEntity entityToSX = consumer.getEntity("ToSXes",entityNhanVien.getProperty("NhanVien_ToSX",Integer.class).getValue()).execute();
                    tenTo = entityToSX.getProperty("Ten",String.class).getValue();
                }
                return 1;
            }catch (Exception exception){
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            switch (integer){
                case 1:
                    ((TextView) findViewById(R.id.DetailInfor_WorkerName)).setText(null==hoTen||hoTen.equals("")?"NULL":hoTen);
                    ((TextView) findViewById(R.id.DetailInfor_WorkerID)).setText(null==maNV||maNV.equals("")?"NULL":maNV);
                    ((TextView) findViewById(R.id.DetailInfor_WorkerSDT)).setText(null==sdt||sdt.equals("")?"NULL":sdt);
                    ((TextView) findViewById(R.id.DetailInfor_WorkerEmail)).setText(null==email||email.equals("")?"NULL":email);
                    ((TextView) findViewById(R.id.DetailInfor_WorkerGroup)).setText(null==tenTo||tenTo.equals("")?"NULL":tenTo);
            }
        }
    }

    private class LoadDataMayTask extends AsyncTask<Integer,Integer,Integer>{
        String maMay,tenMay,xuatSu,trangThai;
        @Override
        protected Integer doInBackground(Integer... integers) {
            try{
                OEntity entityMay = consumer.getEntity("Mays",integers[0]).execute();
                maMay = entityMay.getProperty("MaSo",String.class).getValue();
                tenMay = entityMay.getProperty("Ten",String.class).getValue();
                xuatSu = entityMay.getProperty("NhanHieu",String.class).getValue();
                for (OEntity entityTinhTrang:consumer.getEntities("TinhTrangMays").filter("may eq "+integers[0]).execute()){
                    switch (entityTinhTrang.getProperty("trangThai",Integer.class).getValue()){
                        case 0:
                            trangThai ="Available";
                            break;
                        case 1:
                            trangThai = "Off";
                            break;
                        case 2:
                            trangThai = "Waiting";
                            break;
                        case 3:
                            trangThai = "Ready";
                            break;
                        case 4:
                            trangThai = "SetUp";
                            break;
                        case 5:
                            trangThai = "Run";
                            break;
                        case 6:
                            trangThai = "BreakDown";
                            break;
                        default:
                            trangThai = "Disable";
                    }
                }
                return 1;
            }catch (Exception exception){
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            switch (integer){
                case 1:
                    ((TextView) findViewById(R.id.DetailInfor_MachineID)).setText(null==maMay?"NULL":maMay);
                    ((TextView) findViewById(R.id.DetailInfor_MachineDuty)).setText(null==tenMay?"NULL":tenMay);
                    ((TextView) findViewById(R.id.DetailInfor_MachineOrigin)).setText(null==xuatSu?"NULL":xuatSu);
                    ((TextView) findViewById(R.id.DetailInfor_MachineStatus)).setText(trangThai);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void Back(View view){
        onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_days:
            {
                Intent intent = new Intent(getApplicationContext(),Days.class);
                startActivity(intent);
                break;
            }
            case  R.id.nav_weeks:
            {
                Intent intent = new Intent(getApplicationContext(),Weeks.class);
                startActivity(intent);
                break;
            }
            case R.id.nav_home:
            {
                Intent intent = new Intent(getApplicationContext(),DashBoard.class);
                startActivity(intent);
                finish();
                break;
            }

        }
        return true;
    }
}
