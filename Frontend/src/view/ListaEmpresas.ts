import { confirmarExclusao } from "../components/Confirmacao.ts"
import { renderPerfilEmpresa } from "./PerfilEmpresa.ts" // Certifique-se de que este arquivo existe!
import type { Empresa } from '../model/Empresa'

export async function renderListaEmpresas(mensagemSucesso?: string): Promise<void> {
    const app = document.querySelector<HTMLDivElement>('#app')
    if (!app) return

    renderizarTabelaBase(app, mensagemSucesso)
    await renderizarLinhasEmpresas()
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
                        <th>Nome da Empresa</th>
                        <th>CNPJ</th>
                        <th>Estado</th>
                        <th>Ação</th>
                    </tr>
                </thead>
                <tbody id="lista-empresas">
                    <tr><td colspan="4">Carregando empresas...</td></tr>
                </tbody>
            </table>
        </section>
    `
}

async function renderizarLinhasEmpresas(): Promise<void> {
    const lista = document.querySelector<HTMLTableSectionElement>('#lista-empresas')
    if (!lista) return

    try {
        const resposta = await fetch('http://localhost:8080/api/empresas')

        if (!resposta.ok) {
            lista.innerHTML = '<tr><td colspan="4" class="tabela-vazia">Erro ao carregar as empresas do servidor.</td></tr>'
            return
        }

        const dadosDoServidor = await resposta.json()

        // Usamos o mesmo macete do "dados" que descobrimos no Candidato!
        const empresas: Empresa[] = dadosDoServidor.dados ? dadosDoServidor.dados : dadosDoServidor

        if (!Array.isArray(empresas) || empresas.length === 0) {
            lista.innerHTML = '<tr><td colspan="4" class="tabela-vazia">Nenhuma empresa cadastrada no banco de dados.</td></tr>'
            return
        }

        lista.innerHTML = ''

        empresas.forEach(empresa => {
            const linha = document.createElement('tr')

            linha.innerHTML = `
                <td class="nome-hover" data-email="${empresa.email}">
                    ${empresa.nome}
                </td>
                <td>${empresa.cnpj}</td>
                <td>${empresa.estado}</td>
                <td>
                    <button class="btn-perfil" data-cnpj="${empresa.cnpj}">Ver perfil</button>
                    <button class="btn-excluir" data-cnpj="${empresa.cnpj}">Excluir</button>
                </td>
            `
            lista.appendChild(linha)
        })

        configurarEventosBotoes()

    } catch (erro) {
        lista.innerHTML = '<tr><td colspan="4" class="tabela-vazia">Falha na conexão com o servidor.</td></tr>'
        console.error("Erro na API de empresas:", erro)
    }
}

function configurarEventosBotoes(): void {
    const botoesPerfil = document.querySelectorAll<HTMLButtonElement>('.btn-perfil')
    const botoesExcluir = document.querySelectorAll<HTMLButtonElement>('.btn-excluir')

    botoesPerfil.forEach(botao => {
        botao.addEventListener('click', () => abrirPerfil(botao.dataset.cnpj))
    })

    botoesExcluir.forEach(botao => {
        botao.addEventListener('click', () => processarExclusao(botao.dataset.cnpj))
    })
}

async function abrirPerfil(cnpj?: string): Promise<void> {
    if (!cnpj) return

    try {
        // Limpa a string deixando apenas os números
        const cnpjLimpo = cnpj.replace(/\D/g, '')

        const resposta = await fetch(`http://localhost:8080/api/empresas?cnpj=${cnpjLimpo}`)

        if (resposta.ok) {
            const dadosDoServidor = await resposta.json()
            const empresa: Empresa = dadosDoServidor.dados ? dadosDoServidor.dados : dadosDoServidor
            renderPerfilEmpresa(empresa)
        } else {
            console.error('Empresa não encontrada no banco.')
        }
    } catch (erro) {
        console.error('Erro de conexão ao abrir perfil da empresa:', erro)
    }
}

async function processarExclusao(cnpj?: string): Promise<void> {
    if (!cnpj) {
        console.error('CNPJ da empresa não encontrado.')
        return
    }

    const confirmar = await confirmarExclusao(
        'Excluir empresa?',
        'Essa ação não poderá ser desfeita e removerá todas as vagas publicadas por esta empresa.'
    )

    if (!confirmar) return

    try {
        const resposta = await fetch(`http://localhost:8080/api/empresas?cnpj=${cnpj}`, {
            method: 'DELETE'
        })

        if (resposta.ok) {
            renderListaEmpresas('Empresa excluída com sucesso do banco de dados!')
        } else {
            const erroApi = await resposta.json()
            window.alert(`Erro do Servidor: ${erroApi.mensagem}`)
        }

    } catch (erro) {
        console.error('Erro ao excluir empresa:', erro)
        window.alert('Não foi possível excluir a empresa. Servidor offline.')
    }
}