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
    
    public partial class BoGiamSat
    {
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2214:DoNotCallOverridableMethodsInConstructors")]
        public BoGiamSat()
        {
            this.ThoiGianMays = new HashSet<ThoiGianMay>();
            this.TinhTrangMays = new HashSet<TinhTrangMay>();
        }
    
        public int Id { get; set; }
        public string MaBoGiamSat { get; set; }
        public Nullable<System.DateTime> ThoiGianCapNhatCuoiCung { get; set; }
        public Nullable<bool> ketNoi { get; set; }
        public Nullable<int> GiamSatMay { get; set; }
        public byte[] HinhAnh { get; set; }
        public byte[] HinhAnhMay { get; set; }
    
        public virtual May May { get; set; }
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2227:CollectionPropertiesShouldBeReadOnly")]
        public virtual ICollection<ThoiGianMay> ThoiGianMays { get; set; }
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2227:CollectionPropertiesShouldBeReadOnly")]
        public virtual ICollection<TinhTrangMay> TinhTrangMays { get; set; }
    }
}
