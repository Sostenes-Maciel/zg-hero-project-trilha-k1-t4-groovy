import {renderListaEmpresas} from "../view/ListaEmpresas.ts";
import {renderCadastroCandidato} from '../service/CadastroCandidato.ts'
import {renderCadastroEmpresa} from '../service/CadastroEmpresa.ts'
import {renderListaCandidatos} from "../view/ListaCandidatos.ts";

const movimentoReduzido = window.matchMedia('(prefers-reduced-motion: reduce)').matches

function rolarParaApp(): void {
    requestAnimationFrame(() => {
        document.querySelector('#app')?.scrollIntoView({
            behavior: movimentoReduzido ? 'auto' : 'smooth',
            block: 'start'
        })
    })
}

const elementosRevelacao = document.querySelectorAll<HTMLElement>(
    '.revelar-scroll, .etapa-linketinder'
)

if (movimentoReduzido || !('IntersectionObserver' in window)) {
    elementosRevelacao.forEach(elemento => elemento.classList.add('visivel'))
} else {
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
            threshold: 0.1,
            rootMargin: '0px 0px -40px 0px'
        }
    )

    let indiceCard = 0
    elementosRevelacao.forEach(elemento => {
        if (elemento.matches('.etapa-linketinder')) {
            elemento.classList.add('revelar-scroll')
            elemento.style.transitionDelay = `${indiceCard * 120}ms`
            indiceCard += 1
        }
        observador.observe(elemento)
    })
}

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