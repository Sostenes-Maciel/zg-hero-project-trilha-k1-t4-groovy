import type { Empresa } from '../model/Empresa.ts'
import { BancodeDados } from '../repository/BancodeDados.ts'
import { ValidarEmpresa } from './validarEmpresa.ts'
import {
    getCountries,
    getStatesOfCountry
} from '@countrystatecity/countries-browser'

export async function renderCadastroEmpresa(): Promise<void> {
    const app = document.querySelector<HTMLDivElement>('#app')

    if (!app) {
        return
    }

    app.innerHTML = `
        <section class="cadastro-container">
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
                        placeholder="Ex.: Java, Angular, TypeScript"
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

    const formulario = document.querySelector<HTMLFormElement>('#form-empresa')

    formulario?.addEventListener('submit', (evento) => {
        evento.preventDefault()

        try {
            const dados = new FormData(formulario)

            const nome = dados.get('nome') as string
            const email = dados.get('email') as string
            const cnpj = dados.get('cnpj') as string
            const pais = selectPais.selectedOptions[0]?.textContent?.trim() ?? ''
            const estado = dados.get('estado') as string
            const codigoPostal = dados.get('codigoPostal') as string
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
            ValidarEmpresa.validarCodigoPostal(
                codigoPostal,
                selectPais.value
            )
            ValidarEmpresa.validarDescricao(descricao)
            ValidarEmpresa.validarCompetencias(competencias)

            const empresa: Empresa = {
                nome,
                email,
                cnpj,
                pais,
                estado,
                cep: codigoPostal,
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