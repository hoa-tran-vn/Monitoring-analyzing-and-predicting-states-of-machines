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
    builder.EntitySet<NhanVien>("NhanViens");
    builder.EntitySet<CongViec>("CongViecs"); 
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class NhanViensController : ODataController
    {
        private OneDuyKhanh4Entities db = new OneDuyKhanh4Entities();

        // GET: odata/NhanViens
        [EnableQuery]
        public IQueryable<NhanVien> GetNhanViens()
        {
            return db.NhanViens;
        }

        // GET: odata/NhanViens(5)
        [EnableQuery]
        public SingleResult<NhanVien> GetNhanVien([FromODataUri] int key)
        {
            return SingleResult.Create(db.NhanViens.Where(nhanVien => nhanVien.Id == key));
        }

        // PUT: odata/NhanViens(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<NhanVien> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            NhanVien nhanVien = db.NhanViens.Find(key);
            if (nhanVien == null)
            {
                return NotFound();
            }

            patch.Put(nhanVien);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!NhanVienExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(nhanVien);
        }

        // POST: odata/NhanViens
        public IHttpActionResult Post(NhanVien nhanVien)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.NhanViens.Add(nhanVien);
            db.SaveChanges();

            return Created(nhanVien);
        }

        // PATCH: odata/NhanViens(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<NhanVien> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            NhanVien nhanVien = db.NhanViens.Find(key);
            if (nhanVien == null)
            {
                return NotFound();
            }

            patch.Patch(nhanVien);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!NhanVienExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(nhanVien);
        }

        // DELETE: odata/NhanViens(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            NhanVien nhanVien = db.NhanViens.Find(key);
            if (nhanVien == null)
            {
                return NotFound();
            }

            db.NhanViens.Remove(nhanVien);
            db.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        // GET: odata/NhanViens(5)/CongViecs
        [EnableQuery]
        public IQueryable<CongViec> GetCongViecs([FromODataUri] int key)
        {
            return db.NhanViens.Where(m => m.Id == key).SelectMany(m => m.CongViecs);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool NhanVienExists(int key)
        {
            return db.NhanViens.Count(e => e.Id == key) > 0;
        }
    }
}
