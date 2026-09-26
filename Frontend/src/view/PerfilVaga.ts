import { BancodeDados } from '../repository/BancodeDados'
import { renderListaVagas } from './ListaVagas.ts'
import type {Candidato} from "../model/Candidato.ts";

export function renderPerfilVaga(vagaId: number, onVoltar?: () => void, candidato?: Candidato): void {
    const app = document.querySelector<HTMLDivElement>('#app')

    if (!app) {
        return
    }

    const vagaEncontrada = BancodeDados.vagas.find(v => v.id === vagaId)
    console.log('Vaga encontrada:', vagaEncontrada)

    if (!vagaEncontrada) {
        app.innerHTML = `
            <section>
                <h2>Vaga não encontrada!</h2>
                <button id="btn-voltar">Voltar para vagas</button>
            </section>
        `
        document.querySelector('#btn-voltar')?.addEventListener('click', () => {
            renderListaVagas()
        })
        return
    }

    const temMatch = candidato
        ? BancodeDados.candidatoTemMatchComVaga(candidato, vagaEncontrada)
        : false

    app.innerHTML = `
        <section class="perfil-vaga">
            <h1>Detalhes da Vaga</h1>
            
            <div class="card-vaga">
                <h2>${vagaEncontrada.titulo}</h2>
            
                <hr>
            
                <h3>Informações da Vaga</h3>
            
                <p>
                    <strong>Descrição:</strong>
                    ${vagaEncontrada.descricao}
                </p>
            
                <p>
                    <strong>Local:</strong>
                    ${vagaEncontrada.pais} - ${vagaEncontrada.estado}
                </p>
            
               <p>
                    <strong>Competências:</strong>
                    ${vagaEncontrada.competencias?.join(', ') || 'Nenhuma competência especificada'}
                </p>
            
                <hr>
            
                <h3>Informações da Empresa</h3>
            
                <p>
                    <strong>Empresa:</strong>
                    ${temMatch
                        ? vagaEncontrada.empresa.nome
                        : 'Match necessário para visualização'}
                </p>
            </div>
            <br>
            <button id="btn-voltar-lista">Voltar para Vagas</button>
        </section>
    `

    document.querySelector('#btn-voltar-lista')?.addEventListener('click', () => {
        onVoltar?.()
    })
}