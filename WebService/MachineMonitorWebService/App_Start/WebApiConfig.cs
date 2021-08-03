using System;
using System.Collections.Generic;
using System.Linq;
using System.Web.Http;
using OneDuyKhanhDataAccess;
using System.Web.Http.OData.Builder;
using System.Web.Http.OData.Extensions;
using Microsoft.Data.Edm;
using Microsoft.Data.Edm.Csdl;

namespace MachineMonitorWebService
{
    public static class WebApiConfig
    {
        public static void Register(HttpConfiguration config)
        {
            ODataModelBuilder builder = new ODataConventionModelBuilder();

            builder.EntitySet<BoGiamSat>("BoGiamSats");
            builder.EntitySet<CongViec>("CongViecs");
            builder.EntitySet<LichSuMay>("LichSuMays");
            builder.EntitySet<May>("Mays");
            builder.EntitySet<NhanVien>("NhanViens");
            builder.EntitySet<SuaChua>("SuaChuas");
            builder.EntitySet<ThoiGianMay>("ThoiGianMays");
            builder.EntitySet<TinhTrangMay>("TinhTrangMays");
            builder.EntitySet<ThoiGianMay_Thang>("ThoiGianMay_Thang");
            builder.EntitySet<ChiTiet>("ChiTiets");
            builder.EntitySet<NhanXet_CanhBao>("NhanXet_CanhBao");
            builder.EntitySet<NhomMay>("NhomMays");
            builder.EntitySet<ToSX>("ToSXes");

            Version odataVersion2 = new Version(2, 0);
            Version odataVersion3 = new Version(3, 0);
            builder.DataServiceVersion = odataVersion2;
            builder.MaxDataServiceVersion = odataVersion3;

            IEdmModel edmModel = builder.GetEdmModel();
            edmModel.SetEdmVersion(odataVersion2);
            edmModel.SetEdmxVersion(odataVersion2);


            config.MapHttpAttributeRoutes();

            config.Routes.MapODataServiceRoute("odata", null, edmModel);

            config.Routes.MapHttpRoute(
                name: "DefaultApi",
                routeTemplate: "api/{controller}/{id}",
                defaults: new { id = RouteParameter.Optional }
            );
        }
    }
}
