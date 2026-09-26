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

    app.innerHTML = `
        <section class="lista-matches">
            <h1>Meus Matches</h1>

            ${
        matches.length === 0
            ? `
                        <p>Você ainda não possui Matches.</p>
                    `
            : `
                        <div class="cards-matches">
                            ${matches.map(match => `
                                <div class="card-match">
                                    <h2>🤝 ${match.empresa.nome}</h2>

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