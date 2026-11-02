import type { Empresa } from '../model/Empresa.ts'
import { ValidarEmpresa } from './ValidarEmpresa.ts'
import { configurarAutocompletarCep } from './ConsultaCep.ts'
import { mostrarNotificacaoSucesso } from '../components/Notificacao.ts'
import {
    getCountries,
    getStatesOfCountry
} from '@countrystatecity/countries-browser'

export async function renderCadastroEmpresa(empresaEmEdicao?: Empresa): Promise<void> {
    const app = document.querySelector<HTMLDivElement>('#app')
    if (!app) return

    renderizarFormulario(app, empresaEmEdicao !== undefined)
    await configurarSelectsLocalizacao()
    if (empresaEmEdicao) {
        await preencherFormularioEdicao(empresaEmEdicao)
    }
    configurarEventosFormulario(configurarBuscaCep(), empresaEmEdicao)
}

function renderizarFormulario(app: HTMLDivElement, modoEdicao: boolean): void {
    app.innerHTML = `
        <section class="cadastro-container tela-com-transicao">
            <h1>${modoEdicao ? 'Editar perfil da empresa' : 'Cadastro de Empresa'}</h1>
            <p class="form-descricao">${modoEdicao ? 'Atualize os dados da sua empresa.' : 'Apresente sua empresa e conecte-se a profissionais que combinam com suas vagas.'}</p>
            <form id="form-empresa" data-modo-edicao="${modoEdicao}">
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

                <div class="campo-codigo-postal">
                    <label for="codigoPostal">Código Postal</label>
                    <input type="text" id="codigoPostal" name="codigoPostal" placeholder="Ex.: 55299-300" required>
                    <p id="mensagem-cep" aria-live="polite"></p>
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

                <div>
                    <label for="logradouro">Endereço</label>
                    <input type="text" id="logradouro" readonly>
                </div>

                <div>
                    <label for="bairro">Bairro</label>
                    <input type="text" id="bairro" readonly>
                </div>

                <div>
                    <label for="cidade">Cidade</label>
                    <input type="text" id="cidade" readonly>
                </div>

                <div>
                    <label for="descricao">Descrição</label>
                    <textarea id="descricao" name="descricao" required></textarea>
                </div>

                <div>
                    <label for="competencias">Competências</label>
                    <input type="text" id="competencias" name="competencias" placeholder="Ex.: Java, Angular, TypeScript" required>
                </div>

                <button type="submit">${modoEdicao ? 'Salvar alterações' : 'Cadastrar'}</button>
                <p id="mensagem"></p>
            </form>
        </section>
    `
}

async function preencherFormularioEdicao(empresa: Empresa): Promise<void> {
    const pais = document.querySelector<HTMLSelectElement>('#pais')
    const estado = document.querySelector<HTMLSelectElement>('#estado')
    const codigoPostal = document.querySelector<HTMLInputElement>('#codigoPostal')
    const formulario = document.querySelector<HTMLFormElement>('#form-empresa')
    if (!pais || !estado || !codigoPostal || !formulario) return

    formulario.querySelector<HTMLInputElement>('#nome')!.value = empresa.nome
    formulario.querySelector<HTMLInputElement>('#email')!.value = empresa.email
    const campoCnpj = formulario.querySelector<HTMLInputElement>('#cnpj')!
    campoCnpj.value = empresa.cnpj
    campoCnpj.readOnly = true
    codigoPostal.value = empresa.cep
    formulario.querySelector<HTMLTextAreaElement>('#descricao')!.value = empresa.descricao
    formulario.querySelector<HTMLInputElement>('#competencias')!.value = empresa.competencias.join(', ')

    const opcaoPais = Array.from(pais.options).find(opcao => opcao.textContent?.trim() === empresa.pais)
    if (opcaoPais) {
        pais.value = opcaoPais.value
        await atualizarEstadosPorPais(pais.value, estado)
    }
    estado.value = empresa.estado
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
            option.value = estado.iso2
            option.textContent = estado.name
            option.dataset.uf = estado.iso2
            selectEstado.appendChild(option)
        })
        selectEstado.disabled = false
    } catch (erro) {
        console.error('Erro ao carregar estados:', erro)
    }
}

function configurarBuscaCep(): () => Promise<void> {
    const campoCep = document.querySelector<HTMLInputElement>('#codigoPostal')
    const campoPais = document.querySelector<HTMLSelectElement>('#pais')
    const campoEstado = document.querySelector<HTMLSelectElement>('#estado')
    const campoLogradouro = document.querySelector<HTMLInputElement>('#logradouro')
    const campoBairro = document.querySelector<HTMLInputElement>('#bairro')
    const campoCidade = document.querySelector<HTMLInputElement>('#cidade')
    const mensagemCep = document.querySelector<HTMLParagraphElement>('#mensagem-cep')

    if (!campoCep || !campoPais || !campoEstado || !campoLogradouro || !campoBairro || !campoCidade || !mensagemCep) {
        return async () => undefined
    }

    return configurarAutocompletarCep(
        campoCep,
        campoPais,
        mensagemCep,
        campoEstado,
        campoLogradouro,
        campoBairro,
        campoCidade,
        async () => {
            campoPais.value = 'BR'
            await atualizarEstadosPorPais('BR', campoEstado)
        }
    )
}

function configurarEventosFormulario(validarCep: () => Promise<void>, empresaEmEdicao?: Empresa): void {
    const formulario = document.querySelector<HTMLFormElement>('#form-empresa')
    formulario?.addEventListener('submit', async (evento) => {
        evento.preventDefault()
        await processarSubmissao(formulario, validarCep, empresaEmEdicao)
    })
}

async function processarSubmissao(formulario: HTMLFormElement, validarCep: () => Promise<void>, empresaEmEdicao?: Empresa): Promise<void> {
    const mensagem = document.querySelector<HTMLParagraphElement>('#mensagem')
    const selectPais = document.querySelector<HTMLSelectElement>('#pais')

    if (mensagem) {
        mensagem.className = ''
        mensagem.textContent = ''
    }

    try {
        await validarCep()
        const empresa = extrairEValidarDados(formulario, selectPais)

        const url = empresaEmEdicao
            ? 'http://localhost:8080/api/empresas'
            : 'http://localhost:8081/api/empresas'

        const resposta = await fetch(url, {
            method: empresaEmEdicao ? 'PUT' : 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(empresa)
        });

        if (resposta.status === (empresaEmEdicao ? 200 : 201)) {
            if (empresaEmEdicao) {
                localStorage.setItem('usuarioLogado', JSON.stringify(empresa))
                window.dispatchEvent(new Event('auth-change'))
                const { renderPerfilEmpresa } = await import('../view/PerfilEmpresa.ts')
                await renderPerfilEmpresa(empresa)
                mostrarNotificacaoSucesso('Usuário editado com sucesso!')
            } else {
                exibirMensagem(mensagem, 'Empresa cadastrada com sucesso!', 'mensagem-sucesso')
                formulario.reset()
            }
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

    const competenciasStr = dados.get('competencias') as string

    if (competenciasStr.trim().includes(' ') && !competenciasStr.includes(',')) {
        if (competenciasStr.trim().split(/\s+/).length > 2) {
            throw new Error('Por favor, separe as competências usando vírgulas (Ex: Java, Python, Groovy).')
        }
    }

    const competencias = competenciasStr
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

    return {
        nome,
        email,
        cnpj: formulario.dataset.modoEdicao === 'true' ? cnpj : cnpjLimpo,
        pais,
        estado,
        cep: codigoPostalLimpo,
        descricao,
        competencias
    }
}

function exibirMensagem(elemento: HTMLParagraphElement | null, texto: string, classeCSS: string): void {
    if (elemento) {
        elemento.textContent = texto
        elemento.classList.add(classeCSS)
    }
}