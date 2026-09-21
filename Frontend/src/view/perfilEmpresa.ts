import type { Empresa } from '../model/Empresa'
import { BancodeDados } from '../repository/BancodeDados'
import {Chart} from "chart.js/auto";
import {renderListaEmpresas} from "./listaEmpresas.ts";
import { renderViewCadastroVaga } from '../service/cadastroVaga'

function renderPerfilEmpresa(empresa: Empresa): void {
    const app = document.querySelector<HTMLDivElement>('#app')

    if (!app) {
        return
    }

    app.innerHTML = `
        <section>
            <h1>Perfil da Empresa</h1>

            <div>
                <h2>${empresa.nome}</h2>

                <p><strong>E-mail:</strong> ${empresa.email}</p>
                <p><strong>CNPJ:</strong> ${empresa.cnpj}</p>
                <p><strong>País:</strong> ${empresa.pais}</p>
                <p><strong>Estado:</strong> ${empresa.estado}</p>
                <p><strong>CEP:</strong> ${empresa.cep}</p>
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

            <table>
                <thead>
                    <tr>
                        <th>Candidato</th>
                        <th>Competências</th>
                    </tr>
                </thead>

                <tbody id="lista-candidatos-empresa">
                </tbody>
            </table>

            <button id="voltar-empresa">Voltar</button>
            <button id="btn-cadastrar-vaga">Cadastrar vaga</button>
        </section>
    `
    const botaoCadastrarVaga = document.querySelector<HTMLButtonElement>(
        '#btn-cadastrar-vaga'
    )

    botaoCadastrarVaga?.addEventListener('click', () => {
        renderViewCadastroVaga(() => {
            renderPerfilEmpresa(empresa); // Re-desenha o perfil desta mesma empresa
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
            throw new Error('Canvas do gráfico não encontrado.')
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

        linha.innerHTML = `
        <td>Match necessário para visualização</td>
        <td>${candidato.competencias.join(', ')}</td>
    `

        lista.appendChild(linha)
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

    const botaoVoltar = document.querySelector<HTMLButtonElement>(
        '#voltar-empresa'
    )

    botaoVoltar?.addEventListener('click', () => {
        renderListaEmpresas()
    })
}

export default renderPerfilEmpresa