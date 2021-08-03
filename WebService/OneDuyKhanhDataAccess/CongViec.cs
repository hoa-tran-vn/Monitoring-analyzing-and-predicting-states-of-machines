//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated from a template.
//
//     Manual changes to this file may cause unexpected behavior in your application.
//     Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace OneDuyKhanhDataAccess
{
    using System;
    using System.Collections.Generic;
    
    public partial class CongViec
    {
        public int Id { get; set; }
        public string MaChiTiet { get; set; }
        public string May { get; set; }
        public Nullable<System.DateTime> GioBD { get; set; }
        public Nullable<System.DateTime> GioKT { get; set; }
        public string GhiChu { get; set; }
        public Nullable<System.DateTime> GioBD_BS { get; set; }
        public Nullable<System.DateTime> Duyet { get; set; }
        public Nullable<System.DateTime> GioKT_BS { get; set; }
        public string ThoiLuongBS { get; set; }
        public string Buocs { get; set; }
        public byte[] RowVersion { get; set; }
        public Nullable<int> CongViec_BuocHL { get; set; }
        public Nullable<int> CongViec_ChiTietHL2 { get; set; }
        public Nullable<int> CongViec_ChiTiet2 { get; set; }
        public Nullable<int> CongViec_CongViecPhu { get; set; }
        public Nullable<int> CongViec_May1 { get; set; }
        public Nullable<int> CongViec_NguyenCongHL1 { get; set; }
        public int CongViec_NhanVien { get; set; }
        public Nullable<int> CongViec_NguyenCong { get; set; }
        public Nullable<int> NguyenCongLoSXHL { get; set; }
        public string NoiDung { get; set; }
        public Nullable<int> LenhSanXuat { get; set; }
        public Nullable<System.DateTime> ServerTime { get; set; }
        public Nullable<int> ChonNoiDung { get; set; }
        public Nullable<int> IdChiTiet { get; set; }
        public Nullable<System.DateTime> GioSetupXong { get; set; }
        public Nullable<System.DateTime> GioBDChayMay { get; set; }
        public Nullable<System.DateTime> GioTamDungLan1 { get; set; }
        public Nullable<System.DateTime> GioBatDauLai1 { get; set; }
        public Nullable<System.DateTime> GioTamDungLan2 { get; set; }
        public Nullable<System.DateTime> GioBatDauLai2 { get; set; }
        public Nullable<int> ThoiGianNhanCong { get; set; }
        public Nullable<int> SLDoDang { get; set; }
        public string DoiTuongLamViec { get; set; }
    
        public virtual ChiTiet ChiTiet { get; set; }
        public virtual May May1 { get; set; }
        public virtual NhanVien NhanVien { get; set; }
    }
}
