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
    builder.EntitySet<SuaChua>("SuaChuas");
    builder.EntitySet<May>("Mays"); 
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class SuaChuasController : ODataController
    {
        private OneDuyKhanh4Entities db = new OneDuyKhanh4Entities();

        // GET: odata/SuaChuas
        [EnableQuery]
        public IQueryable<SuaChua> GetSuaChuas()
        {
            return db.SuaChuas;
        }

        // GET: odata/SuaChuas(5)
        [EnableQuery]
        public SingleResult<SuaChua> GetSuaChua([FromODataUri] int key)
        {
            return SingleResult.Create(db.SuaChuas.Where(suaChua => suaChua.Id == key));
        }

        // PUT: odata/SuaChuas(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<SuaChua> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            SuaChua suaChua = db.SuaChuas.Find(key);
            if (suaChua == null)
            {
                return NotFound();
            }

            patch.Put(suaChua);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!SuaChuaExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(suaChua);
        }

        // POST: odata/SuaChuas
        public IHttpActionResult Post(SuaChua suaChua)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.SuaChuas.Add(suaChua);
            db.SaveChanges();

            return Created(suaChua);
        }

        // PATCH: odata/SuaChuas(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<SuaChua> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            SuaChua suaChua = db.SuaChuas.Find(key);
            if (suaChua == null)
            {
                return NotFound();
            }

            patch.Patch(suaChua);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!SuaChuaExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(suaChua);
        }

        // DELETE: odata/SuaChuas(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            SuaChua suaChua = db.SuaChuas.Find(key);
            if (suaChua == null)
            {
                return NotFound();
            }

            db.SuaChuas.Remove(suaChua);
            db.SaveChanges();

            return StatusCode(HttpStatusCode.NoContent);
        }

        // GET: odata/SuaChuas(5)/May1
        [EnableQuery]
        public SingleResult<May> GetMay1([FromODataUri] int key)
        {
            return SingleResult.Create(db.SuaChuas.Where(m => m.Id == key).Select(m => m.May1));
        }

        protected override void Dispose(bool disposing)
        {
            if (disposing)
            {
                db.Dispose();
            }
            base.Dispose(disposing);
        }

        private bool SuaChuaExists(int key)
        {
            return db.SuaChuas.Count(e => e.Id == key) > 0;
        }
    }
}
