import type { Candidato } from '../model/Candidato'
import { renderListaVagas } from "./ListaVagas.ts"
import { renderListaMatchesCandidato } from "./ListaMatchesCandidato.ts"
import { consultarEnderecoPorCep, formatarEndereco } from '../service/ConsultaCep.ts'

export function renderPerfilCandidato(candidato: Candidato): void {
    console.log("Dados do candidato:", candidato)

    const app = document.querySelector<HTMLDivElement>('#app')
    if (!app) return

    renderizarTemplatePerfil(app, candidato)
    void preencherEnderecoPerfil(candidato.cep)
    configurarEventosNavegacao(candidato)
}

function renderizarTemplatePerfil(app: HTMLDivElement, candidato: Candidato): void {
    app.innerHTML = `
        <section class="perfil-candidato tela-com-transicao">            
            <h1>Perfil do Candidato</h1>

            <div class="card-perfil">
                <h2>${candidato.nome}</h2>

                <p><strong>E-mail:</strong> ${candidato.email}</p>
                <p><strong>Idade:</strong> ${candidato.idade}</p>
                <p><strong>CPF:</strong> ${candidato.cpf}</p>
                <p><strong>Pais:</strong> ${candidato.pais}</p>
                <p><strong>Estado:</strong> ${candidato.estado}</p>
                <p><strong>CEP:</strong> ${candidato.cep}</p>
                <p><strong>Endereço:</strong> <span id="endereco-perfil-candidato">Consultando CEP...</span></p>
                <p><strong>Descrição:</strong> ${candidato.descricao}</p>
                <p>
                    <strong>Competências:</strong>
                    ${candidato.competencias.join(', ')}
                </p>
            </div>

            <div class="acoes-perfil">
                <button id="btn-vagas-disponiveis">Vagas disponíveis</button>
                <button id="btn-meus-matches">Meus Matches</button>
                <button type="button" id="btn-sair-perfil">Sair</button>
            </div>
        </section>
    `
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

function configurarEventosNavegacao(candidato: Candidato): void {
    const botaoVagas = document.querySelector<HTMLButtonElement>('#btn-vagas-disponiveis')
    const botaoMatches = document.querySelector<HTMLButtonElement>('#btn-meus-matches')
    const botaoSair = document.querySelector<HTMLButtonElement>('#btn-sair-perfil')

    botaoVagas?.addEventListener('click', () => {
        renderListaVagas(() => {
            renderPerfilCandidato(candidato)
        }, candidato)
    })

    botaoMatches?.addEventListener('click', () => {
        renderListaMatchesCandidato(candidato, () => {
            renderPerfilCandidato(candidato)
        })
    })

    botaoSair?.addEventListener('click', (evento) => {
            evento.preventDefault()

            localStorage.removeItem('usuarioLogado')
            localStorage.removeItem('tipoUsuario')

            // Atualiza o botão Login do menu
            window.dispatchEvent(new Event('auth-change'))

            // Avisa ao menu para abrir a tela de login
            window.dispatchEvent(new Event('app:logout'))
        })
    }
