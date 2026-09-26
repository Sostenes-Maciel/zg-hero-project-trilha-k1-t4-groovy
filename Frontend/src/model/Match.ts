import type { Candidato } from './Candidato'
import type { Empresa } from './Empresa'
import type { Vaga } from './Vaga'

export interface Match {
    candidato: Candidato
    empresa: Empresa
    vaga: Vaga
}