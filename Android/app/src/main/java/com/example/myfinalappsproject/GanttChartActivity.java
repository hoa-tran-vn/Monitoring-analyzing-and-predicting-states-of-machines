package com.example.myfinalappsproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.Hours;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.odata4j.consumer.ODataClientRequest;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.ODataConsumers;
import org.odata4j.consumer.behaviors.OClientBehavior;
import org.odata4j.core.ODataVersion;
import org.odata4j.core.OEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GanttChartActivity extends AppCompatActivity {

    private ArrayList<MyGanttChartObject> myGanttChartObjects;
    private ODataConsumer consumer;
    private NameViewModel viewModel;
    private String TAG = GanttChartActivity.class.getSimpleName();
    private long TimeRepeatTask = 150000;
    // Time limit to execute the DataDisPlays
    // Repeat the task with cycle of 60.000 milliSecond
    private Runnable runnable;
    private Handler handler;
    private SharedPreferences sharedPreferences;
    private int frame = 30,SLMayMax = 25,startList,endList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gantt_chart);

        sharedPreferences = getSharedPreferences("GanttChart",MODE_PRIVATE);
        startList = 0;
        endList = SLMayMax;
        String serviceUrl = ServiceAddress.serviceURL;
        OClientBehavior bacsicAthur = new OClientBehavior() {
            @Override
            public ODataClientRequest transform(ODataClientRequest request) {
                //return request.header("Accept", "application/json");
                return request.header("MaxDataServiceVersion", ODataVersion.V2.asString);
            }
        };
        consumer = ODataConsumers.newBuilder(serviceUrl).setClientBehaviors(bacsicAthur).build();
        viewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(NameViewModel.class);
        Observer<ArrayList<MyGanttChartObject>> observer = new Observer<ArrayList<MyGanttChartObject>>() {
            @Override
            public void onChanged(ArrayList<MyGanttChartObject> myGanttChartObjects) {
                GanttChartActivity.this.myGanttChartObjects = myGanttChartObjects;
                System.gc();
            }
        };
        viewModel.getMutableLiveData().observe(this,observer);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (null == myGanttChartObjects || 0 == myGanttChartObjects.size() || sharedPreferences.getString("Machine", "").equals("")) {
                    myGanttChartObjects = new ArrayList<>();
                    try {
                        for (String s : sharedPreferences.getString("Machine", "").split("-")) {
                            String[] strings = s.split(":");
                            myGanttChartObjects.add(new MyGanttChartObject(Integer.parseInt(strings[0].trim()),strings[1]));
                        }
                    } catch (Exception exception) { }
                }
                if (0 == myGanttChartObjects.size()) {
                    ((ListView) findViewById(R.id.listViewGanttChart)).setAdapter(new MyGanttChartAdapter(new ArrayList<>(), GanttChartActivity.this, 1));
                } else {
                    new LoadDataGanttChart().execute();
                }
                handler.postDelayed(this,TimeRepeatTask);  // Do the task after TimeRepeatTask
            }
        };

        findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoadListMachineTask().execute();
            }
        });
        findViewById(R.id.GanttChart_notificationBell).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Notification_Activity.class));
            }
        });

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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_DPAD_UP:
                if (View.VISIBLE == findViewById(R.id.imageViewUp).getVisibility()){
                    AdditionList(getCurrentFocus());
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (View.VISIBLE == findViewById(R.id.imageViewDown).getVisibility()){
                    SubtractionList(getCurrentFocus());
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                TransferToDashBoard(getCurrentFocus());
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void TransferToDashBoard(View view){
        Intent intent = new Intent(GanttChartActivity.this,DashBoard.class);
        startActivity(intent);
    }

    void UpdateButton(){
        if (SLMayMax>=myGanttChartObjects.size()){
            findViewById(R.id.imageViewDown).setVisibility(View.INVISIBLE);
            findViewById(R.id.imageViewUp).setVisibility(View.INVISIBLE);
        }else if (0==startList){
            findViewById(R.id.imageViewDown).setVisibility(View.INVISIBLE);
            findViewById(R.id.imageViewUp).setVisibility(View.VISIBLE);
        }else if ((myGanttChartObjects.size()-1)<=endList){
            findViewById(R.id.imageViewUp).setVisibility(View.INVISIBLE);
            findViewById(R.id.imageViewDown).setVisibility(View.VISIBLE);
            endList=myGanttChartObjects.size();
        }else {
            findViewById(R.id.imageViewDown).setVisibility(View.VISIBLE);
            findViewById(R.id.imageViewUp).setVisibility(View.VISIBLE);
        }
    }

    public void AdditionList(View view){
        findViewById(R.id.imageViewDown).setVisibility(View.VISIBLE);
        startList+=SLMayMax;
        endList+=SLMayMax;
        if (endList >= (myGanttChartObjects.size()-1)){
            endList=myGanttChartObjects.size();
            findViewById(R.id.imageViewUp).setVisibility(View.INVISIBLE);
        }
        if (null==myGanttChartObjects.get(startList).getMachineClasses()){
            new LoadDataGanttChart().execute();
        }else {
            ProcessBarDisPlay(myGanttChartObjects.subList(startList, endList));
        }
    }

    public void SubtractionList(View view){
        findViewById(R.id.imageViewUp).setVisibility(View.VISIBLE);
        if (endList==myGanttChartObjects.size()){
            startList -= SLMayMax;
            endList -= (myGanttChartObjects.size()%SLMayMax==0?SLMayMax:myGanttChartObjects.size()%SLMayMax);
        }else {
            startList -= SLMayMax;
            endList -= SLMayMax;
        }
        if (startList<=0){
            startList=0;
            findViewById(R.id.imageViewDown).setVisibility(View.INVISIBLE);
        }
        if (null==myGanttChartObjects.get(startList).getMachineClasses()){
            new LoadDataGanttChart().execute();
        }else {
            ProcessBarDisPlay(myGanttChartObjects.subList(startList, endList));
        }
    }

    private void ProcessBarDisPlay(List<MyGanttChartObject> myGanttChartObjectList){
        ArrayList<MyGanttChartObject> myGanttChartObjectArrayList = new ArrayList<>(myGanttChartObjectList);
        int lengthMax = 0;
        LocalDateTime dateTimeStart = null;
        for (MyGanttChartObject myGanttChartObject: myGanttChartObjectArrayList){
            myGanttChartObject.setFrame(frame);
            if ( null == dateTimeStart){
                dateTimeStart = myGanttChartObject.getDateTimeStart();
            }else {
                try {
                    if (Minutes.minutesBetween(dateTimeStart, myGanttChartObject.getDateTimeStart()).getMinutes() < 0) {
                        dateTimeStart = myGanttChartObject.getDateTimeStart();
                    }
                } catch (Exception e){}
            }
        }
        for (MyGanttChartObject myGanttChartObject: myGanttChartObjectArrayList){
            myGanttChartObject.setDateTimeStart(dateTimeStart);
            lengthMax = lengthMax<myGanttChartObject.getListTrangThai().size()?myGanttChartObject.getListTrangThai().size():lengthMax;
        }
        List<String> stringsTitle = new ArrayList<>();
        for (int i = 0; ((((i)*frame)<lengthMax)&& lengthMax > frame*8) || i < 8 ; i++){
            stringsTitle.add(5>(((int) dateTimeStart.getHourOfDay()+i) +":00").length()?("0"+((int) dateTimeStart.getHourOfDay()+i) +":00"):(((int) dateTimeStart.getHourOfDay()+i) +":00"));
        }
        myGanttChartObjectArrayList.add(0,new MyGanttChartObject((ArrayList<String>) stringsTitle,true));
        ListView listView = findViewById(R.id.listViewGanttChart);
        MyCellViewGanttChartAdapter myCellViewGanttChartAdapter = new MyCellViewGanttChartAdapter(myGanttChartObjectArrayList.get(0).getListTrangThai(), GanttChartActivity.this, lengthMax, myGanttChartObjectArrayList.get(0).isTitle(), frame, 8);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(GanttChartActivity.this, 8);
        ((RecyclerView) findViewById(R.id.recyclerView)).setLayoutManager(layoutManager);
        ((RecyclerView) findViewById(R.id.recyclerView)).setAdapter(myCellViewGanttChartAdapter);
        MyGanttChartAdapter adapter = new MyGanttChartAdapter(myGanttChartObjectArrayList,this, lengthMax);
        listView.setAdapter(adapter);
    }// Draw GanttChart

    public void Back(View view) {
        onBackPressed();
    } // BackPress function

    private class LoadDataGanttChart extends AsyncTask<String,Integer,Integer>{
        @Override
        protected Integer doInBackground(String... strings) {
            try {
                Integer integerStart = 0;
                LocalDateTime dateTime = new LocalDateTime();
                for (OEntity entityLichSuMay : consumer.getEntities("LichSuMays").orderBy("Id desc").top(1).execute()) {
                    dateTime = entityLichSuMay.getProperty("ThoiGianCapNhat", LocalDateTime.class).getValue();
                }
                for (MyGanttChartObject myGanttChartObject: myGanttChartObjects.size()<endList?myGanttChartObjects:myGanttChartObjects.subList(startList, endList)){
                    try {
                        integerStart = integerStart>myGanttChartObject.getMachineClasses().get(myGanttChartObject.getMachineClasses().size()-1).getId()?integerStart:myGanttChartObject.getMachineClasses().get(myGanttChartObject.getMachineClasses().size()-1).getId();
                    }catch (Exception exception){}
                }
                if (integerStart.equals(0)) {
                    for (OEntity entityLichSuMay : consumer.getEntities("LichSuMays")
                            .filter("ThoiGianCapNhat ge datetime'" + dateTime.toString("yyyy-MM-dd") + "T00:00:00.000'").top(1).execute()) {
                        integerStart = entityLichSuMay.getProperty("Id", Integer.class).getValue();
                    }
                }
                for (MyGanttChartObject myGanttChartObject : myGanttChartObjects.size()<endList?myGanttChartObjects:myGanttChartObjects.subList(startList, endList)) {
                    if (null == myGanttChartObject.getMay() || 0 == myGanttChartObject.getMay().length()) {
                        OEntity entityMay = consumer.getEntity("Mays", myGanttChartObject.getId_May()).execute();
                        myGanttChartObject.setMay(entityMay.getProperty("MaSo", String.class).getValue());
                    }
                    String filter;
                    if (null == myGanttChartObject.getMachineClasses() || 0 == myGanttChartObject.getMachineClasses().size()) {
                        filter = "Id ge " + integerStart + " and GiamSatMay eq " + myGanttChartObject.getId_May();
                        myGanttChartObject.setMachineClasses(new ArrayList<>());
                    } else {
                        filter = "Id gt " + myGanttChartObject.getMachineClasses().get(myGanttChartObject.getMachineClasses().size() - 1).getId() + " and GiamSatMay eq " + myGanttChartObject.getId_May();
                    }
                    for (OEntity entityLichSuMay : consumer.getEntities("LichSuMays")
                            .filter(filter)
                            .orderBy("ThoiGianCapNhat asc").execute()) {
                        if (0 == myGanttChartObject.getMachineClasses().size() || !entityLichSuMay.getProperty("Id", Integer.class).getValue().equals(myGanttChartObject.getMachineClasses().get(myGanttChartObject.getMachineClasses().size() - 1).getId())) {
                            MachineClass machinePowerClass = new MachineClass();
                            machinePowerClass.setId(entityLichSuMay.getProperty("Id", Integer.class).getValue());
                            machinePowerClass.setMachineStatus(entityLichSuMay.getProperty("trangThai", Integer.class).getValue());
                            machinePowerClass.setMachineDateTime(entityLichSuMay.getProperty("ThoiGianCapNhat", LocalDateTime.class).getValue());
                            myGanttChartObject.getMachineClasses().add(machinePowerClass);
                        }
                    }
                    if (null != myGanttChartObject.getMachineClasses() && 0 != myGanttChartObject.getMachineClasses().size()) {
                        filter = "((GioBD gt datetime'" + (myGanttChartObject.getMachineClasses().get(myGanttChartObject.getMachineClasses().size() - 1).getMachineDateTime()).minusHours(12).toString() + "' and GioBD_BS eq null )" +
                                " or (GioBD_BS ne null and GioBD_BS gt datetime'" + (myGanttChartObject.getMachineClasses().get(myGanttChartObject.getMachineClasses().size() - 1).getMachineDateTime()).minusHours(12).toString() + "')) " +
                                "and CongViec_May1 eq " + myGanttChartObject.getId_May();
                        ArrayList<CongViec> congViecs = new ArrayList<>();
                        myGanttChartObject.setCongViecs(congViecs);
                        for (OEntity entityCongViec : consumer.getEntities("CongViecs").filter(filter).execute()) {
                            OEntity entityNV = consumer.getEntity("NhanViens",entityCongViec.getProperty("CongViec_NhanVien",Integer.class).getValue()).execute();

                            LocalDateTime GioBD = (null!=entityCongViec.getProperty("GioBD_BS",LocalDateTime.class).getValue()?entityCongViec.getProperty("GioBD_BS",LocalDateTime.class).getValue():entityCongViec.getProperty("GioBD",LocalDateTime.class).getValue()),
                                    GioKT = (null!=entityCongViec.getProperty("GioKT_BS",LocalDateTime.class).getValue()?entityCongViec.getProperty("GioKT_BS",LocalDateTime.class).getValue():entityCongViec.getProperty("GioKT",LocalDateTime.class).getValue());
                            if (null==GioKT || ((GioBD.getHourOfDay()+"").equals((GioKT.getHourOfDay()+"")) && Minutes.minutesBetween(GioBD,GioKT).getMinutes()>30)) {
                                congViecs.add(new CongViec(entityCongViec.getProperty("Id", Integer.class).getValue(), entityNV.getProperty("HoTen", String.class).getValue(), GioBD, GioKT));
                                if (null==GioKT){
                                    break;
                                }
                            }
                        }
                        Collections.sort(congViecs);
                    }
                    if (null == myGanttChartObject.getMachineClasses() || 0 == myGanttChartObject.getMachineClasses().size()) {
                        for (OEntity entityThoiGianMay : consumer.getEntities("ThoiGianMays").filter("may eq " + myGanttChartObject.getId_May()).execute()) {
                            myGanttChartObject.setDaLapBGS(true);
                        }
                    }
                    myGanttChartObject.setDateTimeStart(dateTime);
                    Collections.sort(myGanttChartObject.getMachineClasses());
                    while (null != myGanttChartObject.getMachineClasses() && 0 != myGanttChartObject.getMachineClasses().size() && !myGanttChartObject.getMachineClasses().get(0).getMachineDateTime().toString("dd/MM/yyyy").equals(dateTime.toString("dd/MM/yyyy"))) {
                        myGanttChartObject.getMachineClasses().remove(0);
                    }
                }
                return 1;
            }catch (Exception exception){
                return -1;
            }
        }
        @Override
        protected void onPostExecute(Integer integer) {
            switch (integer) {
                case 1:
                    viewModel.getMutableLiveData().setValue(myGanttChartObjects);
                    if (endList>myGanttChartObjects.size()){
                        ProcessBarDisPlay(myGanttChartObjects);
                    }else {
                        ProcessBarDisPlay(myGanttChartObjects.subList(startList, endList));
                    }
                    UpdateButton();
                    System.gc();
            }
        }
    }

    private class LoadListMachineTask extends AsyncTask<Integer,Integer,Integer>{
        
        ArrayList<MachineObject> machineObjects;

        @Override
        protected Integer doInBackground(Integer... integers) {
            try{
                List<Integer> list = new ArrayList<>();
                try {
                    for (String s : (sharedPreferences.getString("Machine","")).split("-")){
                        String[] strings = s.split(":");
                        list.add(Integer.parseInt(strings[0].trim()));
                    }
                }catch (Exception exception){}
                machineObjects = new ArrayList<>();
                for (OEntity entityMachine:consumer.getEntities("Mays").execute()){
                    machineObjects.add( new MachineObject(entityMachine.getProperty("Id", Integer.class).getValue(), entityMachine.getProperty("MaSo", String.class).getValue(),null==entityMachine.getProperty("May_ToSX",Integer.class).getValue()?0:entityMachine.getProperty("May_ToSX",Integer.class).getValue(), (list.indexOf(entityMachine.getProperty("Id", Integer.class).getValue()) != -1)));
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
                    System.gc();
                    LayoutInflater inflater = (LayoutInflater) GanttChartActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.dialog_search, null);
                    MaChineAdapter maChineAdapter = new MaChineAdapter(GanttChartActivity.this,machineObjects);
                    final AlertDialog alertDialog = new AlertDialog.Builder(GanttChartActivity.this)
                            .setView(view)
                            .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("Machine", maChineAdapter.LoadListString(SLMayMax));
                                    editor.commit();
                                    if (!sharedPreferences.getString("Machine","").equals("")) {
                                        if (null!= myGanttChartObjects) {
                                            myGanttChartObjects = new ArrayList<>();
                                        }else if(0!=myGanttChartObjects.size()){
                                            myGanttChartObjects.clear();
                                        }
                                        try {
                                            for (String s : sharedPreferences.getString("Machine", "").split("-")) {
                                                String[] strings = s.split(":");
                                                myGanttChartObjects.add(new MyGanttChartObject(Integer.parseInt(strings[0].trim()),strings[1]));
                                            }
                                        } catch (Exception exception) {
                                        }
                                        if (0== myGanttChartObjects.size()){
                                            ((ListView) findViewById(R.id.listViewGanttChart)).setAdapter(new MyGanttChartAdapter(new ArrayList<>(),GanttChartActivity.this, 1));
                                        }else
                                            new LoadDataGanttChart().execute();
                                    }else {
                                        ((ListView) findViewById(R.id.listViewGanttChart)).setAdapter(new MyGanttChartAdapter(new ArrayList<>(), GanttChartActivity.this, 1));
                                    }
                                }
                            })
                            .create();
                    alertDialog.show();
                    ((ListView) alertDialog.findViewById(R.id.listViewShow)).setAdapter(maChineAdapter);
                    ((EditText) alertDialog.findViewById(R.id.editTextSearch1)).addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            //do nothing
                        }
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            //do nothing
                        }
                        @Override
                        public void afterTextChanged(Editable s) {
                            maChineAdapter.getFilter().filter(((EditText) alertDialog.findViewById(R.id.editTextSearch1)).getText().toString()+"-"+((EditText) alertDialog.findViewById(R.id.editTextSearch2)).getText().toString());
                        }
                    });
                    ((EditText) alertDialog.findViewById(R.id.editTextSearch2)).addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            //do nothing
                        }
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            //do nothing
                        }
                        @Override
                        public void afterTextChanged(Editable s) {
                            maChineAdapter.getFilter().filter(((EditText) alertDialog.findViewById(R.id.editTextSearch1)).getText().toString()+"-"+((EditText) alertDialog.findViewById(R.id.editTextSearch2)).getText().toString());
                        }
                    });
            }
        }
    }
}