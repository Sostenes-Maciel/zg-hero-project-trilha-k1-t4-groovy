import type { Candidato } from '../model/Candidato'
import { BancodeDados } from '../repository/BancodeDados'

export function renderCadastroCandidato(): void {
    const app = document.querySelector<HTMLDivElement>('#app')

    if (!app) {
        return
    }

    app.innerHTML = `
    
        <section>
            <h1>Cadastro de Candidato</h1>

            <form id="form-candidato">
                <div>
                    <label for="nome">Nome</label>
                    <input type="text" id="nome" name="nome" required>
                </div>

                <div>
                    <label for="email">E-mail</label>
                    <input type="email" id="email" name="email"
                     placeholder="Ex.: exemplo@gmail.com"
                     required>
                </div>

                <div>
                    <label for="idade">Idade</label>
                    <input type="number" id="idade" name="idade" required>
                </div>

                <div>
                    <label for="cpf">CPF</label>
                    <input type="text" id="cpf" name="cpf" required>
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
                        placeholder="Ex.: Java, Python, Groovy"
                        required
                    >
                </div>

                <button type="submit">Cadastrar</button>
            </form>
        </section>
    `

    const formulario = document.querySelector<HTMLFormElement>('#form-candidato')

    formulario?.addEventListener('submit', (evento) => {
        evento.preventDefault()

        const dados = new FormData(formulario)

        const candidato: Candidato = {
            nome: dados.get('nome') as string,
            email: dados.get('email') as string,
            idade: Number(dados.get('idade')),
            cpf: dados.get('cpf') as string,
            estado: dados.get('estado') as string,
            cep: dados.get('cep') as string,
            descricao: dados.get('descricao') as string,
            competencias: (dados.get('competencias') as string)
                .split(',')
                .map(competencia => competencia.trim())
                .filter(competencia => competencia.length > 0)
        }

        BancodeDados.cadastrarCandidato(candidato)

        console.log('Candidato cadastrado:', candidato)
    })
}