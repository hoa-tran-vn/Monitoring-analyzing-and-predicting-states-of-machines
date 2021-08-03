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
    builder.EntitySet<CongViec>("CongViecs");
    builder.EntitySet<May>("Mays"); 
    builder.EntitySet<NhanVien>("NhanViens"); 
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class CongViecsController : ODataController
    {
        private OneDuyKhanh4Entities db = new OneDuyKhanh4Entities();

        // GET: odata/CongViecs
        [EnableQuery]
        public IQueryable<CongViec> GetCongViecs()
        {
            return db.CongViecs;
        }

        // GET: odata/CongViecs(5)
        [EnableQuery]
        public SingleResult<CongViec> GetCongViec([FromODataUri] int key)
        {
            return SingleResult.Create(db.CongViecs.Where(congViec => congViec.Id == key));
        }

        // PUT: odata/CongViecs(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<CongViec> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            CongViec congViec = db.CongViecs.Find(key);
            if (congViec == null)
            {
                return NotFound();
            }

            patch.Put(congViec);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!CongViecExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(congViec);
        }

        // POST: odata/CongViecs
        public IHttpActionResult Post(CongViec congViec)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.CongViecs.Add(congViec);
            db.SaveChanges();

            return Created(congViec);
        }

        // PATCH: odata/CongViecs(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<CongViec> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            CongViec congViec = db.CongViecs.Find(key);
            if (congViec == null)
            {
                return NotFound();
            }

            patch.Patch(congViec);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!CongViecExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(congViec);
        }

        // DELETE: odata/CongViecs(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            CongViec congViec = db.CongViecs.Find(key);
            if (congViec == null)
            {
                return NotFound();
            }

            db.CongViecs.Remove(congViec);
            db.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        // GET: odata/CongViecs(5)/May1
        [EnableQuery]
        public SingleResult<May> GetMay1([FromODataUri] int key)
        {
            return SingleResult.Create(db.CongViecs.Where(m => m.Id == key).Select(m => m.May1));
        }

        // GET: odata/CongViecs(5)/NhanVien
        [EnableQuery]
        public SingleResult<NhanVien> GetNhanVien([FromODataUri] int key)
        {
            return SingleResult.Create(db.CongViecs.Where(m => m.Id == key).Select(m => m.NhanVien));
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool CongViecExists(int key)
        {
            return db.CongViecs.Count(e => e.Id == key) > 0;
        }
    }
}
