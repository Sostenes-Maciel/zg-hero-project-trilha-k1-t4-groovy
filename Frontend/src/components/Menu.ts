import { renderCadastroCandidato } from '../service/CadastroCandidato.ts'
import { renderCadastroEmpresa } from '../service/CadastroEmpresa.ts'
import { renderLogin } from "../view/RenderLogin.ts"
import { renderPerfilCandidato } from '../view/PerfilCandidato.ts'
import { renderPerfilEmpresa } from '../view/PerfilEmpresa.ts'
import type { Candidato } from '../model/Candidato.ts'
import type { Empresa } from '../model/Empresa.ts'

function obterSessaoAtiva(): { tipo: 'candidato'; usuario: Candidato } | { tipo: 'empresa'; usuario: Empresa } | null {
    const usuarioSalvo = localStorage.getItem('usuarioLogado')
    const tipoUsuario = localStorage.getItem('tipoUsuario')

    if (!usuarioSalvo || (tipoUsuario !== 'candidato' && tipoUsuario !== 'empresa')) {
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
        return null
    } catch {
        return null
    }
}

function atualizarBotaoLogin(): void {
    const botaoLogin = document.querySelector<HTMLButtonElement>('#menu-login')

    if (!botaoLogin) return

    botaoLogin.textContent = 'Login'
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

    document.querySelector<HTMLButtonElement>(
        '#menu-login'
    )?.addEventListener('click', async () => {
        const sessao = obterSessaoAtiva()
        if (sessao?.tipo === 'candidato') {
            renderPerfilCandidato(sessao.usuario)
        } else if (sessao?.tipo === 'empresa') {
            await renderPerfilEmpresa(sessao.usuario)
        } else {
            renderLogin()
        }

        const appElement = document.getElementById('app')

        appElement?.scrollIntoView({
            behavior: 'smooth',
            block: 'start'
        })
    })

    window.addEventListener('app:logout', () => {
        renderLogin()

        document.querySelector('#app')?.scrollIntoView({
            behavior: 'smooth',
            block: 'start'
        })
    })

    window.addEventListener('auth-change', atualizarBotaoLogin)

    atualizarBotaoLogin()
}