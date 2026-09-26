import { renderCadastroCandidato } from '../service/CadastroCandidato.ts'
import { renderListaCandidatos } from '../view/ListaCandidatos.ts'
import { renderCadastroEmpresa } from '../service/CadastroEmpresa.ts'
import { renderListaEmpresas } from '../view/ListaEmpresas.ts'

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