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
    builder.EntitySet<NhomMay>("NhomMays");
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class NhomMaysController : ODataController
    {
        private OneDuyKhanh4Entities db = new OneDuyKhanh4Entities();

        // GET: odata/NhomMays
        [EnableQuery]
        public IQueryable<NhomMay> GetNhomMays()
        {
            return db.NhomMays;
        }

        // GET: odata/NhomMays(5)
        [EnableQuery]
        public SingleResult<NhomMay> GetNhomMay([FromODataUri] int key)
        {
            return SingleResult.Create(db.NhomMays.Where(nhomMay => nhomMay.Id == key));
        }

        // PUT: odata/NhomMays(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<NhomMay> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            NhomMay nhomMay = db.NhomMays.Find(key);
            if (nhomMay == null)
            {
                return NotFound();
            }

            patch.Put(nhomMay);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!NhomMayExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(nhomMay);
        }

        // POST: odata/NhomMays
        public IHttpActionResult Post(NhomMay nhomMay)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.NhomMays.Add(nhomMay);
            db.SaveChanges();

            return Created(nhomMay);
        }

        // PATCH: odata/NhomMays(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<NhomMay> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            NhomMay nhomMay = db.NhomMays.Find(key);
            if (nhomMay == null)
            {
                return NotFound();
            }

            patch.Patch(nhomMay);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!NhomMayExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(nhomMay);
        }

        // DELETE: odata/NhomMays(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            NhomMay nhomMay = db.NhomMays.Find(key);
            if (nhomMay == null)
            {
                return NotFound();
            }

            db.NhomMays.Remove(nhomMay);
            db.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool NhomMayExists(int key)
        {
            return db.NhomMays.Count(e => e.Id == key) > 0;
        }
    }
}
