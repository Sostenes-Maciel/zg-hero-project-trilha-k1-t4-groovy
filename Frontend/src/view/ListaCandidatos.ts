import { renderPerfilCandidato } from "./PerfilCandidato.ts"
import { confirmarExclusao } from "../components/Confirmacao.ts"
import type { Candidato } from '../model/Candidato'

export async function renderListaCandidatos(mensagemSucesso?: string): Promise<void> {
    const app = document.querySelector<HTMLDivElement>('#app')
    if (!app) return

    renderizarTabelaBase(app, mensagemSucesso)
    await renderizarLinhasCandidatos()
}

function renderizarTabelaBase(app: HTMLDivElement, mensagemSucesso?: string): void {
    app.innerHTML = `
        <section class="tela-com-transicao">
            <h1>Candidatos</h1>
            <p id="mensagem" class="${mensagemSucesso ? 'mensagem-sucesso' : ''}">
                ${mensagemSucesso ?? ''}
            </p>

            <table>
                <thead>
                    <tr>
                        <th>Nome</th>
                        <th>Estado</th>
                        <th>Competências</th>
                        <th>Ação</th>
                    </tr>
                </thead>
                <tbody id="lista-candidatos">
                    <tr><td colspan="4">Carregando candidatos...</td></tr>
                </tbody>
            </table>
        </section>
    `
}

async function renderizarLinhasCandidatos(): Promise<void> {
    const lista = document.querySelector<HTMLTableSectionElement>('#lista-candidatos')
    if (!lista) return

    try {
        const resposta = await fetch('http://localhost:8080/api/candidatos')

        if (!resposta.ok) {
            lista.innerHTML = '<tr><td colspan="4" class="tabela-vazia">Erro ao carregar os candidatos do servidor.</td></tr>'
            return
        }

        const dadosDoServidor = await resposta.json()

        const candidatos: Candidato[] = dadosDoServidor.dados ? dadosDoServidor.dados : dadosDoServidor
        console.log("Chegou do backend:", candidatos)

        if (!Array.isArray(candidatos) || candidatos.length === 0) {
            lista.innerHTML = '<tr><td colspan="4" class="tabela-vazia">Nenhum candidato encontrado.</td></tr>'
            return
        }

        lista.innerHTML = ''

        candidatos.forEach(candidato => {
            const linha = document.createElement('tr')

            linha.innerHTML = `
                <td class="nome-hover" data-email="${candidato.email}" data-descricao="${candidato.descricao}">
                    ${candidato.nome}
                </td>
                <td>${candidato.estado}</td>
                <td>${candidato.competencias.join(', ')}</td>
                <td>
                    <button class="btn-perfil" data-cpf="${candidato.cpf}">Ver perfil</button>
                    <button class="btn-excluir" data-cpf="${candidato.cpf}">Excluir</button>
                </td>
            `
            lista.appendChild(linha)
        })


        configurarEventosBotoes()

    } catch (erro) {
        lista.innerHTML = '<tr><td colspan="4" class="tabela-vazia">Falha na conexão com o servidor.</td></tr>'
        console.error("Erro na API:", erro)
    }
}

function configurarEventosBotoes(): void {
    const botoesPerfil = document.querySelectorAll<HTMLButtonElement>('.btn-perfil')
    const botoesExcluir = document.querySelectorAll<HTMLButtonElement>('.btn-excluir')

    botoesPerfil.forEach(botao => {
        botao.addEventListener('click', () => abrirPerfil(botao.dataset.cpf))
    })

    botoesExcluir.forEach(botao => {
        botao.addEventListener('click', () => processarExclusao(botao.dataset.cpf))
    })
}

async function abrirPerfil(cpf?: string): Promise<void> {
    if (!cpf) return

    try {
        const resposta = await fetch(`http://localhost:8080/api/candidatos?cpf=${cpf}`)

        if (resposta.ok) {
            const candidato: Candidato = await resposta.json()
            renderPerfilCandidato(candidato)
        } else {
            console.error('Candidato não encontrado no banco.')
        }
    } catch (erro) {
        console.error('Erro de conexão ao abrir perfil:', erro)
    }
}

async function processarExclusao(cpf?: string): Promise<void> {
    if (!cpf) {
        console.error('CPF do candidato não encontrado.')
        return
    }

    const confirmar = await confirmarExclusao(
        'Excluir candidato?',
        'Essa ação não poderá ser desfeita e removerá os matches vinculados.'
    )

    if (!confirmar) return

    try {
        const resposta = await fetch(`http://localhost:8080/api/candidatos?cpf=${cpf}`, {
            method: 'DELETE'
        })

        if (resposta.ok) {
            renderListaCandidatos('Candidato excluído com sucesso do banco de dados!')
        } else {
            const erroApi = await resposta.json()
            window.alert(`Erro do Servidor: ${erroApi.mensagem}`)
        }

    } catch (erro) {
        console.error('Erro ao excluir candidato:', erro)
        window.alert('Não foi possível excluir o candidato. Servidor offline.')
    }
}