import type { Empresa } from '../model/Empresa'
import { BancodeDados } from '../repository/BancodeDados'

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

            const empresa: Empresa = {
                nome: dados.get('nome') as string,
                email: dados.get('email') as string,
                cnpj: dados.get('cnpj') as string,
                pais: dados.get('pais') as string,
                estado: dados.get('estado') as string,
                cep: dados.get('cep') as string,
                descricao: dados.get('descricao') as string,
                competencias: (dados.get('competencias') as string)
                    .split(',')
                    .map(competencia => competencia.trim())
                    .filter(competencia => competencia.length > 0)
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
                    'Não foi possível cadastrar a empresa. Tente novamente.'
            }
        }
    })
}