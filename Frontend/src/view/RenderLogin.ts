import {renderPerfilCandidato} from './PerfilCandidato'
import type {Candidato} from '../model/Candidato'
import {renderPerfilEmpresa} from './PerfilEmpresa'
import type {Empresa} from '../model/Empresa'
import { autenticarRecrutador } from '../service/AutenticacaoRecrutador.ts'
import { renderPerfilRecrutador } from './PerfilRecrutador.ts'

export function renderLogin(): void {
    const app = document.querySelector<HTMLDivElement>('#app')
    if (!app) return

    renderizarFormularioLogin(app)
    configurarEventosLogin()
}

function renderizarFormularioLogin(app: HTMLDivElement): void {
    app.innerHTML = `
        <section class="login-container tela-com-transicao">
            <h1>Login no Linketinder</h1>
            <p class="form-descricao">Acesse seu espaço e continue sua jornada profissional.</p>
            <form id="form-login">
                <div>
                    <label for="tipoUsuario">Entrar como:</label>
                    <select id="tipoUsuario" name="tipoUsuario" required>
                        <option value="candidato">Candidato</option>
                        <option value="empresa">Empresa</option>
                        <option value="recrutador">Recrutador</option>
                    </select>
                </div>
                
                <div>
                    <label for="email">E-mail</label>
                    <input type="email" id="email" name="email" required>
                </div>

                <div id="campo-documento-login">
                    <label id="labelDocumento" for="documento">CPF/CNPJ</label>
                    <input type="text" id="documento" name="documento" required>
                </div>

                <div id="campo-senha-login" hidden>
                    <label for="senha">Senha</label>
                    <input type="password" id="senha" name="senha" autocomplete="current-password">
                </div>

                <button type="submit">Entrar</button>
                <p id="mensagem-login"></p>
            </form>
        </section>
    `
}

function configurarEventosLogin(): void {
    const formulario = document.querySelector<HTMLFormElement>('#form-login')
    formulario?.addEventListener('submit', async (evento) => {
        evento.preventDefault()
        await processarLogin(formulario)
    })

    const tipo = formulario?.querySelector<HTMLSelectElement>('#tipoUsuario')
    const documento = formulario?.querySelector<HTMLInputElement>('#documento')
    const campoDocumento = formulario?.querySelector<HTMLDivElement>('#campo-documento-login')
    const labelDocumento = formulario?.querySelector<HTMLLabelElement>('#labelDocumento')
    const campoSenha = formulario?.querySelector<HTMLDivElement>('#campo-senha-login')
    const senha = formulario?.querySelector<HTMLInputElement>('#senha')
    tipo?.addEventListener('change', () => {
        const recrutador = tipo.value === 'recrutador'
        if (campoDocumento) campoDocumento.hidden = recrutador
        if (campoSenha) campoSenha.hidden = !recrutador
        if (senha) senha.required = recrutador
        if (documento) {
            documento.required = !recrutador
            documento.placeholder = recrutador ? 'Não necessário' : ''
            documento.disabled = recrutador
            documento.value = recrutador ? '' : documento.value
        }
        if (labelDocumento) {
            labelDocumento.textContent = recrutador
                ? 'Documento'
                : tipo.value === 'candidato' ? 'CPF' : 'CNPJ'
        }
    })
}

async function processarLogin(formulario: HTMLFormElement): Promise<void> {
    const mensagem = document.querySelector<HTMLParagraphElement>('#mensagem-login')
    const dados = new FormData(formulario)

    const tipoUsuario = dados.get('tipoUsuario') as string
    const email = dados.get('email') as string
    const documentoBruto = dados.get('documento') as string
    const senha = dados.get('senha') as string

    const documentoLimpo = (documentoBruto ?? '').replace(/\D/g, '')

    if (mensagem) {
        mensagem.className = ''
        mensagem.textContent = ''
    }

    try {
        if (tipoUsuario === 'recrutador') {
            const recrutador = await autenticarRecrutador(email, senha)
            window.dispatchEvent(new Event('auth-change'))
            await renderPerfilRecrutador(recrutador)
            return
        }

        const endpoint = tipoUsuario === 'candidato'
            ? 'http://localhost:8080/api/candidatos/login'
            : tipoUsuario === 'empresa'
                ? 'http://localhost:8080/api/empresas/login'
                : 'http://localhost:8080/api/recrutadores/login'

        const payload = tipoUsuario === 'candidato'
            ? {email: email, cpf: documentoLimpo}
            : tipoUsuario === 'empresa'
                ? {email: email, cnpj: documentoLimpo}
                : {email, senha}

        const resposta = await fetch(endpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        if (resposta.status === 200) {
            const retorno = await resposta.json()

            // Extrai o objeto do usuário de dentro da resposta do backend
            const usuarioAutenticado =
                retorno.empresa ??
                retorno.candidato ??
                retorno.dados ??
                retorno.usuario ??
                retorno

            localStorage.setItem('usuarioLogado', JSON.stringify(usuarioAutenticado))
            localStorage.setItem('tipoUsuario', tipoUsuario)

            window.dispatchEvent(new Event('auth-change'))

            if (tipoUsuario === 'candidato') {
                renderPerfilCandidato(usuarioAutenticado as Candidato)
            } else if (tipoUsuario === 'empresa') {
                await renderPerfilEmpresa(usuarioAutenticado as Empresa)
            }

            document.querySelector('#app')?.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            })

        } else {
            exibirMensagemLogin(mensagem, 'E-mail ou Documento incorretos.', 'mensagem-erro')
        }

    } catch (erro) {
        exibirMensagemLogin(mensagem, 'Servidor indisponível.', 'mensagem-erro')
    }

    function exibirMensagemLogin(elementoHtml: HTMLElement | null, texto: string, classeCss: string): void {
        if (elementoHtml) {
            elementoHtml.textContent = texto
            elementoHtml.className = classeCss
            setTimeout(() => {
                elementoHtml.textContent = ''
                elementoHtml.className = ''
            }, 3000)
        }
    }
}