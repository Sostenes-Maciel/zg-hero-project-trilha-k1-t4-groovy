import type { Candidato } from '../model/Candidato'
import type { Empresa } from '../model/Empresa'

export function calcularAfinidade(
    candidato: Candidato,
    empresa: Empresa
): number {

    const competenciasCandidato = new Set(
        candidato.competencias
            .map(competencia => competencia.trim().toLowerCase())
            .filter(competencia => competencia.length > 0)
    )

    const competenciasEmpresa = new Set(
        empresa.competencias
            .map(competencia => competencia.trim().toLowerCase())
            .filter(competencia => competencia.length > 0)
    )

    if (competenciasCandidato.size === 0) {
        return 0
    }

    let competenciasEmComum = 0

    competenciasCandidato.forEach(competencia => {
        if (competenciasEmpresa.has(competencia)) {
            competenciasEmComum++
        }
    })

    return Math.round(
        (competenciasEmComum / competenciasCandidato.size) * 100
    )
}