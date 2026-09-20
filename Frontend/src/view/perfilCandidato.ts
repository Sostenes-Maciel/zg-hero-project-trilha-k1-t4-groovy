import type { Candidato } from '../model/Candidato'
import {renderListaCandidatos} from "./listaCandidatos.ts";
import { BancodeDados } from '../repository/BancodeDados'

export function renderPerfilCandidato(candidato: Candidato): void {
    const app = document.querySelector<HTMLDivElement>('#app')

    if (!app) {
        return
    }

    app.innerHTML = `
        <section>
            <h1>Perfil do Candidato</h1>

            <div>
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
            </div>
            <h2>Vagas disponíveis</h2>

                <table>
                    <thead>
                        <tr>
                            <th>Vaga</th>
                            <th>Empresa</th>
                        </tr>
                    </thead>
                
                    <tbody id="lista-vagas-candidato"></tbody>
                </table>
                
                

            <button id="voltar-candidatos">Voltar</button>
        </section>
    `
    const listaVagas = document.querySelector<HTMLTableSectionElement>(
        '#lista-vagas-candidato'
    )

    if (!listaVagas) {
        return
    }

    if (BancodeDados.vagas.length === 0) {
        listaVagas.innerHTML = `
        <tr>
            <td colspan="2">Nenhuma vaga cadastrada.</td>
        </tr>
    `
    } else {
        BancodeDados.vagas.forEach(vaga => {
            const linha = document.createElement('tr')

            linha.innerHTML = `
            <td>${vaga.titulo}</td>
            <td>${vaga.empresa.nome}</td>
        `

            listaVagas.appendChild(linha)
        })
    }

    const botaoVoltar = document.querySelector<HTMLButtonElement>(
        '#voltar-candidatos'
    )

    botaoVoltar?.addEventListener('click', () => {
        renderListaCandidatos()
    })

}