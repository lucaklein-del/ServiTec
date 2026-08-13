using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using ServiTec.Application.DTOs;
using ServiTec.Application.Services;
using ServiTec.Domain.Models;
namespace ServiTec.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class MenjadorController : ControllerBase
    {
        private readonly MenjadorService _menjadorService;

        public MenjadorController(MenjadorService MenjadorService)
        {
            _menjadorService = MenjadorService;
        }

        [HttpGet("llistar")]
        public async Task<ActionResult<IEnumerable<Menjador>>> LlistarMenjador()
        {
            var menjadors = await _menjadorService.GetMenjadors();
            return Ok(menjadors);
        }

        [HttpPost("crear")]
        public async Task<ActionResult> CrearMenjador(CreateMenjadorDTO dto)
        {
            var menjador = await _menjadorService.Create(dto);

            return StatusCode(StatusCodes.Status201Created, menjador);
        }

        [HttpPut("actualitzar/{id}")]
        public async Task<ActionResult> AcualitzarMenjador(int id, UpdateMenjadorDTO dto)
        {
            var menjador = await _menjadorService.Update(id, dto);

            if (menjador == null)
                return NotFound();

            return Ok(menjador);
        }

        [HttpDelete("eliminar/{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var eliminat = await _menjadorService.Delete(id);

            if (!eliminat)
                return NotFound();

            return NoContent();
        }

    }
}
