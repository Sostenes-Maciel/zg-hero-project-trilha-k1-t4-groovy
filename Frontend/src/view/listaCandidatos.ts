import { BancodeDados } from '../repository/BancodeDados'

export function renderListaCandidatos(): void {
    const app = document.querySelector<HTMLDivElement>('#app')

    if (!app) {
        return
    }

    const candidatos = BancodeDados.candidatos

    app.innerHTML = `
        <section>
            <h1>Candidatos</h1>

            <table>
                <thead>
                    <tr>
                        <th>Nome</th>
                        <th>E-mail</th>
                        <th>Idade</th>
                        <th>Estado</th>
                        <th>Competências</th>
                    </tr>
                </thead>

                <tbody id="lista-candidatos">
                </tbody>
            </table>
        </section>
    `

    const lista = document.querySelector<HTMLTableSectionElement>('#lista-candidatos')

    if (!lista) {
        return
    }

    candidatos.forEach(candidato => {
        const linha = document.createElement('tr')

        linha.innerHTML = `
            <td>${candidato.nome}</td>
            <td>${candidato.email}</td>
            <td>${candidato.idade}</td>
            <td>${candidato.estado}</td>
            <td>${candidato.competencias.join(', ')}</td>
        `

        lista.appendChild(linha)
    })
}