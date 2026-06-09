namespace ServiTec.Application.DTOs
{
    public class UpdateComandaDTO
    {
            public int PutIdComanda { get; set; }

            public DateTime PutDataCreacio { get; set; }

            public string PutEstat { get; set; } = null!;

            public decimal PutTotal { get; set; }

            public int PutIdTaula { get; set; }

            public int PutIdUsuari
        {
                get; set;
            }
    }

    public class UpdateCategoriaDTO
    {
        public int PutIdCategoria { get; set; }

        public string PutNom { get; set; } = null!;

        public string? PutDescripcio { get; set; }
    }

    public class UpdateProducteDTO
    {
        public int PutIdProducte { get; set; }
        public string PutNom { get; set; } = null!;
        public string? PutDescripcio { get; set; }
        public decimal PutPreu { get; set; }
        public bool PutActiu { get; set; }
        public int PutIdCategoria { get; set; }
    }

    public class UpdateUsuariDTO
        {
            public int PutIdUsuari { get; set; }
            public string PutNomUsuari { get; set; } = null!;
            public string PutContrasenya { get; set; } = null!;
            public bool PutActiu { get; set; }
            public bool PutAdmin { get; set; }
    }

    namespace ServiTec.DTOs
    {
        public class UpdateTaulaDTO
        {
            public int PutNumero { get; set; }

            public int PutCapacitat { get; set; }

            public bool PutEstat { get; set; }
        }
    }

}