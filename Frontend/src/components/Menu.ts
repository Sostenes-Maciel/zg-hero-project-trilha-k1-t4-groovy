import { renderCadastroCandidato } from '../service/CadastroCandidato.ts'
import { renderListaCandidatos } from '../view/ListaCandidatos.ts'
import { renderCadastroEmpresa } from '../service/CadastroEmpresa.ts'
import { renderListaEmpresas } from '../view/ListaEmpresas.ts'
import { renderLogin } from "../view/RenderLogin.ts"

function usuarioEstaLogado(): boolean {
    return !!(
        localStorage.getItem('usuarioLogado') &&
        localStorage.getItem('tipoUsuario')
    )
}

function atualizarBotaoLogin(): void {
    const botaoLogin = document.querySelector<HTMLButtonElement>('#menu-login')

    if (!botaoLogin) return

    botaoLogin.textContent = usuarioEstaLogado() ? 'Sair' : 'Login'
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
        '#menu-candidatos'
    )?.addEventListener('click', () => {
        renderListaCandidatos().catch((erro: unknown) => {
            console.error('Erro ao carregar lista de candidatos:', erro)
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
        '#menu-empresas'
    )?.addEventListener('click', () => {
        renderListaEmpresas().catch((erro: unknown) => {
            console.error('Erro ao carregar lista de empresas:', erro)
        })
    })

    document.querySelector<HTMLButtonElement>(
        '#menu-login'
    )?.addEventListener('click', () => {

        if (usuarioEstaLogado()) {
            localStorage.removeItem('usuarioLogado')
            localStorage.removeItem('tipoUsuario')

            atualizarBotaoLogin()

            renderLogin()

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