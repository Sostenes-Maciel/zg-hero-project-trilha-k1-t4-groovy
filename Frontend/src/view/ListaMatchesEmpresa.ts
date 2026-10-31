import type { Empresa } from '../model/Empresa'
import type { Match } from '../model/Match'
import type { Curtida } from '../model/Curtida'

export async function renderListaMatchesEmpresa(empresa: Empresa, onVoltar?: () => void): Promise<void> {
    const app = document.querySelector<HTMLDivElement>('#app')
    if (!app) return

    app.innerHTML = `<section class="lista-matches tela-com-transicao"><h2>Carregando matches...</h2></section>`

    try {
        const cnpjLimpo = empresa.cnpj.replace(/\D/g, '')
        const resposta = await fetch(`http://localhost:8080/api/matches?cnpj=${cnpjLimpo}&completo=true`)

        let matches: Match[] = []
        let curtidasPendentes: Curtida[] = []

        if (resposta.ok) {
            const dados = await resposta.json()
            matches = dados.matches || []
            curtidasPendentes = dados.curtidasPendentes || []
        }

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
        configurarEventoVoltar(empresa, onVoltar)
    } catch (erro) {
        console.error("Erro ao buscar matches completos da empresa:", erro)
        app.innerHTML = `
            <section class="lista-matches">
                <h2>Erro ao conectar com o servidor.</h2>
                <button id="btn-voltar-matches">Voltar</button>
            </section>
        `
        configurarEventoVoltar(empresa, onVoltar)
    }
}

function gerarSecaoCurtidasPendentes(curtidas: Curtida[]): string {
    if (!Array.isArray(curtidas) || curtidas.length === 0) {
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
                <strong>Candidato:</strong> ${curtida.candidato?.nome ?? 'Match necessário para visualização'}
            </p>
        </div>
    `).join('')

    return `<div class="cards-matches">${cardsHtml}</div>`
}

function gerarSecaoMatchesRealizados(matches: Match[]): string {
    if (!Array.isArray(matches) || matches.length === 0) {
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
            <h3>🤝 ${match.candidato?.nome ?? 'Candidato'}</h3>
            <p>
                <strong>Vaga:</strong> ${match.vaga?.titulo ?? 'Vaga'}
            </p>
            <p>
                <strong>Local:</strong> ${match.vaga?.pais ?? ''} - ${match.vaga?.estado ?? ''}
            </p>
        </div>
    `).join('')

    return `<div class="cards-matches">${cardsHtml}</div>`
}

function configurarEventoVoltar(empresa: Empresa, onVoltar?: () => void): void {
    const btnVoltar = document.querySelector<HTMLButtonElement>('#btn-voltar-matches')

    btnVoltar?.addEventListener('click', (e) => {
        e.preventDefault()
        if (onVoltar) {
            onVoltar()
        } else {
            import('./PerfilEmpresa.ts').then(({ renderPerfilEmpresa }) => {
                renderPerfilEmpresa(empresa)
            })
        }
    })
}