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
    builder.EntitySet<ToSX>("ToSXes");
    builder.EntitySet<May>("Mays"); 
    builder.EntitySet<NhanVien>("NhanViens"); 
    builder.EntitySet<NhomMay>("NhomMays"); 
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class ToSXesController : ODataController
    {
        private OneDuyKhanh4Entities db = new OneDuyKhanh4Entities();

        // GET: odata/ToSXes
        [EnableQuery]
        public IQueryable<ToSX> GetToSXes()
        {
            return db.ToSXes;
        }

        // GET: odata/ToSXes(5)
        [EnableQuery]
        public SingleResult<ToSX> GetToSX([FromODataUri] int key)
        {
            return SingleResult.Create(db.ToSXes.Where(toSX => toSX.Id == key));
        }

        // PUT: odata/ToSXes(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<ToSX> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            ToSX toSX = db.ToSXes.Find(key);
            if (toSX == null)
            {
                return NotFound();
            }

            patch.Put(toSX);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!ToSXExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(toSX);
        }

        // POST: odata/ToSXes
        public IHttpActionResult Post(ToSX toSX)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.ToSXes.Add(toSX);
            db.SaveChanges();

            return Created(toSX);
        }

        // PATCH: odata/ToSXes(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<ToSX> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            ToSX toSX = db.ToSXes.Find(key);
            if (toSX == null)
            {
                return NotFound();
            }

            patch.Patch(toSX);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!ToSXExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(toSX);
        }

        // DELETE: odata/ToSXes(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            ToSX toSX = db.ToSXes.Find(key);
            if (toSX == null)
            {
                return NotFound();
            }

            db.ToSXes.Remove(toSX);
            db.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        // GET: odata/ToSXes(5)/Mays
        [EnableQuery]
        public IQueryable<May> GetMays([FromODataUri] int key)
        {
            return db.ToSXes.Where(m => m.Id == key).SelectMany(m => m.Mays);
        }

        // GET: odata/ToSXes(5)/NhanViens
        [EnableQuery]
        public IQueryable<NhanVien> GetNhanViens([FromODataUri] int key)
        {
            return db.ToSXes.Where(m => m.Id == key).SelectMany(m => m.NhanViens);
        }

        // GET: odata/ToSXes(5)/NhomMays
        [EnableQuery]
        public IQueryable<NhomMay> GetNhomMays([FromODataUri] int key)
        {
            return db.ToSXes.Where(m => m.Id == key).SelectMany(m => m.NhomMays);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool ToSXExists(int key)
        {
            return db.ToSXes.Count(e => e.Id == key) > 0;
        }
    }
}
