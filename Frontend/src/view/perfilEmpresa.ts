import type { Empresa } from '../model/Empresa'
import { BancodeDados } from '../repository/BancodeDados'
import {Chart} from "chart.js/auto";

export function renderPerfilEmpresa(empresa: Empresa): void {
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

            <div>
                <canvas id="grafico-competencias"></canvas>
            </div>

            <h2>Candidatos disponíveis</h2>

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
        </section>
    `
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

    BancodeDados.candidatos.forEach((candidato, indice) => {
        const linha = document.createElement('tr')

        linha.innerHTML = `
            <td>Candidato #${indice + 1}</td>
            <td>${candidato.competencias.join(', ')}</td>
        `

        lista.appendChild(linha)
    })

    const botaoVoltar = document.querySelector<HTMLButtonElement>(
        '#voltar-empresa'
    )

    botaoVoltar?.addEventListener('click', () => {
        window.location.reload()
    })
}