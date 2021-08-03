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
    builder.EntitySet<ChiTiet>("ChiTiets");
    builder.EntitySet<CongViec>("CongViecs"); 
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class ChiTietsController : ODataController
    {
        private OneDuyKhanh4Entities db = new OneDuyKhanh4Entities();

        // GET: odata/ChiTiets
        [EnableQuery]
        public IQueryable<ChiTiet> GetChiTiets()
        {
            return db.ChiTiets;
        }

        // GET: odata/ChiTiets(5)
        [EnableQuery]
        public SingleResult<ChiTiet> GetChiTiet([FromODataUri] int key)
        {
            return SingleResult.Create(db.ChiTiets.Where(chiTiet => chiTiet.Id == key));
        }

        // PUT: odata/ChiTiets(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<ChiTiet> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            ChiTiet chiTiet = db.ChiTiets.Find(key);
            if (chiTiet == null)
            {
                return NotFound();
            }

            patch.Put(chiTiet);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!ChiTietExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(chiTiet);
        }

        // POST: odata/ChiTiets
        public IHttpActionResult Post(ChiTiet chiTiet)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.ChiTiets.Add(chiTiet);
            db.SaveChanges();

            return Created(chiTiet);
        }

        // PATCH: odata/ChiTiets(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<ChiTiet> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            ChiTiet chiTiet = db.ChiTiets.Find(key);
            if (chiTiet == null)
            {
                return NotFound();
            }

            patch.Patch(chiTiet);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!ChiTietExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(chiTiet);
        }

        // DELETE: odata/ChiTiets(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            ChiTiet chiTiet = db.ChiTiets.Find(key);
            if (chiTiet == null)
            {
                return NotFound();
            }

            db.ChiTiets.Remove(chiTiet);
            db.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        // GET: odata/ChiTiets(5)/CongViecs
        [EnableQuery]
        public IQueryable<CongViec> GetCongViecs([FromODataUri] int key)
        {
            return db.ChiTiets.Where(m => m.Id == key).SelectMany(m => m.CongViecs);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool ChiTietExists(int key)
        {
            return db.ChiTiets.Count(e => e.Id == key) > 0;
        }
    }
}
