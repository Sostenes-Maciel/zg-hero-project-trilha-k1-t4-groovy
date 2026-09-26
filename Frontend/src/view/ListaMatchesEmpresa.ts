import type { Empresa } from '../model/Empresa'
import { BancodeDados } from '../repository/BancodeDados'

export function renderListaMatchesEmpresa(
    empresa: Empresa,
    onVoltar?: () => void
): void {
    const app = document.querySelector<HTMLDivElement>('#app')

    if (!app) {
        return
    }

    const matches = BancodeDados.getMatchesDaEmpresa(empresa)

    app.innerHTML = `
        <section class="lista-matches">
            <h1>Meus Matches</h1>

            ${
        matches.length === 0
            ? `
                        <p>A empresa ainda não possui Matches.</p>
                    `
            : `
                        <div class="cards-matches">
                            ${matches.map(match => `
                                <div class="card-match">
                                    <h2>🤝 ${match.candidato.nome}</h2>

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