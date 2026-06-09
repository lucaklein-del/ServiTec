using Microsoft.AspNetCore.Mvc;
using ServiTec.Application.DTOs;
using ServiTec.Application.Services;
using ServiTec.Domain.Models;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace ServiTec.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ComandaController : ControllerBase
    {
        private readonly ComandaService _comandaService;

        public ComandaController(ComandaService ComandaService)
        {
            _comandaService = ComandaService;
        }

        /// <brief>
        /// Recupera la llista completa de categories del sistema.
        /// </brief>
        /// <pre>
        /// - El servei de categories ha d'estar operatiu.
        /// </pre>
        /// <post>
        /// - Es retorna una col·lecció amb totes les comandes registrades.
        /// </post>
        /// <returns>
        /// 200 OK amb la llista de categories.
        /// </returns>
        [HttpGet("llistar")]
        public async Task<ActionResult<IEnumerable<Comanda>>> Llistarcomanda()
        {
            var comandas = await _comandaService.GetComandas();
            return Ok(comandas);
        }

        /// <brief>
        /// Cerca una comanda concreta a partir del seu identificador.
        /// </brief>
        /// <pre>
        /// - L'identificador proporcionat ha de ser vàlid.
        /// </pre>
        /// <post>
        /// - Si la comanda existeix, es retorna la seva informació.
        /// </post>
        /// <param name="id">
        /// Identificador de la comanda a cercar.
        /// 
        /// </param>
        /// <returns>
        /// 200 OK amb la comanda trobada.
        /// 404 NotFound si la comanda no existeix.
        /// </returns>
        [HttpGet("buscar/{id}")]
        public async Task<ActionResult<Usuari>> Buscarcomanda(int id)
        {
            var comanda = await _comandaService.GetById(id);

            if (comanda == null)
                return NotFound();

            return Ok(comanda);
        }

        /// <brief>
        /// Crea una nova comanda al sistema.
        /// </brief>
        /// <pre>
        /// - Les dades del DTO han de ser vàlides.
        /// </pre>
        /// <post>
        /// - Es crea un nou registre de comanda al sistema.
        /// </post>
        /// <param name="dto">
        /// Objecte DTO que conté la informació necessària per crear la comanda.
        /// </param>
        /// <returns>
        /// 201 Created si la creació es realitza correctament.
        /// </returns>
        [HttpPost("crear")]
        public async Task<ActionResult> CrearComanda(CreateComandaDTO dto)
        {
            var comanda = await _comandaService.CrearComanda(dto);

            return StatusCode(StatusCodes.Status201Created, comanda);
        }

        /// <brief>
        /// Actualitza la informació d'una comanda existent.
        /// </brief>
        /// <pre>
        /// - La comanda indicada ha d'existir.
        /// - Les dades proporcionades han de ser vàlides.
        /// </pre>
        /// <post>
        /// - Les dades de la comanda queden actualitzades al sistema.
        /// </post>
        /// <param name="id">
        /// Identificador de la comanda a actualitzar.
        /// </param>
        /// <param name="dto">
        /// Objecte DTO amb les noves dades de la comanda.
        /// </param>
        /// <returns>
        /// 200 OK si l'actualització es realitza correctament.
        /// 404 NotFound si la comanda no existeix.
        /// </returns>
        [HttpPut("actualitzar/{id}")]
        public async Task<ActionResult> ActualitzarComanda(int id, UpdateComandaDTO dto)
        {
            var comanda = await _comandaService.UpdateComandaDTO(id, dto);

            if (comanda == null)
                return NotFound();

            return Ok(comanda);
        }

        /// <brief>
        /// Elimina una comanda del sistema.
        /// </brief>
        /// <pre>
        /// - La comanda indicada ha d'existir al sistema.
        /// </pre>
        /// <post>
        /// - La comanda és eliminada del sistema.
        /// </post>
        /// <param name="id">
        /// Identificador de la comanda a eliminar.
        /// </param>
        /// <returns>
        /// 204 NoContent si l'eliminació es realitza correctament.
        /// 404 NotFound si la comanda no existeix.
        /// </returns>
        [HttpDelete("eliminar/{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var eliminat = await _comandaService.DeleteComanda(id);

            if (!eliminat)
                return NotFound();

            return NoContent();
        }
    }
}