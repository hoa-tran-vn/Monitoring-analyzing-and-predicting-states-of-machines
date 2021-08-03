package com.example.myfinalappsproject;

import android.util.Log;

import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import java.util.ArrayList;
import java.util.Calendar;

public class MyGanttChartObject {
    private int Id_May;
    private String May;
    private ArrayList<MachineClass> machineClasses;
    private ArrayList<CongViec> congViecs;
    private ArrayList<String> listTrangThai;
    private LocalDateTime dateTimeStart;
    private boolean isTitle;
    private int frame;
    private boolean DaLapBGS;

    public ArrayList<CongViec> getCongViecs() {
        return congViecs;
    }

    public void setCongViecs(ArrayList<CongViec> congViecs) {
        this.congViecs = congViecs;
    }

    public boolean isDaLapBGS() {
        return DaLapBGS;
    }

    public void setDaLapBGS(boolean daLapBGS) {
        DaLapBGS = daLapBGS;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public int getFrame() {
        return frame;
    }

    public void setListTrangThai(ArrayList<String> listTrangThai) {
        this.listTrangThai = listTrangThai;
    }

    public MyGanttChartObject(ArrayList<String> listTrangThai, boolean isTitle) {
        this.listTrangThai = listTrangThai;
        this.isTitle = isTitle;
    }

    public boolean isTitle() {
        return isTitle;
    }

    public MyGanttChartObject(int id_May, String may) {
        Id_May = id_May;
        May = may;
    }

    public MyGanttChartObject(int id_May, String may, ArrayList<MachineClass> machineClasses) {
        Id_May = id_May;
        May = may;
        this.machineClasses = machineClasses;
    }

    public LocalDateTime getDateTimeStart() {
        int position=0;
        while (null!=machineClasses && machineClasses.size()!=0 && position < machineClasses.size()-1 && machineClasses.get(position).getMachineStatus()<3){
            position++;
        }
        if (null!= machineClasses && machineClasses.size()!=0){
            if (position >= machineClasses.size())
                return machineClasses.get(machineClasses.size()-1).getMachineDateTime();
            else
                return machineClasses.get(position).getMachineDateTime();
        }else
            return dateTimeStart;
    }

    public void setDateTimeStart(LocalDateTime dateTimeStart) {
        this.dateTimeStart = dateTimeStart;
    }

    public int getId_May() {
        return Id_May;
    }

    public void setId_May(int id_May) {
        Id_May = id_May;
    }

    public String getMay() {
        return May;
    }

    public void setMay(String may) {
        May = may;
    }

    public ArrayList<MachineClass> getMachineClasses() {
        return machineClasses;
    }

    public void setMachineClasses(ArrayList<MachineClass> machineClasses) {
        this.machineClasses = machineClasses;
    }

    public ArrayList<String> getListTrangThai() {
            if (!isTitle) {
                LoadListTrangThai();
            }
        return listTrangThai;
    }

    private void LoadListTrangThai(){
        LocalDateTime tempdateTimeStart;
        if (null==dateTimeStart) {
            Calendar calendar = Calendar.getInstance();
            calendar.set((Calendar.getInstance()).get(Calendar.YEAR), (Calendar.getInstance()).get(Calendar.MONTH), (Calendar.getInstance()).get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            tempdateTimeStart = LocalDateTime.fromCalendarFields(calendar);
        }else {
            tempdateTimeStart = dateTimeStart;
        }
        tempdateTimeStart = tempdateTimeStart.minusMinutes(tempdateTimeStart.getMinuteOfHour());
        if (null==listTrangThai) {
            listTrangThai = new ArrayList<>();
        }else {
            listTrangThai.clear();
        }
        if (null!=machineClasses && 0!=machineClasses.size()) {
            int a = 60/frame;
            for (int i=0;i<machineClasses.size();i++) {
                int b = (int) Minutes.minutesBetween(tempdateTimeStart,machineClasses.get(i).getMachineDateTime()).getMinutes()/a;
                for (int j = 0;j<b;j++){
                    try {
                        listTrangThai.add(machineClasses.get(i-1).getMachineStatus()+"");
                    }catch (Exception e) {
                        listTrangThai.add("1");
                    }
                }
                if (b>0) {
                    tempdateTimeStart = tempdateTimeStart.plusMinutes(b * a);
                }
            }
            listTrangThai.add(machineClasses.get(machineClasses.size()-1).getMachineStatus()+"");
        }else {
            if (isDaLapBGS()){
                listTrangThai.add("0");
            }else {
                listTrangThai.add("9");
            }
        }
    }
}
