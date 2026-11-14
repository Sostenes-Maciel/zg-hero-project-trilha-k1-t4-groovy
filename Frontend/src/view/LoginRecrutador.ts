import { autenticarRecrutador } from '../service/AutenticacaoRecrutador.ts'
import { renderPerfilRecrutador } from './PerfilRecrutador.ts'

export function renderLoginRecrutador(): void {
    const app = document.querySelector<HTMLDivElement>('#app')
    if (!app) return
    app.innerHTML = `
        <section class="login-container tela-com-transicao">
            <h1>Login do Recrutador</h1>
            <p class="form-descricao">Acesse seu espaço de recrutamento.</p>
            <form id="form-login-recrutador">
                <div><label for="email">E-mail</label><input id="email" name="email" type="email" required></div>
                <div><label for="senha">Senha</label><input id="senha" name="senha" type="password" required></div>
                <button type="submit">Entrar</button>
                <p id="mensagem-login"></p>
            </form>
        </section>
    `
    document.querySelector<HTMLFormElement>('#form-login-recrutador')?.addEventListener('submit', async evento => {
        evento.preventDefault()
        const dados = Object.fromEntries(new FormData(evento.currentTarget as HTMLFormElement))
        const mensagem = document.querySelector<HTMLParagraphElement>('#mensagem-login')
        try {
            const recrutador = await autenticarRecrutador(String(dados.email), String(dados.senha))
            await renderPerfilRecrutador(recrutador)
        } catch (erro) {
            if (mensagem) {
                mensagem.className = 'mensagem-erro'
                mensagem.textContent = erro instanceof Error ? erro.message : 'Não foi possível entrar.'
            }
        }
    })
}
