package com.example.myfinalappsproject;

public class MachineObject {
    private int Id;
    private String Name;
    private int Id_To;
    private String TenToSX;
    private boolean isChecked;

    public MachineObject(int id, String name, int id_To, boolean isChecked) {
        Id = id;
        Name = name;
        Id_To = id_To;
        this.isChecked = isChecked;
    }

    public String getTenToSX() {
        return TenToSX;
    }

    public void setTenToSX(String tenToSX) {
        TenToSX = tenToSX;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getId_To() {
        return Id_To;
    }

    public void setId_To(int id_To) {
        Id_To = id_To;
    }
}
