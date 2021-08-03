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
    builder.EntitySet<ThoiGianMay_Thang>("ThoiGianMay_Thang");
    builder.EntitySet<May>("Mays"); 
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class ThoiGianMay_ThangController : ODataController
    {
        private OneDuyKhanh4Entities db = new OneDuyKhanh4Entities();

        // GET: odata/ThoiGianMay_Thang
        [EnableQuery]
        public IQueryable<ThoiGianMay_Thang> GetThoiGianMay_Thang()
        {
            return db.ThoiGianMay_Thang;
        }

        // GET: odata/ThoiGianMay_Thang(5)
        [EnableQuery]
        public SingleResult<ThoiGianMay_Thang> GetThoiGianMay_Thang([FromODataUri] int key)
        {
            return SingleResult.Create(db.ThoiGianMay_Thang.Where(thoiGianMay_Thang => thoiGianMay_Thang.Id == key));
        }

        // PUT: odata/ThoiGianMay_Thang(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<ThoiGianMay_Thang> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            ThoiGianMay_Thang thoiGianMay_Thang = db.ThoiGianMay_Thang.Find(key);
            if (thoiGianMay_Thang == null)
            {
                return NotFound();
            }

            patch.Put(thoiGianMay_Thang);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!ThoiGianMay_ThangExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(thoiGianMay_Thang);
        }

        // POST: odata/ThoiGianMay_Thang
        public IHttpActionResult Post(ThoiGianMay_Thang thoiGianMay_Thang)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.ThoiGianMay_Thang.Add(thoiGianMay_Thang);
            db.SaveChanges();

            return Created(thoiGianMay_Thang);
        }

        // PATCH: odata/ThoiGianMay_Thang(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<ThoiGianMay_Thang> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            ThoiGianMay_Thang thoiGianMay_Thang = db.ThoiGianMay_Thang.Find(key);
            if (thoiGianMay_Thang == null)
            {
                return NotFound();
            }

            patch.Patch(thoiGianMay_Thang);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!ThoiGianMay_ThangExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(thoiGianMay_Thang);
        }

        // DELETE: odata/ThoiGianMay_Thang(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            ThoiGianMay_Thang thoiGianMay_Thang = db.ThoiGianMay_Thang.Find(key);
            if (thoiGianMay_Thang == null)
            {
                return NotFound();
            }

            db.ThoiGianMay_Thang.Remove(thoiGianMay_Thang);
            db.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        // GET: odata/ThoiGianMay_Thang(5)/May1
        [EnableQuery]
        public SingleResult<May> GetMay1([FromODataUri] int key)
        {
            return SingleResult.Create(db.ThoiGianMay_Thang.Where(m => m.Id == key).Select(m => m.May1));
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool ThoiGianMay_ThangExists(int key)
        {
            return db.ThoiGianMay_Thang.Count(e => e.Id == key) > 0;
        }
    }
}
