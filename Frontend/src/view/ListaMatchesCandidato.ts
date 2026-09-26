import type { Candidato } from '../model/Candidato'
import { BancodeDados } from '../repository/BancodeDados'

export function renderListaMatchesCandidato(
    candidato: Candidato,
    onVoltar?: () => void
): void {
    const app = document.querySelector<HTMLDivElement>('#app')

    if (!app) {
        return
    }

    const matches = BancodeDados.getMatchesDoCandidato(candidato)

    const curtidasPendentes =
        BancodeDados.getCurtidasPendentesDoCandidato(candidato)

    app.innerHTML = `
        <section class="lista-matches tela-com-transicao">
            <h1>Meus Matches</h1>
            
            <h2>Curtidas aguardando Match</h2>

                ${
                        curtidasPendentes.length === 0
                            ? `
                            <div class="estado-vazio">
                                <span class="estado-vazio-icone">♥</span>
                                <h3>Nenhuma curtida aguardando Match</h3>
                                <p>As vagas que você curtir e ainda não tiverem reciprocidade aparecerão aqui.</p>
                            </div>
                        `
                            : `
                            <div class="cards-matches">
                                ${curtidasPendentes.map(curtida => `
                                    <div class="card-match">
                                        <h3>♥ ${curtida.vaga!.titulo}</h3>
                
                                        <p>
                                            <strong>Empresa:</strong>
                                            Match necessário para visualização
                                        </p>
                                    </div>
                                `).join('')}
                            </div>
                        `
                    }
                
                <h2>Meus Matches</h2>
                
                ${
                        matches.length === 0
                            ? `
                            <div class="estado-vazio">
                                <span class="estado-vazio-icone">🤝</span>
                                <h3>Nenhum Match ainda</h3>
                                <p>Quando houver interesse dos dois lados, o seu Match aparecerá aqui.</p>
                            </div>
                        `
                            : `
                            <div class="cards-matches">
                                ${matches.map(match => `
                                    <div class="card-match">
                                        <h3>🤝 ${match.empresa.nome}</h3>
                
                                        <p>
                                            <strong>Vaga:</strong>
                                            ${match.vaga.titulo}
                                        </p>
                
                                        <p>
                                            <strong>Local:</strong>
                                            ${match.vaga.pais} -
                                            ${match.vaga.estado}
                                        </p>
                                    </div>
                                `).join('')}
                            </div>
                        `
                    }

            <button id="btn-voltar-matches">
                Voltar
            </button>
        </section>
    `

    document
        .querySelector<HTMLButtonElement>('#btn-voltar-matches')
        ?.addEventListener('click', () => {
            onVoltar?.()
        })
}