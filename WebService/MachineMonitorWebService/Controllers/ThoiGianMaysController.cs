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
    builder.EntitySet<ThoiGianMay>("ThoiGianMays");
    builder.EntitySet<BoGiamSat>("BoGiamSats"); 
    builder.EntitySet<May>("Mays"); 
    builder.EntitySet<TinhTrangMay>("TinhTrangMays"); 
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class ThoiGianMaysController : ODataController
    {
        private OneDuyKhanh4Entities db = new OneDuyKhanh4Entities();

        // GET: odata/ThoiGianMays
        [EnableQuery]
        public IQueryable<ThoiGianMay> GetThoiGianMays()
        {
            return db.ThoiGianMays;
        }

        // GET: odata/ThoiGianMays(5)
        [EnableQuery]
        public SingleResult<ThoiGianMay> GetThoiGianMay([FromODataUri] int key)
        {
            return SingleResult.Create(db.ThoiGianMays.Where(thoiGianMay => thoiGianMay.Id == key));
        }

        // PUT: odata/ThoiGianMays(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<ThoiGianMay> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            ThoiGianMay thoiGianMay = db.ThoiGianMays.Find(key);
            if (thoiGianMay == null)
            {
                return NotFound();
            }

            patch.Put(thoiGianMay);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!ThoiGianMayExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(thoiGianMay);
        }

        // POST: odata/ThoiGianMays
        public IHttpActionResult Post(ThoiGianMay thoiGianMay)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.ThoiGianMays.Add(thoiGianMay);
            db.SaveChanges();

            return Created(thoiGianMay);
        }

        // PATCH: odata/ThoiGianMays(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<ThoiGianMay> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            ThoiGianMay thoiGianMay = db.ThoiGianMays.Find(key);
            if (thoiGianMay == null)
            {
                return NotFound();
            }

            patch.Patch(thoiGianMay);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!ThoiGianMayExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(thoiGianMay);
        }

        // DELETE: odata/ThoiGianMays(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            ThoiGianMay thoiGianMay = db.ThoiGianMays.Find(key);
            if (thoiGianMay == null)
            {
                return NotFound();
            }

            db.ThoiGianMays.Remove(thoiGianMay);
            db.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        // GET: odata/ThoiGianMays(5)/BoGiamSat1
        [EnableQuery]
        public SingleResult<BoGiamSat> GetBoGiamSat1([FromODataUri] int key)
        {
            return SingleResult.Create(db.ThoiGianMays.Where(m => m.Id == key).Select(m => m.BoGiamSat1));
        }

        // GET: odata/ThoiGianMays(5)/May1
        [EnableQuery]
        public SingleResult<May> GetMay1([FromODataUri] int key)
        {
            return SingleResult.Create(db.ThoiGianMays.Where(m => m.Id == key).Select(m => m.May1));
        }

        // GET: odata/ThoiGianMays(5)/TinhTrangMays
        [EnableQuery]
        public IQueryable<TinhTrangMay> GetTinhTrangMays([FromODataUri] int key)
        {
            return db.ThoiGianMays.Where(m => m.Id == key).SelectMany(m => m.TinhTrangMays);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool ThoiGianMayExists(int key)
        {
            return db.ThoiGianMays.Count(e => e.Id == key) > 0;
        }
    }
}
