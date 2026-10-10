import { BancodeDados } from '../dao/BancodeDados'
import { renderPerfilVaga } from './PerfilVaga.ts'
import type { Candidato } from "../model/Candidato.ts"
import { calcularAfinidade } from "../service/CalcularAfinidade.ts"
import { mostrarAnimacaoMatch } from "./AnimacaoMatch.ts"

export function renderListaVagas(onVoltar?: () => void, candidato?: Candidato, animarTransicao: boolean = true): void {
    const app = document.querySelector<HTMLDivElement>('#app')
    if (!app) return

    renderizarTemplateBase(app, animarTransicao)
    renderizarLinhasVagas(candidato)
    configurarEventos(onVoltar, candidato)
}

function renderizarTemplateBase(app: HTMLDivElement, animarTransicao: boolean): void {
    app.innerHTML = `
        <section class="lista-vagas ${animarTransicao ? 'tela-com-transicao' : ''}">
            <h1>Vagas disponíveis</h1>
            
            <div class="filtro-vagas">
                <label for="input-filtro-vagas">Buscar vaga</label>
                <input
                    type="text"
                    id="input-filtro-vagas"
                    placeholder="Ex.: Java, Python, Tech Global..."
                >
            </div>

            <table class="tabela-vagas">
                <thead>
                    <tr>
                        <th>Vaga</th>
                        <th>Empresa</th>
                        <th>Afinidade</th>
                        <th>Ação</th>
                    </tr>
                </thead>
                <tbody id="lista-vagas-corpo"></tbody>
            </table>

            <button id="btn-voltar-candidato">Voltar</button>
        </section>
    `
}

function renderizarLinhasVagas(candidato?: Candidato): void {
    const lista = document.querySelector<HTMLTableSectionElement>('#lista-vagas-corpo')
    if (!lista) return

    const vagas = BancodeDados.vagas

    if (vagas.length === 0) {
        lista.innerHTML = `
            <tr>
                <td colspan="4" class="tabela-vazia">Nenhuma vaga cadastrada.</td>
            </tr>
        `
        return
    }

    vagas.forEach(vaga => {
        const linha = document.createElement('tr')
        linha.dataset.titulo = vaga.titulo.toLowerCase()

        const afinidade = candidato ? calcularAfinidade(candidato, vaga.empresa) : null
        const curtiu = candidato ? BancodeDados.candidatoCurtiuVaga(candidato, vaga) : false
        const temMatch = candidato ? BancodeDados.candidatoTemMatchComVaga(candidato, vaga) : false

        const iconeCurtida = temMatch ? '🤝' : curtiu ? '♥' : '♡'

        linha.innerHTML = `
            <td>${vaga.titulo}</td>
            <td>${temMatch ? vaga.empresa.nome : 'Match necessário para visualização'}</td>
            <td>${afinidade !== null ? `${afinidade}%` : '—'}</td>
            <td>
                <button
                    class="btn-curtir-vaga"
                    data-id="${vaga.id}"
                    title="${temMatch ? 'Match confirmado!' : curtiu ? 'Vaga curtida' : 'Curtir vaga'}"
                    ${temMatch ? 'disabled' : ''}
                >
                    ${iconeCurtida}
                </button>
            
                <button class="btn-perfil-vaga" data-id="${vaga.id}">
                    Ver vaga
                </button>
            </td>
        `
        lista.appendChild(linha)
    })
}

function configurarEventos(onVoltar?: () => void, candidato?: Candidato): void {
    configurarBotaoVoltar(onVoltar)
    configurarFiltroBusca()
    configurarBotoesCurtir(onVoltar, candidato)
    configurarBotoesPerfil(onVoltar, candidato)
}

function configurarBotaoVoltar(onVoltar?: () => void): void {
    const botaoVoltar = document.querySelector<HTMLButtonElement>('#btn-voltar-candidato')
    botaoVoltar?.addEventListener('click', () => onVoltar?.())
}

function configurarFiltroBusca(): void {
    const inputFiltro = document.querySelector<HTMLInputElement>('#input-filtro-vagas')
    const lista = document.querySelector<HTMLTableSectionElement>('#lista-vagas-corpo')

    inputFiltro?.addEventListener('input', () => {
        const termo = inputFiltro.value.trim().toLowerCase()
        const linhas = lista?.querySelectorAll<HTMLTableRowElement>('tr') ?? []

        linhas.forEach(linha => {
            const titulo = linha.dataset.titulo ?? ''
            linha.hidden = !titulo.includes(termo)
        })
    })
}

function configurarBotoesCurtir(onVoltar?: () => void, candidato?: Candidato): void {
    const botoesCurtir = document.querySelectorAll<HTMLButtonElement>('.btn-curtir-vaga')

    botoesCurtir.forEach(botao => {
        botao.addEventListener('click', () => {
            if (!candidato) return

            const vagaId = Number(botao.dataset.id)
            const vaga = BancodeDados.vagas.find(v => v.id === vagaId)
            if (!vaga) return

            BancodeDados.curtirVaga(candidato, vaga)

            const criouMatch = BancodeDados.candidatoTemMatchComVaga(candidato, vaga)
            const app = document.querySelector<HTMLDivElement>('#app')

            app?.classList.add('sem-transicao')
            renderListaVagas(onVoltar, candidato, false)

            requestAnimationFrame(() => {
                app?.classList.remove('sem-transicao')
            })

            if (!criouMatch) {
                requestAnimationFrame(() => {
                    const botaoAtual = document.querySelector<HTMLButtonElement>(`.btn-curtir-vaga[data-id="${vaga.id}"]`)
                    botaoAtual?.classList.add('curtida-animada')
                })
            } else {
                mostrarAnimacaoMatch()
            }
        })
    })
}

function configurarBotoesPerfil(onVoltar?: () => void, candidato?: Candidato): void {
    const botoesPerfil = document.querySelectorAll<HTMLButtonElement>('.btn-perfil-vaga')

    botoesPerfil.forEach(botao => {
        botao.addEventListener('click', () => {
            const vagaId = Number(botao.dataset.id)

            renderPerfilVaga(vagaId, () => {
                renderListaVagas(onVoltar, candidato)
            }, candidato)
        })
    })
}