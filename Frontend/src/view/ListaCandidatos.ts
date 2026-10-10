import { BancodeDados } from '../dao/BancodeDados'
import { renderPerfilCandidato } from "./PerfilCandidato.ts"
import { confirmarExclusao } from "../components/Confirmacao.ts"

export function renderListaCandidatos(mensagemSucesso?: string): void {
    const app = document.querySelector<HTMLDivElement>('#app')
    if (!app) return

    renderizarTabelaBase(app, mensagemSucesso)
    renderizarLinhasCandidatos()
    configurarEventosBotoes()
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
                    <!-- Linhas injetadas via JavaScript -->
                </tbody>
            </table>
        </section>
    `
}

function renderizarLinhasCandidatos(): void {
    const lista = document.querySelector<HTMLTableSectionElement>('#lista-candidatos')
    if (!lista) return

    const candidatos = BancodeDados.candidatos

    if (candidatos.length === 0) {
        lista.innerHTML = '<tr><td colspan="4" class="tabela-vazia">Nenhum candidato cadastrado.</td></tr>'
        return
    }

    candidatos.forEach(candidato => {
        const linha = document.createElement('tr')

        linha.innerHTML = `
            <td
                class="nome-hover"
                data-email="${candidato.email}"
                data-descricao="${candidato.descricao}"
            >
                ${candidato.nome}
            </td>
            <td>${candidato.estado}</td>
            <td>${candidato.competencias.join(', ')}</td>
            <td>
                <button class="btn-perfil" data-cpf="${candidato.cpf}">
                    Ver perfil
                </button>
                <button class="btn-excluir" data-cpf="${candidato.cpf}">
                    Excluir
                </button>
            </td>
        `
        lista.appendChild(linha)
    })
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

function abrirPerfil(cpf?: string): void {
    if (!cpf) return

    const candidato = BancodeDados.candidatos.find(c => c.cpf === cpf)
    if (!candidato) {
        console.error('Candidato não encontrado.')
        return
    }

    renderPerfilCandidato(candidato)
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
          BancodeDados.excluirCandidato(cpf)

        renderListaCandidatos('Candidato excluído com sucesso!')

    } catch (erro) {
        console.error('Erro ao excluir candidato:', erro)
        window.alert('Não foi possível excluir o candidato. Tente novamente.')
    }
}