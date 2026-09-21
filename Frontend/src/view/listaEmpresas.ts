import { BancodeDados } from '../repository/BancodeDados'
import renderPerfilEmpresa from './perfilEmpresa'

export function renderListaEmpresas(mensagemSucesso?: string): void {
    const app = document.querySelector<HTMLDivElement>('#app')

    if (!app) {
        return
    }

    const empresas = BancodeDados.empresas

    app.innerHTML = `
        <section>
            <h1>Empresas</h1>
            <p id="mensagem"></p>

            <table>
                <thead>
                    <tr>
                        <th>Empresa</th>
                        <th>País</th>
                        <th>Competências</th>
                        <th>Ação</th>
                    </tr>
                </thead>

                <tbody id="lista-empresas"></tbody>
            </table>
        </section>
    `
    const mensagem = document.querySelector<HTMLParagraphElement>('#mensagem')

    if (mensagemSucesso && mensagem) {
        mensagem.textContent = mensagemSucesso
    }

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
            <td>${empresa.competencias.join(', ')}</td>
            <td>
                <button class="btn-perfil-empresa" data-cnpj="${empresa.cnpj}">
                    Ver perfil
                </button>
                <button class="btn-excluir-empresa" data-cnpj="${empresa.cnpj}">
                    Excluir
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
    const botoesExcluir = document.querySelectorAll<HTMLButtonElement>(
        '.btn-excluir-empresa'
    )

    botoesExcluir.forEach(botao => {
        botao.addEventListener('click', () => {
            const cnpj = botao.dataset.cnpj

            if (!cnpj) {
                console.error('CNPJ da empresa não encontrado.')
                return
            }

            const confirmar = window.confirm(
                'Tem certeza que deseja excluir esta empresa?'
            )

            if (!confirmar) {
                return
            }

            try {
                BancodeDados.excluirEmpresa(cnpj)
                renderListaEmpresas('Empresa excluída com sucesso!')
            } catch (erro) {
                console.error('Erro ao excluir empresa:', erro)

                window.alert(
                    'Não foi possível excluir a empresa. Tente novamente.'
                )
            }
        })
    })
}