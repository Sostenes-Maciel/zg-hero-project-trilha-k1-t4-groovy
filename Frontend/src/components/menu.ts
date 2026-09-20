import { renderCadastroCandidato } from '../service/cadastroCandidato.ts'
import { renderListaCandidatos } from '../view/listaCandidatos'
import { renderCadastroEmpresa } from '../service/cadastroEmpresa.ts'
import { renderListaEmpresas } from '../view/listaEmpresas'
import { renderListaVagas } from '../view/listaVagas'
import { renderViewCadastroVaga } from '../service/cadastroVaga'

export function configurarMenu(): void {
    document.querySelector<HTMLButtonElement>(
        '#menu-cadastro-candidato'
    )?.addEventListener('click', () => {
        renderCadastroCandidato()
    })

    document.querySelector<HTMLButtonElement>(
        '#menu-candidatos'
    )?.addEventListener('click', () => {
        renderListaCandidatos()
    })

    document.querySelector<HTMLButtonElement>(
        '#menu-cadastro-empresa'
    )?.addEventListener('click', () => {
        renderCadastroEmpresa()
    })

    document.querySelector<HTMLButtonElement>(
        '#menu-empresas'
    )?.addEventListener('click', () => {
        renderListaEmpresas()
    })

    document.querySelector<HTMLButtonElement>(
        '#menu-vagas'
    )?.addEventListener('click', () => {
        renderListaVagas()
    })

    document.querySelector<HTMLButtonElement>(
        '#menu-cadastro-vaga'
    )?.addEventListener('click', () => {
        renderViewCadastroVaga()
    })
}