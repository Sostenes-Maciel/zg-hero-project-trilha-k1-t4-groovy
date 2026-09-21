import { renderCadastroCandidato } from '../service/cadastroCandidato.ts'
import { renderListaCandidatos } from '../view/listaCandidatos'
import { renderCadastroEmpresa } from '../service/cadastroEmpresa.ts'
import { renderListaEmpresas } from '../view/listaEmpresas'

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
}