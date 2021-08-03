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
    builder.EntitySet<NhanXet_CanhBao>("NhanXet_CanhBao");
    config.Routes.MapODataServiceRoute("odata", "odata", builder.GetEdmModel());
    */
    public class NhanXet_CanhBaoController : ODataController
    {
        private OneDuyKhanh4Entities db = new OneDuyKhanh4Entities();

        // GET: odata/NhanXet_CanhBao
        [EnableQuery]
        public IQueryable<NhanXet_CanhBao> GetNhanXet_CanhBao()
        {
            return db.NhanXet_CanhBao;
        }

        // GET: odata/NhanXet_CanhBao(5)
        [EnableQuery]
        public SingleResult<NhanXet_CanhBao> GetNhanXet_CanhBao([FromODataUri] int key)
        {
            return SingleResult.Create(db.NhanXet_CanhBao.Where(nhanXet_CanhBao => nhanXet_CanhBao.Id == key));
        }

        // PUT: odata/NhanXet_CanhBao(5)
        public IHttpActionResult Put([FromODataUri] int key, Delta<NhanXet_CanhBao> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            NhanXet_CanhBao nhanXet_CanhBao = db.NhanXet_CanhBao.Find(key);
            if (nhanXet_CanhBao == null)
            {
                return NotFound();
            }

            patch.Put(nhanXet_CanhBao);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!NhanXet_CanhBaoExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(nhanXet_CanhBao);
        }

        // POST: odata/NhanXet_CanhBao
        public IHttpActionResult Post(NhanXet_CanhBao nhanXet_CanhBao)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            db.NhanXet_CanhBao.Add(nhanXet_CanhBao);
            db.SaveChanges();

            return Created(nhanXet_CanhBao);
        }

        // PATCH: odata/NhanXet_CanhBao(5)
        [AcceptVerbs("PATCH", "MERGE")]
        public IHttpActionResult Patch([FromODataUri] int key, Delta<NhanXet_CanhBao> patch)
        {
            Validate(patch.GetEntity());

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            NhanXet_CanhBao nhanXet_CanhBao = db.NhanXet_CanhBao.Find(key);
            if (nhanXet_CanhBao == null)
            {
                return NotFound();
            }

            patch.Patch(nhanXet_CanhBao);

            try
            {
                db.SaveChanges();
            }
            catch (DbUpdateConcurrencyException)
            {
                if (!NhanXet_CanhBaoExists(key))
                {
                    return NotFound();
                }
                else
                {
                    throw;
                }
            }

            return Updated(nhanXet_CanhBao);
        }

        // DELETE: odata/NhanXet_CanhBao(5)
        public IHttpActionResult Delete([FromODataUri] int key)
        {
            NhanXet_CanhBao nhanXet_CanhBao = db.NhanXet_CanhBao.Find(key);
            if (nhanXet_CanhBao == null)
            {
                return NotFound();
            }

            db.NhanXet_CanhBao.Remove(nhanXet_CanhBao);
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

        private bool NhanXet_CanhBaoExists(int key)
        {
            return db.NhanXet_CanhBao.Count(e => e.Id == key) > 0;
        }
    }
}
