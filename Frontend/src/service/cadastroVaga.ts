import { BancodeDados } from '../repository/BancodeDados'
import { renderPerfilVaga } from '../view/perfilVaga'
import type { Empresa } from '../model/Empresa.ts'
import type {Vaga} from "../model/Vaga.ts";

export class cadastroVagas {

    static cadastrar(titulo: string, empresa: Empresa): Vaga {
        if (!titulo.trim()) {
            throw new Error('Título da vaga não pode estar vazio.')
        }

        const maiorId = BancodeDados.vagas.reduce(
            (maior, vaga) => Math.max(maior, vaga.id),
            0
        )

        const vaga: Vaga = {
            id: maiorId + 1,
            titulo: titulo.trim(),
            empresa
        }

        BancodeDados.cadastrarVaga(vaga)

        return vaga
    }
}

export function renderViewCadastroVaga(): void {
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
                    <label for="empresa">Empresa</label>
                    <select id="empresa" name="empresa" required>
                        <option value="">Selecione uma empresa</option>
                    </select>
                </div>

                <button type="submit">Cadastrar</button>

                <p id="mensagem"></p>
            </form>
        </section>
    `

    const selectEmpresa = document.querySelector<HTMLSelectElement>('#empresa')

    if (!selectEmpresa) {
        return
    }

    BancodeDados.empresas.forEach(empresa => {
        const option = document.createElement('option')

        option.value = empresa.cnpj
        option.textContent = empresa.nome

        selectEmpresa.appendChild(option)
    })

    const formulario = document.querySelector<HTMLFormElement>('#form-vaga')

    formulario?.addEventListener('submit', evento => {
        evento.preventDefault()

        try {
            const dados = new FormData(formulario)

            const titulo = dados.get('titulo') as string
            const cnpj = dados.get('empresa') as string

            const empresa = BancodeDados.empresas.find(
                empresa => empresa.cnpj === cnpj
            )

            if (!empresa) {
                throw new Error('Empresa não encontrada.')
            }

            // 1. SALVA a vaga usando a classe do Service
            const vaga = cadastroVagas.cadastrar(titulo, empresa)

            const mensagem = document.querySelector<HTMLParagraphElement>(
                '#mensagem'
            )

            if (mensagem) {
                mensagem.textContent = 'Vaga cadastrada com sucesso!'
            }

            formulario.reset()

            console.log('Vaga cadastrada:', vaga)

            // 2. AQUI ENTRA A SUA IDEIA: Redireciona para o Perfil da Vaga!
            // Passamos o vaga.id, que acabou de ser gerado, para desenhar a tela
            renderPerfilVaga(vaga.id)

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