import type { Empresa } from '../model/Empresa'
import type { Match } from '../model/Match'
import type { Curtida } from '../model/Curtida'
import { BancodeDados } from '../dao/BancodeDados'

export function renderListaMatchesEmpresa(empresa: Empresa, onVoltar?: () => void): void {
    const app = document.querySelector<HTMLDivElement>('#app')
    if (!app) return

    const matches = BancodeDados.getMatchesDaEmpresa(empresa)
    const curtidasPendentes = BancodeDados.getCurtidasPendentesDaEmpresa(empresa)

    app.innerHTML = `
        <section class="lista-matches tela-com-transicao">
            <h1>Meus Matches</h1>

            <h2>Curtidas aguardando Match</h2>
            ${gerarSecaoCurtidasPendentes(curtidasPendentes)}

            <h2>Meus Matches</h2>
            ${gerarSecaoMatchesRealizados(matches)}

            <button id="btn-voltar-matches">Voltar</button>
        </section>
    `

    configurarEventoVoltar(onVoltar)
}

function gerarSecaoCurtidasPendentes(curtidas: Curtida[]): string {
    if (curtidas.length === 0) {
        return `
            <div class="estado-vazio">
                <span class="estado-vazio-icone">♥</span>
                <h3>Nenhuma curtida aguardando Match</h3>
                <p>Os candidatos que demonstrarem interesse e ainda não tiverem reciprocidade aparecerão aqui.</p>
            </div>
        `
    }

    const cardsHtml = curtidas.map(curtida => `
        <div class="card-match">
            <h3>♥ Match pendente</h3>
            <p>
                <strong>Candidato:</strong> Match necessário para visualização
            </p>
            <p>
                <strong>Competências:</strong> ${curtida.candidato.competencias.join(', ')}
            </p>
        </div>
    `).join('')

    return `<div class="cards-matches">${cardsHtml}</div>`
}

function gerarSecaoMatchesRealizados(matches: Match[]): string {
    if (matches.length === 0) {
        return `
            <div class="estado-vazio">
                <span class="estado-vazio-icone">🤝</span>
                <h3>Nenhum Match ainda</h3>
                <p>Quando um candidato demonstrar interesse recíproco, o Match aparecerá aqui.</p>
            </div>
        `
    }

    const cardsHtml = matches.map(match => `
        <div class="card-match">
            <h3>🤝 ${match.candidato.nome}</h3>
            <p>
                <strong>Vaga:</strong> ${match.vaga.titulo}
            </p>
            <p>
                <strong>Local:</strong> ${match.vaga.pais} - ${match.vaga.estado}
            </p>
        </div>
    `).join('')

    return `<div class="cards-matches">${cardsHtml}</div>`
}

function configurarEventoVoltar(onVoltar?: () => void): void {
    const btnVoltar = document.querySelector<HTMLButtonElement>('#btn-voltar-matches')

    btnVoltar?.addEventListener('click', () => {
        onVoltar?.()
    })
}