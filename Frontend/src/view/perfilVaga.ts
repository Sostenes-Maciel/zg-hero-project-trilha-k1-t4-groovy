import { BancodeDados } from '../repository/BancodeDados'
import { renderListaVagas } from './listaVagas'

export function renderPerfilVaga(vagaId: number, onVoltar?: () => void): void {
    const app = document.querySelector<HTMLDivElement>('#app')

    if (!app) {
        return
    }

    const vagaEncontrada = BancodeDados.vagas.find(v => v.id === vagaId)

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

    app.innerHTML = `
        <section class="perfil-vaga">
            <h1>Detalhes da Vaga</h1>
            
            <div class="card-vaga">
                <h2>${vagaEncontrada.titulo}</h2>
                <hr>
                <h3>Informações da Empresa</h3>
                <p><strong>Empresa:</strong> Match necessário para visualização</p>
            </div>

            <br>
            <button id="btn-voltar-lista">Voltar para Vagas</button>
        </section>
    `

    document.querySelector('#btn-voltar-lista')?.addEventListener('click', () => {
        renderListaVagas(onVoltar)
    })
}