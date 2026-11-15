import type { Empresa } from '../model/Empresa.ts'
import type { Recrutador } from '../model/Recrutador.ts'
import { cadastrarRecrutador } from '../service/CadastroRecrutador.ts'

const AREAS = ['TI', 'RH', 'Marketing', 'Vendas', 'Segurança'] as const
const PERMISSOES = ['Total', 'Limitado'] as const

export async function renderCadastroRecrutador(recrutador?: Recrutador): Promise<void> {
    const app = document.querySelector<HTMLDivElement>('#app')
    const empresa = obterEmpresaLogada()
    if (!app) return
    if (!empresa) {
        app.innerHTML = '<section class="login-container tela-com-transicao"><h1>Acesso restrito</h1><p class="mensagem-erro">Somente uma Empresa logada pode cadastrar recrutadores.</p></section>'
        return
    }

    app.innerHTML = `
        <section class="cadastro-container tela-com-transicao">
            <h1>${recrutador ? 'Editar recrutador' : 'Cadastro de Recrutador'}</h1>
            <p class="form-descricao">Adicione colaboradores para analisar candidatos e vagas.</p>
            <form id="form-recrutador">
                <div><label for="nome">Nome</label><input id="nome" name="nome" required></div>
                <div><label for="email">E-mail</label><input id="email" name="email" type="email" required></div>
                <div><label for="cpf">CPF</label><input id="cpf" name="cpf" required></div>
                <div><label for="dataNascimento">Data de nascimento</label><input id="dataNascimento" name="dataNascimento" type="date" required></div>
                <div><label for="areaAtuacao">Área de atuação</label><select id="areaAtuacao" name="areaAtuacao" required>${AREAS.map(area => `<option value="${area}">${area}</option>`).join('')}</select></div>
                <div><label for="nivelPermissao">Nível de permissão</label><select id="nivelPermissao" name="nivelPermissao" required>${PERMISSOES.map(nivel => `<option value="${nivel}">${nivel}</option>`).join('')}</select></div>
                <div><label for="descricao">Descrição</label><textarea id="descricao" name="descricao"></textarea></div>
                <div><label for="senha">Senha</label><input id="senha" name="senha" type="password" minlength="6" maxlength="8" required></div>
                <button type="submit">${recrutador ? 'Salvar alterações' : 'Cadastrar'}</button>
                <p id="mensagem"></p>
            </form>
        </section>
    `
    preencher(recrutador)
    document.querySelector<HTMLFormElement>('#form-recrutador')?.addEventListener('submit', async evento => {
        evento.preventDefault()
        const mensagem = document.querySelector<HTMLParagraphElement>('#mensagem')
        try {
            const dados = Object.fromEntries(new FormData(evento.currentTarget as HTMLFormElement))
            await cadastrarRecrutador({
                ...dados,
                empresaId: empresa.cnpj.replace(/\D/g, ''),
                area_atuacao: dados.areaAtuacao as Recrutador['area_atuacao'],
                nivel_permissao: dados.nivelPermissao as Recrutador['nivel_permissao'],
                senha: String(dados.senha),
                cpf: String(dados.cpf),
                dataNascimento: String(dados.dataNascimento),
                email: String(dados.email),
                nome: String(dados.nome)
            })
            if (mensagem) {
                mensagem.className = 'mensagem-sucesso'
                mensagem.textContent = 'Recrutador cadastrado com sucesso!'
            }
        } catch (erro) {
            if (mensagem) {
                mensagem.className = 'mensagem-erro'
                mensagem.textContent = erro instanceof Error ? erro.message : 'Não foi possível salvar o recrutador.'
            }
        }
    })
}

function obterEmpresaLogada(): Empresa | null {
    if (localStorage.getItem('tipoUsuario') !== 'empresa') return null
    try {
        return JSON.parse(localStorage.getItem('usuarioLogado') ?? '') as Empresa
    } catch {
        return null
    }
}

function preencher(recrutador?: Recrutador): void {
    if (!recrutador) return
    for (const [id, valor] of Object.entries({
        nome: recrutador.nome, email: recrutador.email, cpf: recrutador.cpf ?? '',
        dataNascimento: recrutador.dataNascimento ?? '', descricao: recrutador.descricao ?? '',
        areaAtuacao: recrutador.area_atuacao, nivelPermissao: recrutador.nivel_permissao
    })) {
        const campo = document.querySelector<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>(`#${id}`)
        if (campo) campo.value = valor
    }
}
