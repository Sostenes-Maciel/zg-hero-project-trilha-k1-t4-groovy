import {renderListaEmpresas} from "../view/ListaEmpresas.ts";
import {renderCadastroCandidato} from '../service/CadastroCandidato.ts'
import {renderCadastroEmpresa} from '../service/CadastroEmpresa.ts'
import {renderListaCandidatos} from "../view/ListaCandidatos.ts";

function rolarParaApp(): void {
    requestAnimationFrame(() => {
        document.querySelector('#app')?.scrollIntoView({
            behavior: 'smooth',
            block: 'start'
        })
    })
}

const elementosRevelacao = document.querySelectorAll<HTMLElement>(
    '.revelar-scroll'
)

const observador = new IntersectionObserver(
    entradas => {
        entradas.forEach(entrada => {
            if (entrada.isIntersecting) {
                entrada.target.classList.add('visivel')
                observador.unobserve(entrada.target)
            }
        })
    },
    {
        threshold: 0.15
    }
)

elementosRevelacao.forEach(elemento => {
    observador.observe(elemento)
})

document
    .querySelector<HTMLButtonElement>('#menu-cadastro-candidato-hero')
    ?.addEventListener('click', async () => {
        await renderCadastroCandidato()
        rolarParaApp()
    })

document
    .querySelector<HTMLButtonElement>('#menu-cadastro-empresa-hero')
    ?.addEventListener('click', async () => {
        await renderCadastroEmpresa()
        rolarParaApp()
    })

document
    .querySelector<HTMLButtonElement>('#menu-candidatos')
    ?.addEventListener('click', () => {
        renderListaCandidatos()
        rolarParaApp()
    })

document
    .querySelector<HTMLButtonElement>('#menu-empresas')
    ?.addEventListener('click', () => {
        renderListaEmpresas()
        rolarParaApp()
    })