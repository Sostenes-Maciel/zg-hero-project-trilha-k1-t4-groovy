import type { Candidato } from '../model/Candidato'
import type { Empresa } from '../model/Empresa'

export function calcularAfinidadePorCompetencias(competenciasBase: unknown, competenciasAlvo: unknown): number {
    const competenciasBaseNormalizadas = normalizarCompetencias(competenciasBase)
    const competenciasAlvoNormalizadas = normalizarCompetencias(competenciasAlvo)

    if (competenciasBaseNormalizadas.size === 0) {
        return 0
    }

    const competenciasEmComum = contarCompetenciasEmComum(
        competenciasBaseNormalizadas,
        competenciasAlvoNormalizadas
    )

    return Math.round((competenciasEmComum / competenciasBaseNormalizadas.size) * 100)
}

export function calcularAfinidade(candidato: Candidato, empresa: Empresa): number {
    return calcularAfinidadePorCompetencias(candidato.competencias, empresa.competencias)
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