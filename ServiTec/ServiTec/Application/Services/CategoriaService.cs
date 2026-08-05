using ServiTec.Application.DTOs;
using ServiTec.Domain.Models;

    public class CategoriaService
    {
        private readonly IRepository<Categoria> _repository;

        public CategoriaService(IRepository<Categoria> repository)
        {
            _repository = repository;
        }

        public async Task<Categoria?> GetById(int id)
        {
            return await _repository.GetById(id);
        }

        public async Task<bool> DeleteCategoria(int id)
        {
            var Categoria = await _repository.GetById(id);

            if (Categoria == null)
                return false;

            await _repository.Delete(Categoria);
            return true;
        }

        public async Task<IEnumerable<CategoriaDTO>> GetCategorias()
        {
            var Categorias = await _repository.GetAll();
            return Categorias.Select(p => new CategoriaDTO
            {
                IdCategoria = p.IdCategoria,
                Nom = p.Nom,
                Descripcio = p.Descripcio
            }).ToList();
        }

        public async Task<Categoria?> CrearCategoria(CreateCategoriaDTO dto)
        {
            var Categoria = new Categoria
            {
                Nom = dto.PostNom,
                Descripcio = dto.PostDescripcio,
                IdCategoria = dto.PostIdCategoria
            };

            var resultat = await _repository.Create(Categoria);

            return resultat;
        }

        public async Task<Categoria?> UpdateCategoriaDTO(int id, UpdateCategoriaDTO dto)
        {
            var Categoria = await _repository.GetById(id);

            if (Categoria == null)
                return null;

            Categoria.Nom = dto.PutNom;
            Categoria.Descripcio = dto.PutDescripcio;

            await _repository.Update(Categoria);

            return Categoria;
        }
    }
