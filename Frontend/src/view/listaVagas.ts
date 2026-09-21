import { BancodeDados } from '../repository/BancodeDados'
import { renderPerfilVaga } from '../view/perfilVaga'

export function renderListaVagas(): void {
    const app = document.querySelector<HTMLDivElement>('#app')

    if (!app) {
        return
    }

    app.innerHTML = `
        <section>
            <h1>Vagas disponíveis</h1>
            
            <div class="filtro-vagas">
                <label for="filtro-vagas">Buscar vaga</label>
            
                <input
                    type="text"
                    id="filtro-vagas"
                    placeholder="Ex.: Java, Python, Tech Global..."
                >
            </div>

            <table class="tabela-vagas">
                <thead>
                    <tr>
                        <th>Vaga</th>
                        <th>Empresa</th>
                         <th>Ação</th>

                        
                    </tr>
                </thead>

                <tbody id="lista-vagas"></tbody>
            </table>
        </section>
    `

    const lista = document.querySelector<HTMLTableSectionElement>(
        '#lista-vagas'
    )

    if (!lista) {
        return
    }

    if (BancodeDados.vagas.length === 0) {
        lista.innerHTML = `
            <tr>
                <td colspan="4">Nenhuma vaga cadastrada.</td>
            </tr>
        `

        return
    }

    BancodeDados.vagas.forEach(vaga => {
        const linha = document.createElement('tr')

        linha.dataset.titulo = vaga.titulo.toLowerCase()

        linha.innerHTML = `
            <td>${vaga.titulo}</td>
            <td>Match necessário para visualização</td>
            <button class="btn-perfil-vaga" data-id="${vaga.id}">
                Ver vaga
            </button>
        `


        lista.appendChild(linha)
    })

    const filtroVagas = document.querySelector<HTMLInputElement>(
        '#filtro-vagas'
    )

    filtroVagas?.addEventListener('input', () => {
        const termo = filtroVagas.value.trim().toLowerCase()

        const linhas = lista.querySelectorAll<HTMLTableRowElement>('tr')

        linhas.forEach(linha => {
            const titulo = linha.dataset.titulo ?? ''

            const encontrou = titulo.includes(termo)

            linha.hidden = !encontrou
        })
    })

    const botoesPerfilVaga = document.querySelectorAll<HTMLButtonElement>(
        '.btn-perfil-vaga'
    )

    botoesPerfilVaga.forEach(botao => {
        botao.addEventListener('click', () => {
            const vagaId = Number(botao.dataset.id)

            renderPerfilVaga(vagaId)
        })
    })
}