import { BancodeDados } from '../dao/BancodeDados'
import renderPerfilEmpresa from './PerfilEmpresa.ts'
import { confirmarExclusao } from "../components/Confirmacao.ts"

export function renderListaEmpresas(mensagemSucesso?: string): void {
    const app = document.querySelector<HTMLDivElement>('#app')
    if (!app) return

    renderizarTabelaBase(app, mensagemSucesso)
    renderizarLinhasEmpresas()
    configurarEventosBotoes()
}

function renderizarTabelaBase(app: HTMLDivElement, mensagemSucesso?: string): void {
    app.innerHTML = `
        <section class="tela-com-transicao">
            <h1>Empresas</h1>
            <p id="mensagem" class="${mensagemSucesso ? 'mensagem-sucesso' : ''}">
                ${mensagemSucesso ?? ''}
            </p>

            <table>
                <thead>
                    <tr>
                        <th>Empresa</th>
                        <th>País</th>
                        <th>Competências</th>
                        <th>Ação</th>
                    </tr>
                </thead>
                <tbody id="lista-empresas">
                </tbody>
            </table>
        </section>
    `
}

function renderizarLinhasEmpresas(): void {
    const lista = document.querySelector<HTMLTableSectionElement>('#lista-empresas')
    if (!lista) return

    const empresas = BancodeDados.empresas

    if (empresas.length === 0) {
        lista.innerHTML = '<tr><td colspan="4" class="tabela-vazia">Nenhuma empresa cadastrada.</td></tr>'
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
}

function configurarEventosBotoes(): void {
    const botoesPerfil = document.querySelectorAll<HTMLButtonElement>('.btn-perfil-empresa')
    const botoesExcluir = document.querySelectorAll<HTMLButtonElement>('.btn-excluir-empresa')

    botoesPerfil.forEach(botao => {
        botao.addEventListener('click', () => abrirPerfil(botao.dataset.cnpj))
    })

    botoesExcluir.forEach(botao => {
        botao.addEventListener('click', () => processarExclusao(botao.dataset.cnpj))
    })
}

function abrirPerfil(cnpj?: string): void {
    if (!cnpj) return

    const empresa = BancodeDados.empresas.find(e => e.cnpj === cnpj)
    if (!empresa) {
        console.error('Empresa não encontrada.')
        return
    }

    renderPerfilEmpresa(empresa)
}

async function processarExclusao(cnpj?: string): Promise<void> {
    if (!cnpj) {
        console.error('CNPJ da empresa não encontrado.')
        return
    }

    const confirmar = await confirmarExclusao(
        'Excluir empresa?',
        'Essa ação não poderá ser desfeita e removerá todas as vagas e matches vinculados.'
    )

    if (!confirmar) return

    try {
        BancodeDados.excluirEmpresa(cnpj)
        renderListaEmpresas('Empresa excluída com sucesso!')
    } catch (erro) {
        console.error('Erro ao excluir empresa:', erro)
        window.alert('Não foi possível excluir a empresa. Tente novamente.')
    }
}