import type {Empresa} from '../model/Empresa'
import {BancodeDados} from '../repository/BancodeDados'
import {Chart} from "chart.js/auto";
import {renderListaEmpresas} from "./ListaEmpresas.ts";
import {renderViewCadastroVaga} from '../service/CadastroVaga.ts'
import {calcularAfinidade} from "../service/CalcularAfinidade.ts";
import {renderListaMatchesEmpresa} from "./ListaMatchesEmpresa.ts";

function renderPerfilEmpresa(empresa: Empresa, mensagemSucesso?: string): void {
    const app = document.querySelector<HTMLDivElement>('#app')

    if (!app) {
        return
    }

    app.innerHTML = `
        <section>
            <h1>Perfil da Empresa</h1>
            <p id = "mensagem"></p>

            <div>
                <h2>${empresa.nome}</h2>

                <p><strong>E-mail:</strong> ${empresa.email}</p>
                <p><strong>CNPJ:</strong> ${empresa.cnpj}</p>
                <p><strong>País:</strong> ${empresa.pais}</p>
                <p><strong>Estado:</strong> ${empresa.estado}</p>
                <p><strong>Código Postal:</strong> ${empresa.cep}</p>
                <p><strong>Descrição:</strong> ${empresa.descricao}</p>
                <p>
                    <strong>Competências:</strong>
                    ${empresa.competencias.join(', ')}
                </p>
            </div>
            
            <h2>Candidatos por competência</h2>
            

            <div class="grafico-container">
                <canvas id="grafico-competencias"></canvas>
            </div>

            <h2>Candidatos disponíveis</h2>
            
            <div class="filtro-candidatos">
                <label for="filtro-competencia">Buscar por competência</label>
            
                <input
                    type="text"
                    id="filtro-competencia"
                    placeholder="Ex.: Java, Python, React..."
                >
            </div>

            <table class="tabela-candidatos">
                <thead>
                    <tr>
                        <th class="coluna-candidato">Candidato</th>
                        <th class="coluna-competencias">Competências</th>
                        <th class="coluna-afinidade">Afinidade</th>
                        <th class="coluna-acao">Ação</th>
                    </tr>
                </thead>
            
                <tbody id="lista-candidatos-empresa">
                </tbody>
            </table>

            <button id="voltar-empresa">Voltar</button>
            <button id="btn-cadastrar-vaga">Cadastrar vaga</button>
            <button id="btn-meus-matches">Meus Matches</button>
            
        </section>
    `
    const mensagem = document.querySelector<HTMLParagraphElement>('#mensagem')

    if (mensagemSucesso && mensagem) {
        mensagem.textContent = mensagemSucesso
    }
    const botaoCadastrarVaga = document.querySelector<HTMLButtonElement>(
        '#btn-cadastrar-vaga'
    )

    botaoCadastrarVaga?.addEventListener('click', () => {
        void renderViewCadastroVaga(() => {
            renderPerfilEmpresa(empresa);
        });

    })
    try {
        const quantidadePorCompetencia: Record<string, number> = {}

        BancodeDados.candidatos.forEach(candidato => {
            candidato.competencias.forEach(competencia => {
                const chave = competencia.trim().toLowerCase()

                quantidadePorCompetencia[chave] =
                    (quantidadePorCompetencia[chave] || 0) + 1
            })
        })
        const canvas = document.querySelector<HTMLCanvasElement>(
            '#grafico-competencias'
        )

        if (!canvas) {
            console.error('Canvas do gráfico não encontrado.')
            return
        }

        new Chart(canvas, {
            type: 'bar',

            data: {
                labels: Object.keys(quantidadePorCompetencia),

                datasets: [
                    {
                        label: 'Quantidade de candidatos',
                        data: Object.values(quantidadePorCompetencia)
                    }
                ]
            },

            options: {
                responsive: true,

                plugins: {
                    legend: {
                        display: true
                    }
                },

                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            stepSize: 1
                        }
                    }
                }
            }
        })
    } catch (erro) {
        console.error('Erro ao criar gráfico:', erro)
    }

    const lista = document.querySelector<HTMLTableSectionElement>(
        '#lista-candidatos-empresa'
    )

    if (!lista) {
        return
    }

    BancodeDados.candidatos.forEach((candidato) => {
        const linha = document.createElement('tr')

        linha.dataset.competencias = candidato.competencias
            .join(' ')
            .toLowerCase()

        const curtiu = BancodeDados.empresaCurtiuCandidato(
            empresa,
            candidato
        )

        const temMatch = BancodeDados.empresaTemMatchComCandidato(
            empresa,
            candidato
        )

        const iconeCurtida = temMatch
            ? '🤝'
            : curtiu
                ? '♥'
                : '♡'

        linha.innerHTML = `
            <td class="coluna-candidato">
                ${temMatch ? candidato.nome : 'Match necessário para visualização'}
            </td>
        
            <td class="coluna-competencias">
                ${candidato.competencias.join(', ')}
            </td>
        
            <td class="coluna-afinidade">
                ${calcularAfinidade(candidato, empresa)}%
            </td>
        
            <td class="coluna-acao">
                <button
                    class="btn-curtir-candidato"
                    data-cpf="${candidato.cpf}"
                    title="${
                    temMatch
                        ? 'Match confirmado'
                        : curtiu
                            ? 'Candidato curtido'
                            : 'Curtir candidato'
                }"
                    ${temMatch ? 'disabled' : ''}
                >
                    ${iconeCurtida}
                </button>
            </td>
        `

        lista.appendChild(linha)
    })

    const botoesCurtirCandidato =
        document.querySelectorAll<HTMLButtonElement>(
            '.btn-curtir-candidato'
        )

    botoesCurtirCandidato.forEach(botao => {
        botao.addEventListener('click', () => {
            const cpf = botao.dataset.cpf

            if (!cpf) {
                return
            }

            const candidato = BancodeDados.candidatos.find(
                candidato => candidato.cpf === cpf
            )

            if (!candidato) {
                return
            }

            const posicaoScroll = window.scrollY

            BancodeDados.curtirCandidato(empresa, candidato)

            renderPerfilEmpresa(empresa)

            requestAnimationFrame(() => {
                window.scrollTo(0, posicaoScroll)
            })
        })
    })

    const filtroCompetencia = document.querySelector<HTMLInputElement>(
        '#filtro-competencia'
    )

    filtroCompetencia?.addEventListener('input', () => {
        const termo = filtroCompetencia.value.trim().toLowerCase()

        const linhas = lista.querySelectorAll<HTMLTableRowElement>('tr')

        linhas.forEach(linha => {
            const competencias = linha.dataset.competencias ?? ''

            linha.hidden = !competencias.includes(termo)
        })
    })

    const botaoMatches = document.querySelector<HTMLButtonElement>(
        '#btn-meus-matches'
    )

    botaoMatches?.addEventListener('click', () => {
        renderListaMatchesEmpresa(empresa, () => {
            renderPerfilEmpresa(empresa)
        })
    })

    const botaoVoltar = document.querySelector<HTMLButtonElement>(
        '#voltar-empresa'
    )

    botaoVoltar?.addEventListener('click', () => {
        renderListaEmpresas()
    })
}

export default renderPerfilEmpresa