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
    
    public partial class ToSX
    {
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2214:DoNotCallOverridableMethodsInConstructors")]
        public ToSX()
        {
            this.Mays = new HashSet<May>();
            this.NhanViens = new HashSet<NhanVien>();
            this.NhomMays = new HashSet<NhomMay>();
        }
    
        public int Id { get; set; }
        public string Ten { get; set; }
        public bool GiaCongNgoai { get; set; }
        public string GhiChu { get; set; }
        public string ToTruong { get; set; }
        public byte[] RowVersion { get; set; }
        public Nullable<int> ToSX_TroLiSX { get; set; }
        public Nullable<int> ToSX_NguoiDung { get; set; }
        public Nullable<int> ToSX_NguoiDung1 { get; set; }
    
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2227:CollectionPropertiesShouldBeReadOnly")]
        public virtual ICollection<May> Mays { get; set; }
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2227:CollectionPropertiesShouldBeReadOnly")]
        public virtual ICollection<NhanVien> NhanViens { get; set; }
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2227:CollectionPropertiesShouldBeReadOnly")]
        public virtual ICollection<NhomMay> NhomMays { get; set; }
    }
}
