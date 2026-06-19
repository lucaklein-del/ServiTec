using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using ServiTec.Application.DTOs;
using ServiTec.Application.Services;
using ServiTec.Domain.Models;

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
        [HttpGet("llistar")]
        public async Task<ActionResult<IEnumerable<CategoriaDTO>>> LlistarCategoria() // ✨ Mapeado al DTO de salida si lo usas, o Categoria
        {
            var categorias = await _categoriaService.GetCategorias();
            return Ok(categorias);
        }

        /// <brief>
        /// Cerca una categoria concreta a partir del seu identificador.
        /// </brief>
        [HttpGet("buscar/{id}")]
        public async Task<ActionResult<Categoria>> BuscarCategoria(int id) // 🎇 ¡CORREGIDO! Cambiado Usuari por Categoria
        {
            var categoria = await _categoriaService.GetById(id);

            if (categoria == null)
                return NotFound(new { message = $"No s'ha trobat la categoria amb ID {id}" });

            return Ok(categoria);
        }

        /// <brief>
        /// Crea una nova categoria al sistema.
        /// </brief>
        [HttpPost("crear")]
        public async Task<ActionResult> CrearCategoria(CreateCategoriaDTO dto)
        {
            var categoria = await _categoriaService.CrearCategoria(dto);

            // 🛠️ Es buena práctica devolver un CreatedAtAction o el código 201
            return StatusCode(StatusCodes.Status201Created, categoria);
        }

        /// <brief>
        /// Actualitza la informació d'una categoria existent.
        /// </brief>
        [HttpPut("actualitzar/{id}")]
        public async Task<ActionResult> ActualitzarCategoria(int id, UpdateCategoriaDTO dto)
        {
            var categoria = await _categoriaService.UpdateCategoriaDTO(id, dto);

            if (categoria == null)
                return NotFound(new { message = $"No s'ha pogut actualitzar. La categoria amb ID {id} no existeix." });

            return Ok(categoria);
        }

        /// <brief>
        /// Elimina una categoria del sistema.
        /// </brief>
        [HttpDelete("eliminar/{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var eliminat = await _categoriaService.DeleteCategoria(id);

            if (!eliminat)
                return NotFound(new { message = $"No s'ha pogut eliminar. La categoria amb ID {id} no existeix." });

            return NoContent();
        }
    }
}