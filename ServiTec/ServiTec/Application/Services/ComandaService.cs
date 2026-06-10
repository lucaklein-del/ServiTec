using ServiTec.Application.DTOs;
using ServiTec.Domain.Models;

public class ComandaService
{
    private readonly IRepository<Comanda> _repository;
    private readonly IRepository<Producte> _productRepository;
    private readonly IRepository<Taula> _taulaRepository;

    public ComandaService(IRepository<Comanda> repository, IRepository<Producte> productRepository, IRepository<Taula> taulaResposity)
    {
        _repository = repository;
        _productRepository = productRepository;
        _taulaRepository = taulaResposity;

    }

    public async Task<Comanda?> GetById(int id)
    {
        return await _repository.GetById(id);
    }

    public async Task<bool> DeleteComanda(int id)
    {
        var Comanda = await _repository.GetById(id);

        if (Comanda == null)
            return false;

        await _repository.Delete(Comanda);
        return true;
    }

    public async Task<IEnumerable<ComandaDTO>> GetComandas()
    {
        var Comandas = await _repository.GetAll();
        return Comandas.Select(p => new ComandaDTO
        {
            IdComanda = p.IdComanda,
            DataCreacio = p.DataCreacio,
            Estat = p.Estat,
            Total = p.Total,
            IdTaula = p.IdTaula,
            IdUsuari = p.IdUsuari
        }).ToList();
    }

    public async Task<Comanda?> CrearComanda(CreateComandaDTO dto)
    {
        var taula = await _taulaRepository.GetById(dto.PostIdTaula);
        if (taula == null)
        {
            // En servicios lanzamos excepciones de argumento o de negocio
            throw new ArgumentException("La mesa especificada no existe.");
        }

        // 2. Control de seguridad: Si ya está ocupada (Estat == false), no dejamos duplicar
        if (!taula.Estat)
        {
            throw new InvalidOperationException("Esta mesa ya tiene una comanda activa.");
        }

        // 1. Instanciamos el objeto principal Comanda
        var comanda = new Comanda
        {
            DataCreacio = DateTime.Now, // Fecha e hora actual del servidor
            Estat = dto.PostEstat ?? "Pendent",
            IdTaula = dto.PostIdTaula,
            IdUsuari = dto.PostIdUsuari,
            Total = 0, // Lo calcularemos sumando los productos
            LiniaComanda = new List<LiniaComanda>() // Colección de navegación en tu modelo Comanda
        };

        decimal granTotal = 0;

        // 2. Recorremos las líneas que nos ha enviado el camarero desde Android
        foreach (var liniaDto in dto.PostLinies)
        {
            // Usamos tu método GetById(id) del repositorio genérico de productos
            var producte = await _productRepository.GetById(liniaDto.PostIdProducte);

            if (producte != null)
            {
                // Tomamos el precio del momento exacto de la creación (La "Fotografía" del precio)
                decimal preuUnitari = (decimal)producte.Preu;
                decimal subtotal = preuUnitari * liniaDto.PostQuantitat;

                granTotal += subtotal;

                // Creamos la línea física que guardará el historial intacto
                var novaLinia = new LiniaComanda
                {
                    Quantitat = liniaDto.PostQuantitat,
                    PreuUnitari = preuUnitari,
                    Subtotal = subtotal,
                    IdProducte = liniaDto.PostIdProducte
                    // NO asignamos IdComanda. Al meterlo en la lista de 'comanda', EF lo mapea solo.
                };

                comanda.LiniaComanda.Add(novaLinia);
            }
        }

        // 3. Asignamos el total real calculado por el Back de forma segura
        comanda.Total = granTotal;

        taula.Estat = false;
        await _taulaRepository.Update(taula);

        // 4. Guardamos en la BD a través de tu repositorio genérico
        // Al pasarle 'comanda', EF guardará la cabecera y todas las filas de LiniasComanda de golpe.
        var resultat = await _repository.Create(comanda);

        return resultat;
    }

    public async Task<Comanda?> UpdateComandaDTO(int id, UpdateComandaDTO dto)
    {
        var Comanda = await _repository.GetById(id);

        if (Comanda == null)
            return null;

        Comanda.IdComanda = dto.PutIdComanda;
        Comanda.DataCreacio = dto.PutDataCreacio;
        Comanda.Estat = dto.PutEstat;
        Comanda.Total = dto.PutTotal;
        Comanda.IdTaula = dto.PutIdTaula;
        Comanda.IdUsuari = dto.PutIdUsuari;

        await _repository.Update(Comanda);

        return Comanda;
    }
}
