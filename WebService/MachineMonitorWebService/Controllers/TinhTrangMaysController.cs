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
    builder.EntitySet<TinhTrangMay>("TinhTrangMays");
    builder.EntitySet<BoGiamSat>("BoGiamSats"); 
    builder.EntitySet<May>("Mays"); 
    builder.EntitySet<ThoiGianMay>("ThoiGianMays"); 
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class TinhTrangMaysController : ODataController
    {
        private OneDuyKhanh4Entities db = new OneDuyKhanh4Entities();

        // GET: odata/TinhTrangMays
        [EnableQuery]
        public IQueryable<TinhTrangMay> GetTinhTrangMays()
        {
            return db.TinhTrangMays;
        }

        // GET: odata/TinhTrangMays(5)
        [EnableQuery]
        public SingleResult<TinhTrangMay> GetTinhTrangMay([FromODataUri] int key)
        {
            return SingleResult.Create(db.TinhTrangMays.Where(tinhTrangMay => tinhTrangMay.Id == key));
        }

        // PUT: odata/TinhTrangMays(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<TinhTrangMay> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            TinhTrangMay tinhTrangMay = db.TinhTrangMays.Find(key);
            if (tinhTrangMay == null)
            {
                return NotFound();
            }

            patch.Put(tinhTrangMay);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!TinhTrangMayExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(tinhTrangMay);
        }

        // POST: odata/TinhTrangMays
        public IHttpActionResult Post(TinhTrangMay tinhTrangMay)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.TinhTrangMays.Add(tinhTrangMay);
            db.SaveChanges();

            return Created(tinhTrangMay);
        }

        // PATCH: odata/TinhTrangMays(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<TinhTrangMay> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            TinhTrangMay tinhTrangMay = db.TinhTrangMays.Find(key);
            if (tinhTrangMay == null)
            {
                return NotFound();
            }

            patch.Patch(tinhTrangMay);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!TinhTrangMayExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(tinhTrangMay);
        }

        // DELETE: odata/TinhTrangMays(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            TinhTrangMay tinhTrangMay = db.TinhTrangMays.Find(key);
            if (tinhTrangMay == null)
            {
                return NotFound();
            }

            db.TinhTrangMays.Remove(tinhTrangMay);
            db.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        // GET: odata/TinhTrangMays(5)/BoGiamSat1
        [EnableQuery]
        public SingleResult<BoGiamSat> GetBoGiamSat1([FromODataUri] int key)
        {
            return SingleResult.Create(db.TinhTrangMays.Where(m => m.Id == key).Select(m => m.BoGiamSat1));
        }

        // GET: odata/TinhTrangMays(5)/May1
        [EnableQuery]
        public SingleResult<May> GetMay1([FromODataUri] int key)
        {
            return SingleResult.Create(db.TinhTrangMays.Where(m => m.Id == key).Select(m => m.May1));
        }

        // GET: odata/TinhTrangMays(5)/ThoiGianMay1
        [EnableQuery]
        public SingleResult<ThoiGianMay> GetThoiGianMay1([FromODataUri] int key)
        {
            return SingleResult.Create(db.TinhTrangMays.Where(m => m.Id == key).Select(m => m.ThoiGianMay1));
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool TinhTrangMayExists(int key)
        {
            return db.TinhTrangMays.Count(e => e.Id == key) > 0;
        }
    }
}
