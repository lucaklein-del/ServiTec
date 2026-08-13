using Microsoft.EntityFrameworkCore;
using ServiTec.Application.DTOs;
using ServiTec.Domain.Models;
using ServiTec.Infrastructure.Data;

namespace ServiTec.Application.Services
{
    public class MenjadorService
    {
        private readonly ServiTecDbContext _context;
        private readonly IRepository<Menjador> _menjadorRepository;

        public MenjadorService(ServiTecDbContext context, IRepository<Menjador> menjadorRepository)
        {
            _context = context;
            _menjadorRepository = menjadorRepository;
        }

        public async Task<MenjadorDTO?> GetById(int id)
        {
            var menjador = await _menjadorRepository.GetById(id);

            if (menjador == null)
                return null;

            return new MenjadorDTO
            {
                IdMenjador = menjador.IdMenjador,
                NomMenjador = menjador.NomMenjador,
                Actiu = menjador.Actiu
            };
        }

        public async Task<IEnumerable<MenjadorDTO>> GetMenjadors()
        {
            var menjadors = await _context.Menjadors
                .Include(m => m.Taula)
                .ToListAsync();

            return menjadors.Select(m => new MenjadorDTO
            {
                IdMenjador = m.IdMenjador,
                NomMenjador = m.NomMenjador,
                Actiu = m.Actiu,
                Taules = m.Taula.Select(t => new TaulaDTO
                {
                    IdTaula = t.IdTaula,
                    Numero = t.Numero,
                    Capacitat = t.Capacitat,
                    Estat = t.Estat,
                    IdMenjador = t.IdMenjador,
                    EstatComanda = _context.Comanda
                        .Where(c => c.IdTaula == t.IdTaula && (c.Estat == "oberta" || c.Estat == "pendent"))
                        .OrderByDescending(c => c.DataCreacio)
                        .Select(c => c.Estat)
                        .FirstOrDefault() ?? "lliure",
                    PosX = t.PosX,
                    PosY = t.PosY
                }).ToList()
            }).ToList();
        }

        public async Task<Menjador?> Create(CreateMenjadorDTO dto)
        {
            var menjador = new Menjador
            {
                NomMenjador = dto.PostNomMenjador,
                Actiu = true,

                Taula = dto.PostTaules?.Select((t, index) => new Taula
                {
                    Numero = index + 1,
                    Capacitat = t.PostCapacitat,
                    PosX = t.PostPosX,
                    PosY = t.PostPosY,
                    Estat = true
                }).ToList() ?? new List<Taula>()
            };

            var resultat = await _menjadorRepository.Create(menjador);
            return resultat;
        }

        public async Task<Menjador?> Update(int id, UpdateMenjadorDTO dto)
        {
            var menjador = await _menjadorRepository.GetById(id);

            if (menjador == null)
                return null;

            menjador.NomMenjador = dto.PutNomMenjador;
            menjador.Actiu = dto.PutActiu;

            await _menjadorRepository.Update(menjador);
            return menjador;
        }

        public async Task<bool> Delete(int id)
        {
            var menjador = await _menjadorRepository.GetById(id);

            if (menjador == null)
                return false;

            await _menjadorRepository.Delete(menjador);
            return true;
        }
    }
}