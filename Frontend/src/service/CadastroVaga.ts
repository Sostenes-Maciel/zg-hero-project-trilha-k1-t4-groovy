import type { Empresa } from '../model/Empresa.ts'
import type { Vaga } from "../model/Vaga.ts"
import { getCountries, getStatesOfCountry } from "@countrystatecity/countries-browser"

export class CadastroVagasService {
    static async cadastrar(
        titulo: string,
        descricao: string,
        pais: string,
        estado: string,
        areaAtuacao: string,
        competencias: string[],
        empresa: Empresa
    ): Promise<Vaga> {

        this.validarCampos(titulo, descricao, pais, estado, areaAtuacao, competencias)

        const dadosVaga = {
            titulo: titulo.trim(),
            descricao: descricao.trim(),
            pais: pais.trim(),
            estado: estado.trim(),
            areaAtuacao: areaAtuacao.trim(),

            // O backend utiliza "competencia" no singular
            competencia: competencias,

            // O backend precisa apenas do CNPJ para vincular a vaga
            empresa: {
                cnpj: empresa.cnpj.replace(/\D/g, '')
            }
        }

        const resposta = await fetch('http://localhost:8080/api/vagas', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(dadosVaga)
        })

        const resultado = await resposta.json()

        if (!resposta.ok) {
            throw new Error(resultado.mensagem || 'Erro ao cadastrar vaga.')
        }

        return {
            id: resultado.id,
            titulo: dadosVaga.titulo,
            descricao: dadosVaga.descricao,
            pais: dadosVaga.pais,
            estado: dadosVaga.estado,
            areaAtuacao: dadosVaga.areaAtuacao,
            competencias,
            empresa
        }
    }

    private static validarCampos(
        titulo: string,
        descricao: string,
        pais: string,
        estado: string,
        areaAtuacao: string,
        competencias: string[]
    ): void {

        if (!titulo.trim()) {
            throw new Error('Título da vaga não pode estar vazio.')
        }

        if (!descricao.trim()) {
            throw new Error('Descrição da vaga não pode estar vazia.')
        }

        if (!pais.trim()) {
            throw new Error('País da vaga é obrigatório.')
        }

        if (!estado.trim()) {
            throw new Error('Estado da vaga é obrigatório.')
        }

        if (!areaAtuacao.trim()) {
            throw new Error('Área de atuação da vaga é obrigatória.')
        }

        if (competencias.length === 0) {
            throw new Error('A vaga deve possuir pelo menos uma competência.')
        }
    }
}

export async function renderViewCadastroVaga(
    empresa: Empresa,
    onCadastroConcluido?: (mensagem?: string) => void
): Promise<void> {
    const app = document.querySelector<HTMLDivElement>('#app')
    if (!app) return

    renderizarFormulario(app)
    await configurarSelectsLocalizacao()
    configurarEventosFormulario(empresa, onCadastroConcluido)
}

function renderizarFormulario(app: HTMLDivElement): void {
    app.innerHTML = `
        <section class="cadastro-container tela-com-transicao">
            <h1>Cadastro de Vaga</h1>
            <p class="form-descricao">Descreva a oportunidade e indique onde ela está disponível.</p>

            <form id="form-vaga">
                <div class="campo-vaga-titulo">
                    <label for="titulo">Título da vaga</label>
                    <input type="text" id="titulo" name="titulo" required>
                </div>
                <div class="campo-vaga-descricao">
                    <label for="descricao">Descrição</label>
                    <textarea id="descricao" name="descricao" required></textarea>
                </div>

                <div class="campo-vaga-competencias">
                    <label for="competencias">Competências</label>
                    <input type="text" id="competencias" name="competencias" placeholder="Ex.: Java, Spring, Git" required>
                </div>

                <div class="campo-vaga-pais">
                    <label for="pais">País</label>
                    <select id="pais" name="pais" required>
                        <option value="">Selecione um país</option>
                    </select>
                </div>

                <div class="campo-vaga-estado">
                    <label for="estado">Estado</label>
                    <select id="estado" name="estado" required disabled>
                        <option value="">Selecione um país primeiro</option>
                    </select>
                </div>

                <div class="campo-vaga-area">
                    <label for="areaAtuacao">Área de atuação</label>
                    <select id="areaAtuacao" name="areaAtuacao" required>
                        <option value="">Selecione uma área</option>
                        <option value="TI">TI</option>
                        <option value="RH">RH</option>
                        <option value="Marketing">Marketing</option>
                        <option value="Vendas">Vendas</option>
                        <option value="Segurança">Segurança</option>
                    </select>
                </div>

                <div class="acoes-form-vaga">
                    <button type="submit">Cadastrar vaga</button>
                </div>

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

function configurarEventosFormulario(empresa: Empresa, onCadastroConcluido?: (mensagem?: string) => void): void {
    const formulario = document.querySelector<HTMLFormElement>('#form-vaga')

    formulario?.addEventListener('submit', (evento) => {
        evento.preventDefault()
        void processarSubmissao(formulario, empresa, onCadastroConcluido)
    })
}

async function processarSubmissao(formulario: HTMLFormElement, empresa: Empresa, onCadastroConcluido?: (mensagem?: string) => void): Promise<void> {
    const mensagem = document.querySelector<HTMLParagraphElement>('#mensagem')
    const selectPais = document.querySelector<HTMLSelectElement>('#pais')

    if (mensagem) {
        mensagem.className = ''
        mensagem.textContent = ''
    }

    try {
        const dados = new FormData(formulario)

        const titulo = dados.get('titulo') as string
        const descricao = dados.get('descricao') as string
        const pais = selectPais?.selectedOptions[0]?.textContent?.trim() ?? ''
        const estado = dados.get('estado') as string
        const areaAtuacao = String(dados.get('areaAtuacao') ?? '')
        const competencias = (dados.get('competencias') as string)
            .split(',')
            .map(c => c.trim())
            .filter(c => c.length > 0)

        const vaga = await CadastroVagasService.cadastrar(
            titulo,
            descricao,
            pais,
            estado,
            areaAtuacao,
            competencias,
            empresa
        )

        console.log('Vaga cadastrada:', vaga)

        onCadastroConcluido?.('Vaga cadastrada com sucesso!')

    } catch (erro) {
        console.error('Erro ao cadastrar vaga:', erro)
        if (mensagem) {
            mensagem.textContent = erro instanceof Error ? erro.message : 'Não foi possível cadastrar a vaga.'
            mensagem.classList.add('mensagem-erro')
        }
    }
}