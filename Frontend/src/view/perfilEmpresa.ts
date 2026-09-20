import type { Empresa } from '../model/Empresa'
import { BancodeDados } from '../repository/BancodeDados'

export function renderPerfilEmpresa(empresa: Empresa): void {
    const app = document.querySelector<HTMLDivElement>('#app')

    if (!app) {
        return
    }

    app.innerHTML = `
        <section>
            <h1>Perfil da Empresa</h1>

            <div>
                <h2>${empresa.nome}</h2>

                <p><strong>E-mail:</strong> ${empresa.email}</p>
                <p><strong>CNPJ:</strong> ${empresa.cnpj}</p>
                <p><strong>País:</strong> ${empresa.pais}</p>
                <p><strong>Estado:</strong> ${empresa.estado}</p>
                <p><strong>CEP:</strong> ${empresa.cep}</p>
                <p><strong>Descrição:</strong> ${empresa.descricao}</p>
                <p>
                    <strong>Competências:</strong>
                    ${empresa.competencias.join(', ')}
                </p>
            </div>

            <h2>Candidatos disponíveis</h2>

            <table>
                <thead>
                    <tr>
                        <th>Candidato</th>
                        <th>Competências</th>
                    </tr>
                </thead>

                <tbody id="lista-candidatos-empresa">
                </tbody>
            </table>

            <button id="voltar-empresa">Voltar</button>
        </section>
    `

    const lista = document.querySelector<HTMLTableSectionElement>(
        '#lista-candidatos-empresa'
    )

    if (!lista) {
        return
    }

    BancodeDados.candidatos.forEach((candidato, indice) => {
        const linha = document.createElement('tr')

        linha.innerHTML = `
            <td>Candidato #${indice + 1}</td>
            <td>${candidato.competencias.join(', ')}</td>
        `

        lista.appendChild(linha)
    })

    const botaoVoltar = document.querySelector<HTMLButtonElement>(
        '#voltar-empresa'
    )

    botaoVoltar?.addEventListener('click', () => {
        window.location.reload()
    })
}