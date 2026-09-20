import { BancodeDados } from '../repository/BancodeDados'

export function renderListaVagas(): void {
    const app = document.querySelector<HTMLDivElement>('#app')

    if (!app) {
        return
    }

    app.innerHTML = `
        <section>
            <h1>Vagas disponíveis</h1>

            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Vaga</th>
                        <th>Empresa</th>
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
                <td colspan="3">Nenhuma vaga cadastrada.</td>
            </tr>
        `

        return
    }

    BancodeDados.vagas.forEach(vaga => {
        const linha = document.createElement('tr')

        linha.innerHTML = `
            <td>${vaga.id}</td>
            <td>${vaga.titulo}</td>
            <td>${vaga.empresa.nome}</td>
        `

        lista.appendChild(linha)
    })
}