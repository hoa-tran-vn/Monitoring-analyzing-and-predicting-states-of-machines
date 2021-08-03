using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Data.Entity.Infrastructure;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.ModelBinding;
using System.Web.Http.OData;
using System.Web.Http.OData.Routing;
using OneDuyKhanhDataAccess;

namespace MachineMonitorWebService.Controllers
{
    /*
    The WebApiConfig class may require additional changes to add a route for this controller. Merge these statements into the Register method of the WebApiConfig class as applicable. Note that OData URLs are case sensitive.

    using System.Web.Http.OData.Builder;
    using System.Web.Http.OData.Extensions;
    using OneDuyKhanhDataAccess;
    ODataConventionModelBuilder builder = new ODataConventionModelBuilder();
    builder.EntitySet<May>("Mays");
    builder.EntitySet<BoGiamSat>("BoGiamSats"); 
    builder.EntitySet<CongViec>("CongViecs"); 
    builder.EntitySet<LichSuMay>("LichSuMays"); 
    builder.EntitySet<ThoiGianMay_Thang>("ThoiGianMay_Thang"); 
    builder.EntitySet<SuaChua>("SuaChuas"); 
    builder.EntitySet<ThoiGianMay>("ThoiGianMays"); 
    builder.EntitySet<TinhTrangMay>("TinhTrangMays"); 
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class MaysController : ODataController
    {
        private OneDuyKhanh4Entities db = new OneDuyKhanh4Entities();

        // GET: odata/Mays
        [EnableQuery]
        public IQueryable<May> GetMays()
        {
            return db.Mays;
        }

        // GET: odata/Mays(5)
        [EnableQuery]
        public SingleResult<May> GetMay([FromODataUri] int key)
        {
            return SingleResult.Create(db.Mays.Where(may => may.Id == key));
        }

        // PUT: odata/Mays(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<May> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            May may = db.Mays.Find(key);
            if (may == null)
            {
                return NotFound();
            }

            patch.Put(may);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!MayExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(may);
        }

        // POST: odata/Mays
        public IHttpActionResult Post(May may)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.Mays.Add(may);
            db.SaveChanges();

            return Created(may);
        }

        // PATCH: odata/Mays(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<May> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            May may = db.Mays.Find(key);
            if (may == null)
            {
                return NotFound();
            }

            patch.Patch(may);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!MayExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(may);
        }

        // DELETE: odata/Mays(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            May may = db.Mays.Find(key);
            if (may == null)
            {
                return NotFound();
            }

            db.Mays.Remove(may);
            db.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        // GET: odata/Mays(5)/BoGiamSats
        [EnableQuery]
        public IQueryable<BoGiamSat> GetBoGiamSats([FromODataUri] int key)
        {
            return db.Mays.Where(m => m.Id == key).SelectMany(m => m.BoGiamSats);
        }

        // GET: odata/Mays(5)/CongViecs
        [EnableQuery]
        public IQueryable<CongViec> GetCongViecs([FromODataUri] int key)
        {
            return db.Mays.Where(m => m.Id == key).SelectMany(m => m.CongViecs);
        }

        // GET: odata/Mays(5)/LichSuMays
        [EnableQuery]
        public IQueryable<LichSuMay> GetLichSuMays([FromODataUri] int key)
        {
            return db.Mays.Where(m => m.Id == key).SelectMany(m => m.LichSuMays);
        }

        // GET: odata/Mays(5)/ThoiGianMay_Thang
        [EnableQuery]
        public IQueryable<ThoiGianMay_Thang> GetThoiGianMay_Thang([FromODataUri] int key)
        {
            return db.Mays.Where(m => m.Id == key).SelectMany(m => m.ThoiGianMay_Thang);
        }

        // GET: odata/Mays(5)/SuaChuas
        [EnableQuery]
        public IQueryable<SuaChua> GetSuaChuas([FromODataUri] int key)
        {
            return db.Mays.Where(m => m.Id == key).SelectMany(m => m.SuaChuas);
        }

        // GET: odata/Mays(5)/ThoiGianMays
        [EnableQuery]
        public IQueryable<ThoiGianMay> GetThoiGianMays([FromODataUri] int key)
        {
            return db.Mays.Where(m => m.Id == key).SelectMany(m => m.ThoiGianMays);
        }

        // GET: odata/Mays(5)/TinhTrangMays
        [EnableQuery]
        public IQueryable<TinhTrangMay> GetTinhTrangMays([FromODataUri] int key)
        {
            return db.Mays.Where(m => m.Id == key).SelectMany(m => m.TinhTrangMays);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool MayExists(int key)
        {
            return db.Mays.Count(e => e.Id == key) > 0;
        }
    }
}
