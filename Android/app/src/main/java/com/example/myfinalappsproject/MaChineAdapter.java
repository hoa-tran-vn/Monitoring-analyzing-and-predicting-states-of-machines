package com.example.myfinalappsproject;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class MaChineAdapter extends ArrayAdapter {
    Activity activity;
    ArrayList<MachineObject> arrayList;
    ArrayList<MachineObject> tempMachineObject;

    public MaChineAdapter(Activity activity, ArrayList<MachineObject> arrayList) {
        super(activity, R.layout.item_detail,arrayList);
        this.activity = activity;
        this.arrayList = arrayList;
    }

    public String LoadListString(int SoLuongMayHienThi){
        if (null==tempMachineObject){
            tempMachineObject = new ArrayList<>(arrayList);
        }
        String list = "";
        int i = 0 ;
        for (MachineObject machineObject:tempMachineObject){
//            if (i>=SoLuongMayHienThi){
//                break;
//            }
            if (machineObject.isChecked()){
                i++;
                if (list.equals("")){
                    list = machineObject.getId()+":"+machineObject.getName();
                }else {
                    list +="-"+ machineObject.getId()+":"+machineObject.getName();
                }
            }
        }
        return list;
    }

    @Override
    public int getCount() {
        return arrayList.size()+1;
    }

    @Override
    public MachineObject getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder                    = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
            convertView                   = layoutInflater.inflate(R.layout.item_detail,null);
            viewHolder.checkBox           = convertView.findViewById(R.id.checkbox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (0== position){
            viewHolder.checkBox.setText("Tất cả máy");
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    for (MachineObject machineObject:arrayList){
                        machineObject.setChecked(b);
                    }
                    notifyDataSetChanged();
                }
            });
        }else {
            MachineObject machineObject = arrayList.get(position - 1);
            viewHolder.checkBox.setText(machineObject.getName());
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    machineObject.setChecked(b);
                    notifyDataSetChanged();
                }
            });
            viewHolder.checkBox.setChecked(machineObject.isChecked());
        }
        if (position % 2 == 1) {
            convertView.setBackgroundColor(Color.LTGRAY);
        } else {
            convertView.setBackgroundColor(Color.WHITE);
        }
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {
                arrayList = (ArrayList<MachineObject>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<MachineObject> FilteredArrList = new ArrayList<>();
                if (tempMachineObject == null || 0 == tempMachineObject.size()) {
                    tempMachineObject = new ArrayList<MachineObject>(arrayList);
                }

                if (constraint == null || constraint.length() == 0) {
                    results.count = tempMachineObject.size();
                    results.values = tempMachineObject;
                } else {
                    String[] strings = constraint.toString().split("-");
                    String may = "",id_to="";
                    try{
                        may = strings[0];
                    }catch (Exception e){}
                    try{
                        id_to = strings[1];
                    }catch (Exception e){}
                    for (MachineObject machineObject : tempMachineObject) {
                        if ((may.isEmpty() || machineObject.getName().toLowerCase().startsWith(may)) && (id_to.isEmpty() || (machineObject.getId_To() + "").equals(id_to))) {
                            FilteredArrList.add(machineObject);
                        }
                    }
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }

    private class ViewHolder{
        CheckBox checkBox;
    }
}
