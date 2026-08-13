using System;
using System.Collections.Generic;
namespace ServiTec.Domain.Models
{
    public class Menjador
    {
        public int IdMenjador { get; set; }
        public string NomMenjador { get; set; } = null!;
        public bool Actiu { get; set; }
        public virtual ICollection<Taula> Taula { get; set; } = new List<Taula>();
    }
}
