import type { Candidato } from './Candidato'
import type { Empresa } from './Empresa'
import type { Vaga } from './Vaga'

export interface Curtida {
    candidato: Candidato
    vaga?: Vaga
    empresa?: Empresa
}