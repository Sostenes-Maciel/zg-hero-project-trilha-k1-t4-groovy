import type {Candidato} from '../model/Candidato.ts'
import {BancodeDados} from '../repository/BancodeDados.ts'
import {ValidarCandidato} from './ValidarCandidato.ts'
import {
    getCountries,
    getStatesOfCountry
} from '@countrystatecity/countries-browser'
export async function renderCadastroCandidato(): Promise<void> {
    const app = document.querySelector<HTMLDivElement>('#app')

    if (!app) {
        return
    }

    app.innerHTML = `
    
        <section class="cadastro-container">
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

                <div class="campo-idade">
                    <label for="idade">Idade</label>
                
                    <input
                        type="number"
                        id="idade"
                        name="idade"
                        min="18"
                        max="120"
                        inputmode="numeric"
                        placeholder="Ex.: 29"
                        required
                    >
                </div>

                <div>
                    <label for="cpf">CPF</label>
                    <input type="text" id="cpf" name="cpf" 
                    placeholder="Ex.: 111.222.333-44"
                    required>
                </div>

                <div>
                    <label for="pais">País</label>
                    <select id="pais" name="pais" required>
                        <option value="">Selecione um país</option>
                    </select>
                </div>
                
                <div>
                    <label for="estado">Estado</label>
                    <select id="estado" name="estado" required disabled>
                        <option value="">Selecione um país primeiro</option>
                    </select>
                </div>

                <label for="codigoPostal">Código Postal</label>
                    <input
                        type="text"
                        id="codigoPostal"
                        name="codigoPostal"
                        placeholder="Ex.: 55299-300"
                        required
                    >

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

    const selectPais = document.querySelector<HTMLSelectElement>('#pais')
    const selectEstado = document.querySelector<HTMLSelectElement>('#estado')

    if (!selectPais || !selectEstado) {
        return
    }

    try {
        const paises = await getCountries()

        paises.forEach(pais => {
            const option = document.createElement('option')

            option.value = pais.iso2
            option.textContent = pais.name

            selectPais.appendChild(option)
        })
    } catch (erro) {
        console.error('Erro ao carregar países:', erro)
    }

    selectPais.addEventListener('change', async () => {
        const codigoPais = selectPais.value

        selectEstado.innerHTML = `
        <option value="">Selecione um estado</option>
    `

        selectEstado.disabled = true

        if (!codigoPais) {
            return
        }

        try {
            const estados = await getStatesOfCountry(codigoPais)

            estados.forEach(estado => {
                const option = document.createElement('option')

                option.value = estado.name
                option.textContent = estado.name

                selectEstado.appendChild(option)
            })

            selectEstado.disabled = false

        } catch (erro) {
            console.error('Erro ao carregar estados:', erro)
        }
    })

    const formulario = document.querySelector<HTMLFormElement>('#form-candidato')

    formulario?.addEventListener('submit', (evento) => {
        evento.preventDefault()

        try {

            const dados = new FormData(formulario)

            const nome = dados.get('nome') as string
            const email = dados.get('email') as string
            const idade = Number(dados.get('idade'))
            const cpf = dados.get('cpf') as string
            const pais = selectPais.selectedOptions[0]?.textContent?.trim() ?? ''
            const estado = dados.get('estado') as string
            const codigoPostal = dados.get('codigoPostal') as string
            const descricao = dados.get('descricao') as string

            const competencias = (dados.get('competencias') as string)
                .split(',')
                .map(competencia => competencia.trim())
                .filter(competencia => competencia.length > 0)

            ValidarCandidato.validarNome(nome)
            ValidarCandidato.validarEmail(email)
            ValidarCandidato.validarCpf(cpf)
            ValidarCandidato.validarPais(pais)
            ValidarCandidato.validarIdade(idade)
            ValidarCandidato.validarEstado(estado)
            ValidarCandidato.validarCodigoPostal(
                codigoPostal,
                selectPais.value
            )
            ValidarCandidato.validarDescricao(descricao)
            ValidarCandidato.validarCompetencias(competencias)

            const candidato: Candidato = {
                nome,
                email,
                idade,
                cpf,
                pais,
                estado,
                cep: codigoPostal,
                descricao,
                competencias
            }

            BancodeDados.cadastrarCandidato(candidato)

            const mensagem = document.querySelector<HTMLParagraphElement>('#mensagem')

            if (mensagem) {
                mensagem.textContent = 'Candidato cadastrado com sucesso!'
                mensagem.classList.add('mensagem-sucesso')
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
                        mensagem.classList.add('mensagem-erro')
            }
        }
    })
}
