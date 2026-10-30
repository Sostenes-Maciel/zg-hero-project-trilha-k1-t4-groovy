import type { Empresa } from '../model/Empresa'
import type { Candidato } from '../model/Candidato'
import { Chart } from "chart.js/auto"
import { renderViewCadastroVaga } from '../service/CadastroVaga.ts'
import { calcularAfinidade } from "../service/CalcularAfinidade.ts"
import { renderListaMatchesEmpresa } from "./ListaMatchesEmpresa.ts"

export async function renderPerfilEmpresa(empresa: Empresa, mensagemSucesso?: string, animarTransicao: boolean = true): Promise<void> {
    const app = document.querySelector<HTMLDivElement>('#app')
    if (!app) return

    renderizarTemplatePerfil(app, empresa, mensagemSucesso, animarTransicao)

    // Agora buscamos os candidatos REAIS do banco antes de desenhar
    const candidatosDoBanco = await buscarCandidatosDoBanco()

    renderizarGraficoCompetencias(candidatosDoBanco)
    renderizarTabelaCandidatos(empresa, candidatosDoBanco)
    configurarEventos(empresa)
}

function renderizarTemplatePerfil(app: HTMLDivElement, empresa: Empresa, mensagemSucesso?: string, animarTransicao: boolean = true): void {
    app.innerHTML = `
        <section class="perfil-empresa ${animarTransicao ? 'tela-com-transicao' : ''}">
            <h1>Perfil da Empresa</h1>
            <p id="mensagem" class="${mensagemSucesso ? 'mensagem-sucesso' : ''}">${mensagemSucesso ?? ''}</p>

            <div>
                <h2>${empresa.nome}</h2>
                <p><strong>E-mail:</strong> ${empresa.email}</p>
                <p><strong>CNPJ:</strong> ${empresa.cnpj}</p>
                <p><strong>Estado:</strong> ${empresa.estado}</p>
                <p><strong>Descrição:</strong> ${empresa.descricao}</p>
                <p>
                    <strong>Competências Desejadas:</strong>
                    ${Array.isArray(empresa.competencias) ? empresa.competencias.join(', ') : 'Nenhuma competência informada'}
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
                    <tr><td colspan="4">Carregando candidatos...</td></tr>
                </tbody>
            </table>

            <div class="acoes-perfil">
                <button type="button" id="btn-cadastrar-vaga">Cadastrar vaga</button>
                <button type="button" id="btn-meus-matches">Meus Matches</button>
            </div>
        </section>
    `
}

async function buscarCandidatosDoBanco(): Promise<Candidato[]> {
    try {
        const resposta = await fetch('http://localhost:8080/api/candidatos')
        if (resposta.ok) {
            const dadosDoServidor = await resposta.json()
            return dadosDoServidor.dados ? dadosDoServidor.dados : dadosDoServidor
        }
        return []
    } catch (erro) {
        console.error("Erro ao buscar candidatos:", erro)
        return []
    }
}

function renderizarGraficoCompetencias(candidatos: Candidato[]): void {
    try {
        const quantidadePorCompetencia: Record<string, number> = {}

        candidatos.forEach(candidato => {
            candidato.competencias.forEach(competencia => {
                const chave = competencia.trim().toLowerCase()
                quantidadePorCompetencia[chave] = (quantidadePorCompetencia[chave] || 0) + 1
            })
        })

        const canvas = document.querySelector<HTMLCanvasElement>('#grafico-competencias')
        if (!canvas || Object.keys(quantidadePorCompetencia).length === 0) return

        new Chart(canvas, {
            type: 'bar',
            data: {
                labels: Object.keys(quantidadePorCompetencia),
                datasets: [{
                    label: 'Quantidade de candidatos',
                    data: Object.values(quantidadePorCompetencia)
                }]
            },
            options: {
                responsive: true,
                plugins: { legend: { display: true } },
                scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } }
            }
        })
    } catch (erro) {
        console.error('Erro ao criar gráfico:', erro)
    }
}

function renderizarTabelaCandidatos(empresa: Empresa, candidatos: Candidato[]): void {
    const lista = document.querySelector<HTMLTableSectionElement>('#lista-candidatos-empresa')
    if (!lista) return

    if (candidatos.length === 0) {
        lista.innerHTML = '<tr><td colspan="4" class="tabela-vazia">Nenhum candidato disponível no momento.</td></tr>'
        return
    }

    lista.innerHTML = ''

    candidatos.forEach((candidato) => {
        const linha = document.createElement('tr')
        linha.dataset.competencias = candidato.competencias.join(' ').toLowerCase()

        const curtiu = false
        const temMatch = false
        const iconeCurtida = '♡'

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
                    title="${temMatch ? 'Match confirmado' : curtiu ? 'Candidato curtido' : 'Curtir candidato'}"
                    ${temMatch ? 'disabled' : ''}
                >
                    ${iconeCurtida}
                </button>
            </td>
        `
        lista.appendChild(linha)
    })
}

function configurarEventos(empresa: Empresa): void {
    configurarBotaoCadastrarVaga(empresa)
    configurarBotoesCurtirCandidato(empresa)
    configurarFiltroCompetencia()
    configurarNavegacaoMatches(empresa)
    configurarBotaoSair()
}

function configurarBotaoCadastrarVaga(empresa: Empresa): void {
    const botaoCadastrarVaga = document.querySelector<HTMLButtonElement>('#btn-cadastrar-vaga')
    botaoCadastrarVaga?.addEventListener('click', (e) => {
        e.preventDefault()
        void renderViewCadastroVaga(() => {
            void renderPerfilEmpresa(empresa)
        })
    })
}

function configurarBotoesCurtirCandidato(empresa: Empresa): void {
    const botoesCurtir = document.querySelectorAll<HTMLButtonElement>('.btn-curtir-candidato')

    botoesCurtir.forEach(botao => {
        botao.addEventListener('click', (e) => {
            e.preventDefault()
            const cpf = botao.dataset.cpf
            if (!cpf) return

            alert(`A empresa ${empresa.nome} curtiu o candidato com CPF: ${cpf}! (Lógica do banco em breve)`)
        })
    })
}

function configurarFiltroCompetencia(): void {
    const filtroCompetencia = document.querySelector<HTMLInputElement>('#filtro-competencia')
    const lista = document.querySelector<HTMLTableSectionElement>('#lista-candidatos-empresa')

    filtroCompetencia?.addEventListener('input', () => {
        const termo = filtroCompetencia.value.trim().toLowerCase()
        const linhas = lista?.querySelectorAll<HTMLTableRowElement>('tr') ?? []

        linhas.forEach(linha => {
            const competencias = linha.dataset.competencias ?? ''
            linha.hidden = !competencias.includes(termo)
        })
    })
}

function configurarNavegacaoMatches(empresa: Empresa): void {
    const botaoMatches = document.querySelector<HTMLButtonElement>('#btn-meus-matches')
    botaoMatches?.addEventListener('click', (e) => {
        e.preventDefault()
        renderListaMatchesEmpresa(empresa, () => {
            void renderPerfilEmpresa(empresa)
        })
    })
}

function configurarBotaoSair(): void {
    const botaoSair = document.querySelector<HTMLButtonElement>('#btn-sair-perfil')

    botaoSair?.addEventListener('click', (evento) => {
        evento.preventDefault()

        localStorage.removeItem('usuarioLogado')
        localStorage.removeItem('tipoUsuario')

        // Atualiza o botão Login do menu
        window.dispatchEvent(new Event('auth-change'))

        // Avisa ao menu para abrir a tela de login
        window.dispatchEvent(new Event('app:logout'))
    })
}

export default renderPerfilEmpresa