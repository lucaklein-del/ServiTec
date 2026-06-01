using Microsoft.AspNetCore.Mvc;
using ServiTec.Application.DTOs;
using ServiTec.Application.Services;
using ServiTec.Domain.Models;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace ServiTec.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class CategoriaController : ControllerBase
    {
        private readonly CategoriaService _categoriaService;

        public CategoriaController(CategoriaService categoriaService)
        {
            _categoriaService = categoriaService;
        }

        /// <brief>
        /// Recupera la llista completa de categories del sistema.
        /// </brief>
        /// <pre>
        /// - El servei de categories ha d'estar operatiu.
        /// </pre>
        /// <post>
        /// - Es retorna una col·lecció amb totes les categories registrades.
        /// </post>
        /// <returns>
        /// 200 OK amb la llista de categories.
        /// </returns>
        [HttpGet("llistar")]
        public async Task<ActionResult<IEnumerable<Categoria>>> LlistarCategoria()
        {
            var categorias = await _categoriaService.GetCategorias();
            return Ok(categorias);
        }

        /// <brief>
        /// Cerca una categoria concreta a partir del seu identificador.
        /// </brief>
        /// <pre>
        /// - L'identificador proporcionat ha de ser vàlid.
        /// </pre>
        /// <post>
        /// - Si la categoria existeix, es retorna la seva informació.
        /// </post>
        /// <param name="id">
        /// Identificador de la categoria a cercar.
        /// </param>
        /// <returns>
        /// 200 OK amb la categoria trobada.
        /// 404 NotFound si la categoria no existeix.
        /// </returns>
        [HttpGet("buscar/{id}")]
        public async Task<ActionResult<Usuari>> BuscarCategoria(int id)
        {
            var categoria = await _categoriaService.GetById(id);

            if (categoria == null)
                return NotFound();

            return Ok(categoria);
        }

        /// <brief>
        /// Crea una nova categoria al sistema.
        /// </brief>
        /// <pre>
        /// - Les dades del DTO han de ser vàlides.
        /// </pre>
        /// <post>
        /// - Es crea un nou registre de categoria al sistema.
        /// </post>
        /// <param name="dto">
        /// Objecte DTO que conté la informació necessària per crear la categoria.
        /// </param>
        /// <returns>
        /// 201 Created si la creació es realitza correctament.
        /// </returns>
        [HttpPost("crear")]
        public async Task<ActionResult> CrearCategoria(CreateCategoriaDTO dto)
        {
            var categoria = await _categoriaService.CrearCategoria(dto);

            return StatusCode(StatusCodes.Status201Created, categoria);
        }

        /// <brief>
        /// Actualitza la informació d'una categoria existent.
        /// </brief>
        /// <pre>
        /// - La categoria indicada ha d'existir.
        /// - Les dades proporcionades han de ser vàlides.
        /// </pre>
        /// <post>
        /// - Les dades de la categoria queden actualitzades al sistema.
        /// </post>
        /// <param name="id">
        /// Identificador de la categoria a actualitzar.
        /// </param>
        /// <param name="dto">
        /// Objecte DTO amb les noves dades de la categoria.
        /// </param>
        /// <returns>
        /// 200 OK si l'actualització es realitza correctament.
        /// 404 NotFound si la categoria no existeix.
        /// </returns>
        [HttpPut("actualitzar/{id}")]
        public async Task<ActionResult> ActualitzarCategoria(int id, UpdateCategoriaDTO dto)
        {
            var categoria = await _categoriaService.UpdateCategoriaDTO(id, dto);

            if (categoria == null)
                return NotFound();

            return Ok(categoria);
        }

        /// <brief>
        /// Elimina una categoria del sistema.
        /// </brief>
        /// <pre>
        /// - La categoria indicada ha d'existir al sistema.
        /// </pre>
        /// <post>
        /// - La categoria és eliminada del sistema.
        /// </post>
        /// <param name="id">
        /// Identificador de la categoria a eliminar.
        /// </param>
        /// <returns>
        /// 204 NoContent si l'eliminació es realitza correctament.
        /// 404 NotFound si la categoria no existeix.
        /// </returns>
        [HttpDelete("eliminar/{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var eliminat = await _categoriaService.DeleteCategoria(id);

            if (!eliminat)
                return NotFound();

            return NoContent();
        }
    }
}