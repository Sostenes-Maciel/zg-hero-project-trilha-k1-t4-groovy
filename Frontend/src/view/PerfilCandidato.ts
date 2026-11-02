import type { Candidato } from '../model/Candidato'
import { consultarEnderecoPorCep, formatarEndereco } from '../service/ConsultaCep.ts'

export function renderPerfilCandidato(candidato: Candidato): void {
    const app = document.querySelector<HTMLDivElement>('#app')
    if (!app) return

    renderizarTemplatePerfil(app, candidato)
    void preencherEnderecoPerfil(candidato.cep)
}

function renderizarTemplatePerfil(app: HTMLDivElement, candidato: Candidato): void {
    const competencias = Array.isArray(candidato.competencias) ? candidato.competencias : []
    app.innerHTML = `
        <section class="perfil-candidato tela-com-transicao">
            <h1>Perfil do Candidato</h1>

            <div class="card-perfil">
                <header class="perfil-cabecalho">
                    <h2>${escaparHtml(candidato.nome)}</h2>
                    <p class="perfil-subtitulo">Candidato</p>
                </header>

                <div class="perfil-grade">
                    <section class="perfil-bloco">
                        <h3>Sobre</h3>
                        <p>${escaparHtml(candidato.descricao || 'Adicione uma descrição para apresentar sua trajetória.')}</p>
                    </section>
                    <section class="perfil-bloco">
                        <h3>Competências</h3>
                        <div class="perfil-tags">
                            ${competencias.length
                                ? competencias.map(competencia => `<span class="perfil-tag">${escaparHtml(competencia)}</span>`).join('')
                                : '<span>Sem competências informadas</span>'}
                        </div>
                    </section>
                </div>

                <div class="perfil-detalhes">
                    <p><strong>E-mail:</strong> ${escaparHtml(candidato.email)}</p>
                    <p><strong>Idade:</strong> ${escaparHtml(String(candidato.idade))}</p>
                    <p><strong>Localização:</strong> ${escaparHtml([candidato.estado, candidato.pais].filter(Boolean).join(', '))}</p>
                    <p><strong>Endereço:</strong> <span id="endereco-perfil-candidato">Consultando CEP...</span></p>
                </div>
            </div>

        </section>
    `
}

function escaparHtml(valor: string): string {
    return valor.replace(/[&<>"']/g, caractere => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
    })[caractere]!)
}

async function preencherEnderecoPerfil(cep: string): Promise<void> {
    const enderecoPerfil = document.querySelector<HTMLSpanElement>('#endereco-perfil-candidato')
    if (!enderecoPerfil) return

    try {
        const endereco = await consultarEnderecoPorCep(cep)
        enderecoPerfil.textContent = formatarEndereco(endereco)
    } catch (erro) {
        console.error('Não foi possível carregar o endereço do candidato:', erro)
        enderecoPerfil.textContent = 'Endereço indisponível para este CEP.'
    }
}
