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
    builder.EntitySet<BoGiamSat>("BoGiamSats");
    builder.EntitySet<May>("Mays"); 
    builder.EntitySet<ThoiGianMay>("ThoiGianMays"); 
    builder.EntitySet<TinhTrangMay>("TinhTrangMays"); 
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class BoGiamSatsController : ODataController
    {
        private OneDuyKhanh4Entities db = new OneDuyKhanh4Entities();

        // GET: odata/BoGiamSats
        [EnableQuery]
        public IQueryable<BoGiamSat> GetBoGiamSats()
        {
            return db.BoGiamSats;
        }

        // GET: odata/BoGiamSats(5)
        [EnableQuery]
        public SingleResult<BoGiamSat> GetBoGiamSat([FromODataUri] int key)
        {
            return SingleResult.Create(db.BoGiamSats.Where(boGiamSat => boGiamSat.Id == key));
        }

        // PUT: odata/BoGiamSats(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<BoGiamSat> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            BoGiamSat boGiamSat = db.BoGiamSats.Find(key);
            if (boGiamSat == null)
            {
                return NotFound();
            }

            patch.Put(boGiamSat);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!BoGiamSatExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(boGiamSat);
        }

        // POST: odata/BoGiamSats
        public IHttpActionResult Post(BoGiamSat boGiamSat)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.BoGiamSats.Add(boGiamSat);
            db.SaveChanges();

            return Created(boGiamSat);
        }

        // PATCH: odata/BoGiamSats(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<BoGiamSat> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            BoGiamSat boGiamSat = db.BoGiamSats.Find(key);
            if (boGiamSat == null)
            {
                return NotFound();
            }

            patch.Patch(boGiamSat);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!BoGiamSatExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(boGiamSat);
        }

        // DELETE: odata/BoGiamSats(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            BoGiamSat boGiamSat = db.BoGiamSats.Find(key);
            if (boGiamSat == null)
            {
                return NotFound();
            }

            db.BoGiamSats.Remove(boGiamSat);
            db.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        // GET: odata/BoGiamSats(5)/May
        [EnableQuery]
        public SingleResult<May> GetMay([FromODataUri] int key)
        {
            return SingleResult.Create(db.BoGiamSats.Where(m => m.Id == key).Select(m => m.May));
        }

        // GET: odata/BoGiamSats(5)/ThoiGianMays
        [EnableQuery]
        public IQueryable<ThoiGianMay> GetThoiGianMays([FromODataUri] int key)
        {
            return db.BoGiamSats.Where(m => m.Id == key).SelectMany(m => m.ThoiGianMays);
        }

        // GET: odata/BoGiamSats(5)/TinhTrangMays
        [EnableQuery]
        public IQueryable<TinhTrangMay> GetTinhTrangMays([FromODataUri] int key)
        {
            return db.BoGiamSats.Where(m => m.Id == key).SelectMany(m => m.TinhTrangMays);
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool BoGiamSatExists(int key)
        {
            return db.BoGiamSats.Count(e => e.Id == key) > 0;
        }
    }
}
