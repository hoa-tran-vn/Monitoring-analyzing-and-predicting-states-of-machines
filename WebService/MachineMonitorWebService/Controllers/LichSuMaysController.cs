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
    builder.EntitySet<LichSuMay>("LichSuMays");
    builder.EntitySet<May>("Mays"); 
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class LichSuMaysController : ODataController
    {
        private OneDuyKhanh4Entities db = new OneDuyKhanh4Entities();

        // GET: odata/LichSuMays
        [EnableQuery]
        public IQueryable<LichSuMay> GetLichSuMays()
        {
            return db.LichSuMays;
        }

        // GET: odata/LichSuMays(5)
        [EnableQuery]
        public SingleResult<LichSuMay> GetLichSuMay([FromODataUri] int key)
        {
            return SingleResult.Create(db.LichSuMays.Where(lichSuMay => lichSuMay.Id == key));
        }

        // PUT: odata/LichSuMays(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<LichSuMay> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            LichSuMay lichSuMay = db.LichSuMays.Find(key);
            if (lichSuMay == null)
            {
                return NotFound();
            }

            patch.Put(lichSuMay);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!LichSuMayExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(lichSuMay);
        }

        // POST: odata/LichSuMays
        public IHttpActionResult Post(LichSuMay lichSuMay)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.LichSuMays.Add(lichSuMay);
            db.SaveChanges();

            return Created(lichSuMay);
        }

        // PATCH: odata/LichSuMays(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<LichSuMay> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            LichSuMay lichSuMay = db.LichSuMays.Find(key);
            if (lichSuMay == null)
            {
                return NotFound();
            }

            patch.Patch(lichSuMay);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!LichSuMayExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(lichSuMay);
        }

        // DELETE: odata/LichSuMays(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            LichSuMay lichSuMay = db.LichSuMays.Find(key);
            if (lichSuMay == null)
            {
                return NotFound();
            }

            db.LichSuMays.Remove(lichSuMay);
            db.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        // GET: odata/LichSuMays(5)/May
        [EnableQuery]
        public SingleResult<May> GetMay([FromODataUri] int key)
        {
            return SingleResult.Create(db.LichSuMays.Where(m => m.Id == key).Select(m => m.May));
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool LichSuMayExists(int key)
        {
            return db.LichSuMays.Count(e => e.Id == key) > 0;
        }
    }
}
