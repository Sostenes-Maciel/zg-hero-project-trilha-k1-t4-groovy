import { BancodeDados } from '../repository/BancodeDados'
import { renderPerfilEmpresa } from './perfilEmpresa'

export function renderListaEmpresas(): void {
    const app = document.querySelector<HTMLDivElement>('#app')

    if (!app) {
        return
    }

    const empresas = BancodeDados.empresas

    app.innerHTML = `
        <section>
            <h1>Empresas</h1>

            <table>
                <thead>
                    <tr>
                        <th>Empresa</th>
                        <th>País</th>
                        <th>Estado</th>
                        <th>Competências</th>
                        <th>Ação</th>
                    </tr>
                </thead>

                <tbody id="lista-empresas"></tbody>
            </table>
        </section>
    `

    const lista = document.querySelector<HTMLTableSectionElement>(
        '#lista-empresas'
    )

    if (!lista) {
        return
    }

    empresas.forEach(empresa => {
        const linha = document.createElement('tr')

        linha.innerHTML = `
            <td>${empresa.nome}</td>
            <td>${empresa.pais}</td>
            <td>${empresa.estado}</td>
            <td>${empresa.competencias.join(', ')}</td>
            <td>
                <button class="btn-perfil-empresa" data-cnpj="${empresa.cnpj}">
                    Ver perfil
                </button>
            </td>
        `

        lista.appendChild(linha)
    })

    const botoesPerfil = document.querySelectorAll<HTMLButtonElement>(
        '.btn-perfil-empresa'
    )

    botoesPerfil.forEach(botao => {
        botao.addEventListener('click', () => {
            const cnpj = botao.dataset.cnpj

            const empresa = BancodeDados.empresas.find(
                empresa => empresa.cnpj === cnpj
            )

            if (!empresa) {
                console.error('Empresa não encontrada.')
                return
            }

            renderPerfilEmpresa(empresa)
        })
    })
}