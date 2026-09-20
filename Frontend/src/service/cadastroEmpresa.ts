import type { Empresa } from '../model/Empresa.ts'
import { BancodeDados } from '../repository/BancodeDados.ts'
import { ValidarEmpresa } from '../service/validarEmpresa'

export function renderCadastroEmpresa(): void {
    const app = document.querySelector<HTMLDivElement>('#app')

    if (!app) {
        return
    }

    app.innerHTML = `
        <section>
            <h1>Cadastro de Empresa</h1>

            <form id="form-empresa">
                <div>
                    <label for="nome">Nome</label>
                    <input type="text" id="nome" name="nome" required>
                </div>

                <div>
                    <label for="email">E-mail</label>
                    <input type="email" id="email" name="email" required>
                </div>

                <div>
                    <label for="cnpj">CNPJ</label>
                    <input type="text" id="cnpj" name="cnpj" required>
                </div>

                <div>
                    <label for="pais">País</label>
                    <input type="text" id="pais" name="pais" required>
                </div>

                <div>
                    <label for="estado">Estado</label>
                    <input type="text" id="estado" name="estado" required>
                </div>

                <div>
                    <label for="cep">CEP</label>
                    <input type="text" id="cep" name="cep" required>
                </div>

                <div>
                    <label for="descricao">Descrição</label>
                    <textarea id="descricao" name="descricao" required></textarea>
                </div>

                <div>
                    <label for="competencias">Competências</label>
                    <input
                        type="text"
                        id="competencias"
                        name="competencias"
                        placeholder="Ex.: Java, Angular, TypeScript"
                        required
                    >
                </div>

                <button type="submit">Cadastrar</button>

                <p id="mensagem"></p>
            </form>
        </section>
    `

    const formulario = document.querySelector<HTMLFormElement>('#form-empresa')

    formulario?.addEventListener('submit', (evento) => {
        evento.preventDefault()

        try {
            const dados = new FormData(formulario)

            const nome = dados.get('nome') as string
            const email = dados.get('email') as string
            const cnpj = dados.get('cnpj') as string
            const pais = dados.get('pais') as string
            const estado = dados.get('estado') as string
            const cep = dados.get('cep') as string
            const descricao = dados.get('descricao') as string

            const competencias = (dados.get('competencias') as string)
                .split(',')
                .map(competencia => competencia.trim())
                .filter(competencia => competencia.length > 0)

            ValidarEmpresa.validarNome(nome)
            ValidarEmpresa.validarEmail(email)
            ValidarEmpresa.validarCnpj(cnpj)
            ValidarEmpresa.validarPais(pais)
            ValidarEmpresa.validarEstado(estado)
            ValidarEmpresa.validarCep(cep)
            ValidarEmpresa.validarDescricao(descricao)
            ValidarEmpresa.validarCompetencias(competencias)

            const empresa: Empresa = {
                nome,
                email,
                cnpj,
                pais,
                estado,
                cep,
                descricao,
                competencias
            }

            BancodeDados.cadastrarEmpresa(empresa)

            const mensagem = document.querySelector<HTMLParagraphElement>(
                '#mensagem'
            )

            if (mensagem) {
                mensagem.textContent = 'Empresa cadastrada com sucesso!'
            }

            formulario.reset()

            console.log('Empresa cadastrada:', empresa)

        } catch (erro) {
            console.error('Erro ao cadastrar empresa:', erro)

            const mensagem = document.querySelector<HTMLParagraphElement>(
                '#mensagem'
            )

            if (mensagem) {
                mensagem.textContent =
                    erro instanceof Error
                        ? erro.message
                        : 'Não foi possível cadastrar o candidato. Tente novamente.'
            }
        }
    })
}