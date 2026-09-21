import type { Candidato } from '../model/Candidato'
import {renderListaCandidatos} from "./listaCandidatos.ts";
import {renderListaVagas} from "./listaVagas.ts";

export function renderPerfilCandidato(candidato: Candidato): void {
    const app = document.querySelector<HTMLDivElement>('#app')

    if (!app) {
        return
    }

    app.innerHTML = `
        <section class="perfil-candidato">
            <h1>Perfil do Candidato</h1>

            <div class="card-perfil">
                <h2>${candidato.nome}</h2>

                <p><strong>E-mail:</strong> ${candidato.email}</p>
                <p><strong>Idade:</strong> ${candidato.idade}</p>
                <p><strong>CPF:</strong> ${candidato.cpf}</p>
                <p><strong>Estado:</strong> ${candidato.estado}</p>
                <p><strong>CEP:</strong> ${candidato.cep}</p>
                <p><strong>Descrição:</strong> ${candidato.descricao}</p>
                <p>
                    <strong>Competências:</strong>
                    ${candidato.competencias.join(', ')}
                </p>
            </>
            <button id="voltar-candidatos">Voltar</button>
            <button id="btn-vagas-disponiveis">Vagas disponíveis</button>
        </>
    `

    const botaoVagas = document.querySelector<HTMLButtonElement>(
        '#btn-vagas-disponiveis'
    )

    botaoVagas?.addEventListener('click', () => {
        renderListaVagas(() => {
            renderPerfilCandidato(candidato)
        })
    })

    const botaoVoltar = document.querySelector<HTMLButtonElement>(
        '#voltar-candidatos'
    )

    botaoVoltar?.addEventListener('click', () => {
        renderListaCandidatos()
    })

}