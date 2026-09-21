import type {Candidato} from '../model/Candidato.ts'
import {BancodeDados} from '../repository/BancodeDados.ts'
import {ValidarCandidato} from '../service/validarCandidato'
import { getStatesOfCountry } from '@countrystatecity/countries-browser'

export async function renderCadastroCandidato(): Promise<void> {
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
                    <input type="text" id="cpf" name="cpf" 
                    placeholder="Ex.: 111.222.333-44"
                    required>
                </div>

                <div>
                    <label for="estado">Estado</label>
                    <select id="estado" name="estado" required>
                    </select>
                </div>

                <div>
                    <label for="cep">CEP</label>
                    <input type="text" id="cep" name="cep" 
                    placeholder="Ex.: 11222-333"
                    required>
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
                <p id="mensagem"></p>
            </form>
        </section>
    `

    const selectEstado = document.querySelector<HTMLSelectElement>('#estado')

    if (!selectEstado) {
        return
    }

    try {
        const estados = await getStatesOfCountry('BR')

        selectEstado.innerHTML = `
        <option value="">Selecione um estado</option>
    `

        estados.forEach(estado => {
            const option = document.createElement('option')

            option.value = estado.iso2
            option.textContent = estado.name

            selectEstado.appendChild(option)
        })
    } catch (erro) {
        console.error('Erro ao carregar estados:', erro)

        selectEstado.innerHTML = `
        <option value="">Não foi possível carregar os estados</option>
    `
    }

    const formulario = document.querySelector<HTMLFormElement>('#form-candidato')

    formulario?.addEventListener('submit', (evento) => {
        evento.preventDefault()

        try {

            const dados = new FormData(formulario)

            const nome = dados.get('nome') as string
            const email = dados.get('email') as string
            const idade = Number(dados.get('idade'))
            const cpf = dados.get('cpf') as string
            const estado = dados.get('estado') as string
            const cep = dados.get('cep') as string
            const descricao = dados.get('descricao') as string

            const competencias = (dados.get('competencias') as string)
                .split(',')
                .map(competencia => competencia.trim())
                .filter(competencia => competencia.length > 0)

            ValidarCandidato.validarNome(nome)
            ValidarCandidato.validarEmail(email)
            ValidarCandidato.validarCpf(cpf)
            ValidarCandidato.validarIdade(idade)
            ValidarCandidato.validarEstado(estado)
            ValidarCandidato.validarCep(cep)
            ValidarCandidato.validarDescricao(descricao)
            ValidarCandidato.validarCompetencias(competencias)

            const candidato: Candidato = {
                nome,
                email,
                idade,
                cpf,
                estado,
                cep,
                descricao,
                competencias
            }

            BancodeDados.cadastrarCandidato(candidato)

            const mensagem = document.querySelector<HTMLParagraphElement>('#mensagem')

            if (mensagem) {
                mensagem.textContent = 'Candidato cadastrado com sucesso!'
            }

            formulario.reset()

            console.log('Candidato cadastrado:', candidato)

        } catch (erro) {
            console.error('Erro ao cadastrar candidato:', erro)

            const mensagem = document.querySelector<HTMLParagraphElement>('#mensagem')

            if (mensagem) {
                mensagem.textContent =
                    erro instanceof Error
                        ? erro.message
                        : 'Não foi possível cadastrar o candidato. Tente novamente.'
            }
        }
    })
}
