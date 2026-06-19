using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using ServiTec.Application.DTOs;
using ServiTec.Application.DTOs.ServiTec.DTOs;
using ServiTec.Services;

namespace ServiTec.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class LiniaComandaController : ControllerBase
    {
        private readonly LiniaComandaService _liniaComandaService;

        public LiniaComandaController(LiniaComandaService liniaComandaService)
        {
            _liniaComandaService = liniaComandaService;
        }

        /// <brief>
        /// Recupera la llista completa de línies de comanda del sistema.
        /// </brief>
        /// <pre>
        /// - El servei de línies de comanda ha d'estar operatiu.
        /// </pre>
        /// <post>
        /// - Es retorna una col·lecció amb totes les línies registrades.
        /// </post>
        /// <returns>
        /// 200 OK amb la llista de línies de comanda.
        /// </returns>
        [HttpGet("llistar")]
        public async Task<ActionResult<IEnumerable<LiniaComandaDTO>>> LlistarLinies()
        {
            var linies = await _liniaComandaService.GetAll();
            return Ok(linies);
        }

        /// <brief>
        /// Cerca una línia de comanda concreta a partir del seu identificador.
        /// </brief>
        /// <pre>
        /// - L'identificador proporcionat ha de ser vàlid.
        /// </pre>
        /// <post>
        /// - Si la línia existeix, es retorna la seva informació.
        /// </post>
        /// <param name="id">Identificador de la línia a cercar.</param>
        /// <returns>
        /// 200 OK amb la línia trobada.
        /// 404 NotFound si la línia no existeix.
        /// </returns>
        [HttpGet("buscar/{id}")]
        public async Task<ActionResult<LiniaComandaDTO>> BuscarLinia(int id)
        {
            var linia = await _liniaComandaService.GetById(id);

            if (linia == null)
                return NotFound(new { message = $"No s'ha trobat la línia de comanda amb ID {id}" });

            return Ok(linia);
        }

        /// <brief>
        /// Afegeix una nova línia de comanda (un producte) a una comanda existent.
        /// </brief>
        /// <pre>
        /// - Les dades del DTO han de ser vàlides i el producte ha d'existir.
        /// </pre>
        /// <post>
        /// - Es crea un nou registre de línia de comanda i es recalcula el total de la comanda.
        /// </post>
        /// <param name="dto">Objecte DTO amb la informació per crear la línia.</param>
        /// <returns>
        /// 201 Created amb el DTO de la línia creada.
        /// 400 BadRequest si el producte especificat no existeix.
        /// </returns>
        [HttpPost("crear")]
        public async Task<ActionResult<LiniaComandaDTO>> CrearLinia(CreateLiniaComandaDTO dto)
        {
            var liniaCreada = await _liniaComandaService.Create(dto);

            if (liniaCreada == null)
                return BadRequest(new { message = "No s'ha pogut crear la línia. El producte especificat no existeix." });

            return StatusCode(StatusCodes.Status201Created, liniaCreada);
        }

        /// <brief>
        /// Actualitza la quantitat d'una línia de comanda existent.
        /// </brief>
        /// <pre>
        /// - La línia indicada ha d'existir.
        /// - La nova quantitat ha de ser vàlida.
        /// </pre>
        /// <post>
        /// - La quantitat de la línia queda actualitzada i es recalcula el total de la comanda.
        /// </post>
        /// <param name="id">Identificador de la línia a actualitzar.</param>
        /// <param name="dto">Objecte DTO amb la nova quantitat.</param>
        /// <returns>
        /// 200 OK si l'actualització es realitza correctament.
        /// 404 NotFound si la línia no existeix.
        /// </returns>
        [HttpPut("actualitzar/{id}")]
        public async Task<ActionResult> ActualitzarLinia(int id, UpdateLiniaComandaDTO dto)
        {
            var exit = await _liniaComandaService.Update(id, dto);

            if (!exit)
                return NotFound(new { message = $"No s'ha pogut actualitzar. La línia amb ID {id} no existeix." });

            return Ok(new { message = "Línia de comanda actualitzada correctament." });
        }

        /// <brief>
        /// Elimina una línia de comanda del sistema (treu un producte de la comanda).
        /// </brief>
        /// <pre>
        /// - La línia indicada ha d'existir al sistema.
        /// </pre>
        /// <post>
        /// - La línia és eliminada i es recalcula automàticament el total de la comanda.
        /// </post>
        /// <param name="id">Identificador de la línia a eliminar.</param>
        /// <returns>
        /// 204 NoContent si l'eliminació es realitza correctament.
        /// 404 NotFound si la línia no existeix.
        /// </returns>
        [HttpDelete("eliminar/{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var eliminat = await _liniaComandaService.Delete(id);

            if (!eliminat)
                return NotFound(new { message = $"No s'ha pogut eliminar. La línia amb ID {id} no existeix." });

            return NoContent();
        }
    }
}