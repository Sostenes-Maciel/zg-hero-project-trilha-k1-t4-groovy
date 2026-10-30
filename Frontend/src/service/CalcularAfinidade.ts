import type { Candidato } from '../model/Candidato'
import type { Empresa } from '../model/Empresa'

export function calcularAfinidade(candidato: Candidato, empresa: Empresa): number {
    const competenciasCandidato = normalizarCompetencias(candidato.competencias)
    const competenciasEmpresa = normalizarCompetencias(empresa.competencias)

    if (competenciasCandidato.size === 0) {
        return 0
    }

    const competenciasEmComum = contarCompetenciasEmComum(competenciasCandidato, competenciasEmpresa)

    return Math.round((competenciasEmComum / competenciasCandidato.size) * 100)
}

function normalizarCompetencias(competencias: unknown): Set<string> {
    if (!Array.isArray(competencias)) {
        return new Set<string>()
    }

    const arrayLimpo = competencias
        .filter((competencia): competencia is string =>
            typeof competencia === 'string'
        )
        .map(competencia => competencia.trim().toLowerCase())
        .filter(competencia => competencia.length > 0)

    return new Set(arrayLimpo)
}

function contarCompetenciasEmComum(setA: Set<string>, setB: Set<string>): number {
    let quantidade = 0

    for (const competencia of setA) {
        if (setB.has(competencia)) {
            quantidade++
        }
    }

    return quantidade
}