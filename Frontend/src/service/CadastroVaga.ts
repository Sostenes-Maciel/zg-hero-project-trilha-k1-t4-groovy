import { BancodeDados } from '../repository/BancodeDados'
import type { Empresa } from '../model/Empresa.ts'
import type {Vaga} from "../model/Vaga.ts";
import {getCountries, getStatesOfCountry} from "@countrystatecity/countries-browser";

export class cadastroVagas {

    static cadastrar(
        titulo: string,
        descricao: string,
        pais: string,
        estado: string,
        competencias: string[],
        empresa: Empresa
        ): Vaga {

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

        if (competencias.length === 0) {
            throw new Error('A vaga deve possuir pelo menos uma competência.')
        }

        const maiorId = BancodeDados.vagas.reduce(
            (maior, vaga) => Math.max(maior, vaga.id),
            0
        )

        const vaga: Vaga = {
            id: maiorId + 1,
            titulo: titulo.trim(),
            descricao: descricao.trim(),
            pais: pais.trim(),
            estado: estado.trim(),
            competencias,
            empresa
        }

        BancodeDados.cadastrarVaga(vaga)

        return vaga
    }
}

export async function renderViewCadastroVaga(onVoltar?: (mensagem?: string) => void): Promise<void> {
    const app = document.querySelector<HTMLDivElement>('#app')

    if (!app) {
        return
    }

    app.innerHTML = `
        <section class="cadastro-container">
            <h1>Cadastro de Vaga</h1>

            <form id="form-vaga">
                <div>
                    <label for="titulo">Título da vaga</label>
                    <input
                        type="text"
                        id="titulo"
                        name="titulo"
                        required
                    >
                </div>
                <div>
                    <label for="descricao">Descrição</label>
                    <textarea
                        id="descricao"
                        name="descricao"
                        required
                    ></textarea>
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
                    <label for="competencias">Competências</label>
                    <input
                        type="text"
                        id="competencias"
                        name="competencias"
                        placeholder="Ex.: Java, Spring, Git"
                        required
                    >
                </div>

                <div>
                    <label for="empresa">Empresa</label>
                    <select id="empresa" name="empresa" required>
                        <option value="">Selecione uma empresa</option>
                    </select>
                </div>

                <button type="submit">Cadastrar</button> 
                <button type="button" id="btn-voltar-vaga">Voltar</button>

                <p id="mensagem"></p>
            </form>
        </section>
    `

    const selectEmpresa = document.querySelector<HTMLSelectElement>('#empresa')
    const selectPais = document.querySelector<HTMLSelectElement>('#pais')
    const selectEstado = document.querySelector<HTMLSelectElement>('#estado')

    if (!selectEmpresa || !selectEstado || !selectPais) {
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

    BancodeDados.empresas.forEach(empresa => {
        const option = document.createElement('option')

        option.value = empresa.cnpj
        option.textContent = empresa.nome

        selectEmpresa.appendChild(option)
    })

    const formulario = document.querySelector<HTMLFormElement>('#form-vaga')

    const botaoVoltar = document.querySelector<HTMLButtonElement>(
        '#btn-voltar-vaga'
    )

    botaoVoltar?.addEventListener('click', () => {
        onVoltar?.()
    })

    formulario?.addEventListener('submit', evento => {
        evento.preventDefault()

        try {
            const dados = new FormData(formulario)

            const titulo = dados.get('titulo') as string
            const cnpj = dados.get('empresa') as string
            const descricao = dados.get('descricao') as string
            const pais = selectPais.selectedOptions[0]?.textContent?.trim() ?? ''
            const estado = dados.get('estado') as string

            const competencias = (dados.get('competencias') as string)
                .split(',')
                .map(competencia => competencia.trim())
                .filter(competencia => competencia.length > 0)

            const empresa = BancodeDados.empresas.find(
                empresa => empresa.cnpj === cnpj
            )

            if (!empresa) {
                const mensagem = document.querySelector<HTMLParagraphElement>(
                    '#mensagem'
                )

                if (mensagem) {
                    mensagem.textContent = 'Empresa não encontrada.'
                }

                return
            }

            const vaga = cadastroVagas.cadastrar(
                titulo,
                descricao,
                pais,
                estado,
                competencias,
                empresa)

            console.log('Vaga cadastrada:', vaga)

            onVoltar?.('Vaga cadastrada com sucesso!')

        } catch (erro) {
            console.error('Erro ao cadastrar vaga:', erro)

            const mensagem = document.querySelector<HTMLParagraphElement>(
                '#mensagem'
            )

            if (mensagem) {
                mensagem.textContent =
                    erro instanceof Error
                        ? erro.message
                        : 'Não foi possível cadastrar a vaga. Tente novamente.'
            }
        }
    })
}