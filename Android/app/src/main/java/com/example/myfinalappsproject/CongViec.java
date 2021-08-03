package com.example.myfinalappsproject;

import org.joda.time.LocalDateTime;

public class CongViec implements Comparable<CongViec>{
    private int Id;
    private String HoTenNV;
    private LocalDateTime GioBD;
    private LocalDateTime GioKT;

    public CongViec(int id, String hoTenNV, LocalDateTime gioBD, LocalDateTime gioKT) {
        Id = id;
        HoTenNV = hoTenNV;
        GioBD = gioBD;
        GioKT = gioKT;
    }

    public CongViec(int id, LocalDateTime gioBD, LocalDateTime gioKT) {
        Id = id;
        GioBD = gioBD;
        GioKT = gioKT;
    }

    public String getHoTenNV() {
        return HoTenNV;
    }

    public void setHoTenNV(String hoTenNV) {
        HoTenNV = hoTenNV;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public LocalDateTime getGioBD() {
        return GioBD;
    }

    public void setGioBD(LocalDateTime gioBD) {
        GioBD = gioBD;
    }

    public LocalDateTime getGioKT() {
        return GioKT;
    }

    public void setGioKT(LocalDateTime gioKT) {
        GioKT = gioKT;
    }

    @Override
    public int compareTo(CongViec congViec) {
        return (this.getGioBD().compareTo(congViec.getGioBD()));
    }
}
