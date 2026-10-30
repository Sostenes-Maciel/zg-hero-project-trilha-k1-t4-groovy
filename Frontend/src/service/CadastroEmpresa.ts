import type { Empresa } from '../model/Empresa.ts'
import { ValidarEmpresa } from './ValidarEmpresa.ts'
import {
    getCountries,
    getStatesOfCountry
} from '@countrystatecity/countries-browser'

export async function renderCadastroEmpresa(): Promise<void> {
    const app = document.querySelector<HTMLDivElement>('#app')
    if (!app) return

    renderizarFormulario(app)
    await configurarSelectsLocalizacao()
    configurarEventosFormulario()
}

function renderizarFormulario(app: HTMLDivElement): void {
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
                <input type="text" id="codigoPostal" name="codigoPostal" placeholder="Ex.: 55299-300" required>

                <div>
                    <label for="descricao">Descrição</label>
                    <textarea id="descricao" name="descricao" required></textarea>
                </div>

                <div>
                    <label for="competencias">Competências</label>
                    <input type="text" id="competencias" name="competencias" placeholder="Ex.: Java, Angular, TypeScript" required>
                </div>

                <button type="submit">Cadastrar</button>
                <p id="mensagem"></p>
            </form>
        </section>
    `
}

async function configurarSelectsLocalizacao(): Promise<void> {
    const selectPais = document.querySelector<HTMLSelectElement>('#pais')
    const selectEstado = document.querySelector<HTMLSelectElement>('#estado')

    if (!selectPais || !selectEstado) return

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
        await atualizarEstadosPorPais(selectPais.value, selectEstado)
    })
}

async function atualizarEstadosPorPais(codigoPais: string, selectEstado: HTMLSelectElement): Promise<void> {
    selectEstado.innerHTML = '<option value="">Selecione um estado</option>'
    selectEstado.disabled = true

    if (!codigoPais) return

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
}

function configurarEventosFormulario(): void {
    const formulario = document.querySelector<HTMLFormElement>('#form-empresa')
    formulario?.addEventListener('submit', async (evento) => {
        evento.preventDefault()
        await processarSubmissao(formulario)
    })
}

async function processarSubmissao(formulario: HTMLFormElement): Promise<void> {
    const mensagem = document.querySelector<HTMLParagraphElement>('#mensagem')
    const selectPais = document.querySelector<HTMLSelectElement>('#pais')

    if (mensagem) {
        mensagem.className = ''
        mensagem.textContent = ''
    }

    try {
        const empresa = extrairEValidarDados(formulario, selectPais)

        const resposta = await fetch('http://localhost:8080/api/empresas', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(empresa)
        });

        if (resposta.status === 201) {
            console.log('Empresa cadastrada no banco real:', empresa)
            exibirMensagem(mensagem, 'Empresa cadastrada com sucesso!', 'mensagem-sucesso')
            formulario.reset()
        } else {
            const erroBackend = await resposta.json();
            exibirMensagem(mensagem, "Erro: " + (erroBackend.mensagem || "Dados inválidos."), 'mensagem-erro')
        }

    } catch (erro) {
        console.error('Erro ao cadastrar empresa:', erro)
        const textoErro = erro instanceof Error ? erro.message : 'Servidor indisponível. Verifique se o Tomcat está rodando.'
        exibirMensagem(mensagem, textoErro, 'mensagem-erro')
    }
}

function extrairEValidarDados(formulario: HTMLFormElement, selectPais: HTMLSelectElement | null): Empresa {
    const dados = new FormData(formulario)

    const nome = dados.get('nome') as string
    const email = dados.get('email') as string
    const cnpj = dados.get('cnpj') as string
    const pais = selectPais?.selectedOptions[0]?.textContent?.trim() ?? ''
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
    ValidarEmpresa.validarCodigoPostal(codigoPostal, selectPais?.value ?? '')
    ValidarEmpresa.validarDescricao(descricao)
    ValidarEmpresa.validarCompetencias(competencias)

    const cnpjLimpo = cnpj.replace(/\D/g, '')
    const codigoPostalLimpo = codigoPostal.replace(/\D/g, '')

    return { nome, email, cnpj: cnpjLimpo, pais, estado, cep: codigoPostalLimpo, descricao, competencias }
}

function exibirMensagem(elemento: HTMLParagraphElement | null, texto: string, classeCSS: string): void {
    if (elemento) {
        elemento.textContent = texto
        elemento.classList.add(classeCSS)
    }
}