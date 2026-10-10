import { BancodeDados } from '../dao/BancodeDados'
import { renderListaVagas } from './ListaVagas.ts'
import type { Candidato } from "../model/Candidato.ts"
import type { Vaga } from "../model/Vaga.ts"

export function renderPerfilVaga(vagaId: number, onVoltar?: () => void, candidato?: Candidato): void {
    const app = document.querySelector<HTMLDivElement>('#app')
    if (!app) return

    const vagaEncontrada = BancodeDados.vagas.find(v => v.id === vagaId)
    console.log('Vaga encontrada:', vagaEncontrada)

    if (!vagaEncontrada) {
        renderizarVagaNaoEncontrada(app)
        return
    }

    const temMatch = candidato ? BancodeDados.candidatoTemMatchComVaga(candidato, vagaEncontrada) : false

    renderizarPerfilVagaHTML(app, vagaEncontrada, temMatch)
    configurarEventoVoltar(onVoltar)
}

function renderizarVagaNaoEncontrada(app: HTMLDivElement): void {
    app.innerHTML = `
        <section class="perfil-vaga tela-com-transicao">
            <h2>Vaga não encontrada!</h2>
            <button id="btn-voltar">Voltar para vagas</button>
        </section>
    `

    document.querySelector('#btn-voltar')?.addEventListener('click', () => {
        renderListaVagas()
    })
}

function renderizarPerfilVagaHTML(app: HTMLDivElement, vaga: Vaga, temMatch: boolean): void {
    const competenciasTexto = vaga.competencias?.join(', ') || 'Nenhuma competência especificada'
    const nomeEmpresa = temMatch ? vaga.empresa.nome : 'Match necessário para visualização'

    app.innerHTML = `
        <section class="perfil-vaga tela-com-transicao">
            <h1>Detalhes da Vaga</h1>
            
            <div class="card-vaga">
                <h2>${vaga.titulo}</h2>
                <hr>
                
                <h3>Informações da Vaga</h3>
                <p>
                    <strong>Descrição:</strong>
                    ${vaga.descricao}
                </p>
                
                <p>
                    <strong>Local:</strong>
                    ${vaga.pais} - ${vaga.estado}
                </p>
                
                <p>
                    <strong>Competências:</strong>
                    ${competenciasTexto}
                </p>
                
                <hr>
                
                <h3>Informações da Empresa</h3>
                <p>
                    <strong>Empresa:</strong>
                    ${nomeEmpresa}
                </p>
            </div>
            
            <button id="btn-voltar-lista">Voltar para Vagas</button>
        </section>
    `
}

function configurarEventoVoltar(onVoltar?: () => void): void {
    document.querySelector('#btn-voltar-lista')?.addEventListener('click', () => {
        onVoltar?.()
    })
}