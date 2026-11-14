import { renderCadastroCandidato } from '../service/CadastroCandidato.ts'
import { renderCadastroEmpresa } from '../service/CadastroEmpresa.ts'
import { renderViewCadastroVaga } from '../service/CadastroVaga.ts'
import { renderLogin } from "../view/RenderLogin.ts"
import { renderPerfilCandidato } from '../view/PerfilCandidato.ts'
import { renderPerfilEmpresa } from '../view/PerfilEmpresa.ts'
import { renderListaVagas } from '../view/ListaVagas.ts'
import { renderListaMatchesCandidato } from '../view/ListaMatchesCandidato.ts'
import { renderListaMatchesEmpresa } from '../view/ListaMatchesEmpresa.ts'
import { confirmarExclusao } from './Confirmacao.ts'
import type { Candidato } from '../model/Candidato.ts'
import type { Empresa } from '../model/Empresa.ts'
import type { RecrutadorSessao } from '../model/Recrutador.ts'
import { renderCadastroRecrutador } from '../view/CadastroRecrutador.ts'
import { renderPerfilRecrutador } from '../view/PerfilRecrutador.ts'

function obterSessaoAtiva(): { tipo: 'candidato'; usuario: Candidato } | { tipo: 'empresa'; usuario: Empresa } | { tipo: 'recrutador'; usuario: RecrutadorSessao } | null {
    const usuarioSalvo = localStorage.getItem('usuarioLogado')
    const tipoUsuario = localStorage.getItem('tipoUsuario')

    if (!usuarioSalvo || !['candidato', 'empresa', 'recrutador'].includes(tipoUsuario ?? '')) {
        return null
    }

    try {
        const usuario: unknown = JSON.parse(usuarioSalvo)
        if (typeof usuario !== 'object' || usuario === null) return null

        if (tipoUsuario === 'candidato' && 'cpf' in usuario) {
            return { tipo: 'candidato', usuario: usuario as Candidato }
        }
        if (tipoUsuario === 'empresa' && 'cnpj' in usuario) {
            return { tipo: 'empresa', usuario: usuario as Empresa }
        }
        if (tipoUsuario === 'recrutador' && 'areaAtuacao' in usuario) {
            return { tipo: 'recrutador', usuario: usuario as RecrutadorSessao }
        }
        return null
    } catch {
        return null
    }
}

function fecharMenuConta(recolherAteSairDoHover = false): void {
    const menuConta = document.querySelector<HTMLElement>('#conta-menu')
    const botaoConta = document.querySelector<HTMLButtonElement>('#menu-conta-trigger')
    menuConta?.classList.remove('aberto')
    menuConta?.classList.toggle('recolhido', recolherAteSairDoHover)
    botaoConta?.setAttribute('aria-expanded', 'false')
}

function atualizarMenuConta(): void {
    const sessao = obterSessaoAtiva()
    const botaoLogin = document.querySelector<HTMLButtonElement>('#menu-login')
    const menuConta = document.querySelector<HTMLElement>('#conta-menu')
    const nomeUsuario = document.querySelector<HTMLElement>('#menu-nome-usuario')
    const opcoes = document.querySelector<HTMLDivElement>('#menu-conta-opcoes')

    if (!botaoLogin || !menuConta || !nomeUsuario || !opcoes) return

    botaoLogin.hidden = Boolean(sessao)
    menuConta.hidden = !sessao
    fecharMenuConta()

    if (!sessao) {
        opcoes.replaceChildren()
        return
    }

    nomeUsuario.textContent = sessao.usuario.nome
    const itens = sessao.tipo === 'candidato'
        ? [
            ['perfil', 'Meu perfil'],
            ['vagas', 'Vagas disponíveis'],
            ['matches', 'Meus matches'],
            ['editar', 'Editar perfil'],
            ['excluir', 'Excluir perfil'],
            ['sair', 'Sair']
        ]
        : sessao.tipo === 'empresa' ? [
            ['perfil', 'Meu perfil'],
            ['vaga', 'Cadastrar vaga'],
            ['recrutador', 'Cadastrar recrutador'],
            ['matches', 'Meus matches'],
            ['editar', 'Editar perfil'],
            ['excluir', 'Excluir perfil'],
            ['sair', 'Sair']
        ] : [
            ['perfil', 'Meu perfil'],
            ['sair', 'Sair']
        ]

    opcoes.innerHTML = itens.map(([acao, titulo]) =>
        `<button type="button" role="menuitem" data-acao="${acao}">${titulo}</button>`
    ).join('')
}

function limparSessao(): void {
    localStorage.removeItem('usuarioLogado')
    localStorage.removeItem('tipoUsuario')
    window.dispatchEvent(new Event('auth-change'))
    window.dispatchEvent(new Event('app:logout'))
}

async function excluirPerfil(sessao: NonNullable<ReturnType<typeof obterSessaoAtiva>>): Promise<void> {
    const candidato = sessao.tipo === 'candidato'
    const confirmou = await confirmarExclusao(
        candidato ? 'Excluir perfil de candidato?' : 'Excluir perfil da empresa?',
        candidato
            ? 'Essa ação não poderá ser desfeita e removerá seu perfil e dados vinculados.'
            : 'Essa ação não poderá ser desfeita e removerá as vagas e dados vinculados à empresa.'
    )
    if (!confirmou) return

    const documento = candidato
        ? (sessao.usuario as Candidato).cpf
        : (sessao.usuario as Empresa).cnpj
    const parametro = candidato ? 'cpf' : 'cnpj'
    const endpoint = candidato ? 'candidatos' : 'empresas'

    try {
        const resposta = await fetch(
            `http://localhost:8080/api/${endpoint}?${parametro}=${encodeURIComponent(documento)}`,
            { method: 'DELETE' }
        )
        if (!resposta.ok) {
            const erroApi = await resposta.json()
            throw new Error(erroApi.mensagem ?? `Falha ao excluir perfil (HTTP ${resposta.status}).`)
        }
        limparSessao()
    } catch (erro) {
        console.error('Erro ao excluir perfil:', erro)
        window.alert(erro instanceof Error ? erro.message : 'Não foi possível excluir o perfil.')
    }
}

async function executarAcaoMenu(acao: string): Promise<void> {
    const sessao = obterSessaoAtiva()
    if (!sessao) {
        atualizarMenuConta()
        return
    }

    fecharMenuConta(true)
    document.querySelector<HTMLButtonElement>('#menu-conta-trigger')?.focus()

    switch (acao) {
        case 'perfil':
            if (sessao.tipo === 'candidato') renderPerfilCandidato(sessao.usuario)
            else if (sessao.tipo === 'empresa') await renderPerfilEmpresa(sessao.usuario)
            else await renderPerfilRecrutador(sessao.usuario)
            break
        case 'vagas':
            if (sessao.tipo === 'candidato') {
                renderListaVagas(sessao.usuario)
            }
            break
        case 'matches':
            if (sessao.tipo === 'candidato') {
                renderListaMatchesCandidato(sessao.usuario)
            } else if (sessao.tipo === 'empresa') {
                await renderListaMatchesEmpresa(sessao.usuario)
            }
            break
        case 'vaga':
            if (sessao.tipo === 'empresa') {
                await renderViewCadastroVaga(sessao.usuario, mensagem => void renderPerfilEmpresa(sessao.usuario, mensagem))
            }
            break
        case 'recrutador':
            if (sessao.tipo === 'empresa') await renderCadastroRecrutador()
            break
        case 'editar':
            if (sessao.tipo === 'candidato') await renderCadastroCandidato(sessao.usuario)
            else if (sessao.tipo === 'empresa') await renderCadastroEmpresa(sessao.usuario)
            else await renderPerfilRecrutador(sessao.usuario)
            break
        case 'excluir':
            await excluirPerfil(sessao)
            break
        case 'sair':
            limparSessao()
            break
    }

    document.querySelector('#app')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

export function configurarMenu(): void {
    document.querySelector<HTMLButtonElement>(
        '#menu-cadastro-candidato'
    )?.addEventListener('click', () => {
        renderCadastroCandidato().catch((erro: unknown) => {
            console.error('Erro ao carregar caandidato:', erro)
        })
    })

    document.querySelector<HTMLButtonElement>(
        '#menu-cadastro-empresa'
    )?.addEventListener('click', () => {
        renderCadastroEmpresa().catch((erro: unknown) => {
            console.error('Erro ao cadastrar empresa:', erro)
        })
    })

    document.querySelector<HTMLButtonElement>('#menu-login')?.addEventListener('click', () => {
        renderLogin()
        document.querySelector('#app')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
    })

    const menuConta = document.querySelector<HTMLElement>('#conta-menu')
    const botaoConta = document.querySelector<HTMLButtonElement>('#menu-conta-trigger')
    const opcoesConta = document.querySelector<HTMLDivElement>('#menu-conta-opcoes')

    botaoConta?.addEventListener('click', () => {
        menuConta?.classList.remove('recolhido')
        const aberto = menuConta?.classList.toggle('aberto') ?? false
        botaoConta.setAttribute('aria-expanded', String(aberto))
    })

    menuConta?.addEventListener('pointerleave', () => {
        menuConta.classList.remove('recolhido')
    })

    opcoesConta?.addEventListener('click', evento => {
        const botao = (evento.target as HTMLElement).closest<HTMLButtonElement>('[data-acao]')
        if (botao?.dataset.acao) void executarAcaoMenu(botao.dataset.acao)
    })

    document.addEventListener('click', evento => {
        if (menuConta && !menuConta.contains(evento.target as Node)) fecharMenuConta()
    })

    document.addEventListener('keydown', evento => {
        const menuEstavaEmUso = menuConta?.classList.contains('aberto')
            || menuConta?.contains(document.activeElement)
        if (evento.key === 'Escape' && menuEstavaEmUso) {
            fecharMenuConta(true)
            botaoConta?.focus()
        }
    })

    window.addEventListener('app:logout', () => {
        renderLogin()

        document.querySelector('#app')?.scrollIntoView({
            behavior: 'smooth',
            block: 'start'
        })
    })

    window.addEventListener('auth-change', atualizarMenuConta)

    atualizarMenuConta()
}