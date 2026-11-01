import type { Candidato } from '../model/Candidato'
import { consultarEnderecoPorCep, formatarEndereco } from '../service/ConsultaCep.ts'

export function renderPerfilCandidato(candidato: Candidato): void {
    console.log("Dados do candidato:", candidato)

    const app = document.querySelector<HTMLDivElement>('#app')
    if (!app) return

    renderizarTemplatePerfil(app, candidato)
    void preencherEnderecoPerfil(candidato.cep)
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
