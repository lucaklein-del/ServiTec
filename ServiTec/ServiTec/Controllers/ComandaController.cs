using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
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

        [HttpGet("activa/{idTaula}")]
        public async Task<IActionResult> ObtenirComandaActiva(int idTaula)
        {
            // El controlador NO habla con la BD, le pide los datos al Service
            var comanda = await _comandaService.ObtenirComandaActivaSegonsTaulaAsync(idTaula);

            if (comanda == null)
            {
                return NotFound(new { message = "No s'ha trobat cap comanda activa per a aquesta taula." });
            }

            // Devolvemos el objeto (si usas DTOs de salida, lo mapeas aquí)
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
            try
            {
                var comanda = await _comandaService.CrearComanda(dto);
                return StatusCode(StatusCodes.Status201Created, comanda);
            }
            catch (ArgumentException ex)
            {
                // Si la taula no existeix en el sistema
                return NotFound(new { error = ex.Message });
            }
            catch (InvalidOperationException ex)
            {
                // Si la taula ja està ocupada (Estat == false)
                return BadRequest(new { error = ex.Message });
            }
            catch (Exception ex)
            {
                // Captura cualquier otro error inesperado para que la API no muera
                return StatusCode(StatusCodes.Status500InternalServerError, new { error = "Error intern del servidor.", detall = ex.Message });
            }
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

        [HttpGet("cuina")]
        public async Task<ActionResult<List<ComandaCuinaDTO>>> GetComandesCuina()
        {
            var comandes = await _comandaService.ObtenirComandesCuinaAsync();
            return Ok(comandes);
        }

        [HttpPut("{id}/estat")]
        public async Task<IActionResult> CanviarEstatComanda(int id, [FromBody] string nouEstat)
        {
            var exit = await _comandaService.CanviarEstatComandaAsync(id, nouEstat);

            if (!exit)
            {
                return NotFound(new { missatge = "Comanda no trobada" });
            }

            return Ok(new { missatge = "Estat de la comanda actualitzat correctament" });
        }

        [HttpPut("linia/{idLinia}/estat")]
        public async Task<IActionResult> CanviarEstatLinia(int idLinia, [FromBody] string nouEstat)
        {
            var exit = await _comandaService.CanviarEstatLiniaAsync(idLinia, nouEstat);

            if (!exit)
            {
                return NotFound(new { missatge = "Línia de comanda no trobada" });
            }

            return Ok(new { missatge = "Estat de la línia actualitzat correctament" });
        }

        [HttpPut("{idComanda}/cobrar")]
        public async Task<IActionResult> CobrarComanda(int idComanda)
        {
            var exit = await _comandaService.CobrarComandaAsync(idComanda);
            if (!exit) return NotFound(new { missatge = "Comanda no trobada" });

            return Ok(new { missatge = "Comanda cobrada i tancada correctament" });
        }

        [HttpPost("{idComanda}/linies")]
        public async Task<IActionResult> AfegirLinies(int idComanda, [FromBody] List<CreateLiniaComandaDTO> novesLinies)
        {
            if (novesLinies == null || !novesLinies.Any())
            {
                return BadRequest("La llista de productes no pot estar buida.");
            }

            try
            {
                var comandaActualitzada = await _comandaService.AfegirLiniesAComanda(idComanda, novesLinies);

                if (comandaActualitzada == null)
                {
                    return NotFound($"No s'ha trobat cap comanda amb l'ID {idComanda}");
                }

                return Ok(comandaActualitzada);
            }
            catch (ArgumentException ex)
            {
                return BadRequest(ex.Message);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Error intern del servidor: {ex.Message}");
            }
        }

        [HttpPut("linia/{idLinia}/eliminar")]
        public async Task<IActionResult> EliminarLiniaComanda(int idLinia)
        {
            if (idLinia <= 0)
            {
                return BadRequest(new { missatge = "ID de línia invàlid." });
            }

            var exit = await _comandaService.EliminarLiniaComandaAsync(idLinia);

            if (!exit)
            {
                return NotFound(new { missatge = "No s'ha trobat la línia de comanda especificada." });
            }

            return Ok(new { missatge = "Línia de comanda eliminada correctament i total actualitzat." });
        }
    }
}