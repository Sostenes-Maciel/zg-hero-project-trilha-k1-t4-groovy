import { BancodeDados } from '../repository/BancodeDados'
import {renderPerfilCandidato} from "./perfilCandidato.ts";

export function renderListaCandidatos(): void {
    const app = document.querySelector<HTMLDivElement>('#app')

    if (!app) {
        return
    }

    const candidatos = BancodeDados.candidatos

    app.innerHTML = `
        <section>
            <h1>Candidatos</h1>

            <table>
                <thead>
                    <tr>
                        <th>Nome</th>
                        <th>Estado</th>
                        <th>Competências</th>
                        <th>Ação</th>
                        
                    </tr>
                </thead>
                

                <tbody id="lista-candidatos">
                </tbody>
            </table>
        </section>
    `

    const lista = document.querySelector<HTMLTableSectionElement>('#lista-candidatos')

    if (!lista) {
        return
    }

    candidatos.forEach(candidato => {
        const linha = document.createElement('tr')

        linha.innerHTML = `
            <td>${candidato.nome}</td>
            <td>${candidato.estado}</td>
            <td>${candidato.competencias.join(', ')}</td>
                <td>
                    <button class="btn-perfil" data-cpf="${candidato.cpf}">
                        Ver perfil
                    </button>
                
                    <button class="btn-excluir" data-cpf="${candidato.cpf}">
                        Excluir
                    </button>
                </td>
            `

        lista.appendChild(linha)
    })

    const botoesPerfil = document.querySelectorAll<HTMLButtonElement>(
        '.btn-perfil'
    )

    botoesPerfil.forEach(botao => {
        botao.addEventListener('click', () => {
            const cpf = botao.dataset.cpf

            const candidato = BancodeDados.candidatos.find(
                candidato => candidato.cpf === cpf
            )

            if (!candidato) {
                console.error('Candidato não encontrado.')
                return
            }

            renderPerfilCandidato(candidato)
        })
    })

    const botoesExcluir = document.querySelectorAll<HTMLButtonElement>(
        '.btn-excluir'
    )

    botoesExcluir.forEach(botao => {
        botao.addEventListener('click', () => {
            const cpf = botao.dataset.cpf

            if (!cpf) {
                console.error('CPF do candidato não encontrado.')
                return
            }

            const confirmar = window.confirm(
                'Tem certeza que deseja excluir este candidato?'
            )

            if (!confirmar) {
                return
            }

            try {
                BancodeDados.excluirCandidato(cpf)

                renderListaCandidatos()
            } catch (erro) {
                console.error('Erro ao excluir candidato:', erro)

                window.alert(
                    'Não foi possível excluir o candidato. Tente novamente.'
                )
            }
        })
    })
}