package com.example.myfinalappsproject;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyGanttChartAdapter extends BaseAdapter {
    private List<MyGanttChartObject> myGanttChartObjects;
    private Activity activity;
    private int lengthMax;
    private String TAG = MyGanttChartAdapter.class.getSimpleName();

    public MyGanttChartAdapter(List<MyGanttChartObject> myGanttChartObjects, Activity activity, int lengthMax) {
        this.myGanttChartObjects = myGanttChartObjects;
        this.activity = activity;
        this.lengthMax = lengthMax;
    }

    @Override
    public int getCount() {
        return myGanttChartObjects.size()-1;
    }

    @Override
    public MyGanttChartObject getItem(int i) {
        return myGanttChartObjects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (null==convertView){
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cell_view_gantt_chart,null);
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.textViewTenMay);
            holder.recyclerView = convertView.findViewById(R.id.recyclerViewStatus);
            holder.recyclerViewTime = convertView.findViewById(R.id.recyclerViewTime);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        MyGanttChartObject myGanttChartObject = myGanttChartObjects.get(i+1);
        holder.textView.setText(myGanttChartObject.getMay());
        int hour = 8;
        int frame = myGanttChartObject.getFrame();
        MyCellViewGanttChartAdapter adapter = new MyCellViewGanttChartAdapter(myGanttChartObject.getListTrangThai(), convertView.getContext(), lengthMax, myGanttChartObject.isTitle(),frame,hour);
        if (!myGanttChartObject.isTitle() && null != myGanttChartObject.getCongViecs() && 0 != myGanttChartObject.getCongViecs().size()) {
            ArrayList<String> strings = myGanttChartObjects.get(0).isTitle() ? myGanttChartObjects.get(0).getListTrangThai() : (myGanttChartObjects.get(myGanttChartObjects.size() - 1).isTitle() ? myGanttChartObjects.get(myGanttChartObjects.size() - 1).getListTrangThai() : new ArrayList<>());
            ArrayList<String> listTime = new ArrayList<>(strings);
            for (int i1 = 0; i1 < listTime.size(); i1++) {
                listTime.set(i1, "");
            }
            int i1 = 0, i2 = 0;
            for (CongViec congViec : myGanttChartObject.getCongViecs()) {
                if (null != congViec.getGioBD() && -1 != strings.indexOf((4 == (congViec.getGioBD().getHourOfDay() + ":00").length()) ? ("0" + congViec.getGioBD().getHourOfDay() + ":00") : (congViec.getGioBD().getHourOfDay() + ":00"))) {
                    i1 = strings.indexOf((4 == (congViec.getGioBD().getHourOfDay() + ":00").length()) ? ("0" + congViec.getGioBD().getHourOfDay() + ":00") : (congViec.getGioBD().getHourOfDay() + ":00"));
                    if (i1 < i2) {
                        continue;
                    }
                    listTime.set(i1, (listTime.get(i1) + "[" + congViec.getGioBD().toString("HH:mm")));
                }
                if (null != congViec.getGioKT() && -1 != strings.indexOf((4 == (congViec.getGioKT().getHourOfDay() + ":00").length()) ? ("0" + congViec.getGioKT().getHourOfDay() + ":00") : (congViec.getGioKT().getHourOfDay() + ":00"))) {
                    i2 = strings.indexOf((4 == (congViec.getGioKT().getHourOfDay() + ":00").length()) ? ("0" + congViec.getGioKT().getHourOfDay() + ":00") : (congViec.getGioKT().getHourOfDay() + ":00"));
                    listTime.set(i2, (listTime.get(i2) + congViec.getGioKT().toString("HH:mm") + "]"));
                }
                if ((i2 - i1) > 2 || (null == congViec.getGioKT() && i1 < strings.size() - 2)) {
                    int a = ((null == congViec.getGioKT() ? strings.size() : i2) - i1) / 2 + i1;
                    listTime.set(a, congViec.getHoTenNV().substring(0, congViec.getHoTenNV().length() / 2) + "@");
                    listTime.set(a + 1, "@" + congViec.getHoTenNV().substring(congViec.getHoTenNV().length() / 2));
                } else if ((i2 - i1) > 1 || (null == congViec.getGioKT() && i1 < strings.size() - 1)) {
                    listTime.set(i1 + 1, congViec.getHoTenNV());
                }
            }
            holder.recyclerViewTime.setVisibility(View.GONE);
            if (isShow(listTime,hour)) {
                holder.recyclerViewTime.setVisibility(View.VISIBLE);
                MyCellViewGanttChartAdapter adapterTime = new MyCellViewGanttChartAdapter(listTime, convertView.getContext(), lengthMax, true, frame, hour);
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(convertView.getContext(), hour);
                holder.recyclerViewTime.setLayoutManager(layoutManager);
                holder.recyclerViewTime.setAdapter(adapterTime);
            }
        }else {
            holder.recyclerViewTime.setVisibility(View.GONE);
        }
        if (!myGanttChartObject.isTitle()&&myGanttChartObject.getListTrangThai().size()<(lengthMax-frame*hour)){
            ArrayList<String> list = new ArrayList<>();
            list.add( myGanttChartObject.getListTrangThai().get( myGanttChartObject.getListTrangThai().size()-1));
            adapter = new MyCellViewGanttChartAdapter(list, convertView.getContext(), 1, false,frame,hour);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(convertView.getContext(), 1);
            holder.recyclerView.setLayoutManager(layoutManager);
        } else if (myGanttChartObject.isTitle()) {
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(convertView.getContext(), myGanttChartObject.getListTrangThai().size()<hour?myGanttChartObject.getListTrangThai().size():hour);
            holder.recyclerView.setLayoutManager(layoutManager);
        } else {
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(convertView.getContext(), hour*frame);
            holder.recyclerView.setLayoutManager(layoutManager);
        }
        holder.recyclerView.setAdapter(adapter);
        return convertView;
    }

    private boolean isNumber(String a){
        try {
            Integer.parseInt(a);
            return true;
        }catch (Exception exception){
            return false;
        }
    }

    private boolean isShow(ArrayList<String> strings,int hour){
        for (String string : strings.subList(strings.size()-hour,strings.size())){
            if (!string.equals("")){
                return true;
            }
        }
        return false;
    }

    class ViewHolder{
        TextView textView;
        RecyclerView recyclerView,recyclerViewTime;
    }

}
