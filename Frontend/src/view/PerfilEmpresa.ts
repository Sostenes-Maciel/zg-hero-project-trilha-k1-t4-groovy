import type { Empresa } from '../model/Empresa'
import type { Candidato } from '../model/Candidato'
import type { Vaga } from '../model/Vaga'
import { Chart } from "chart.js/auto"
import { calcularAfinidade } from "../service/CalcularAfinidade.ts"
import { consultarEnderecoPorCep, formatarEndereco } from '../service/ConsultaCep.ts'
import { mostrarAnimacaoMatch } from "./AnimacaoMatch.ts"

interface StatusMatch {
    cpfCandidato: string
    idVaga: number
    status: 'curtido' | 'match'
}

type VagaDaApi = Omit<Vaga, 'competencias'> & {
    competencias?: string[]
    competencia?: string[]
}

export async function renderPerfilEmpresa(empresa: Empresa, mensagemSucesso?: string, animarTransicao: boolean = true): Promise<void> {
    const app = document.querySelector<HTMLDivElement>('#app')
    if (!app) return

    renderizarTemplatePerfil(app, empresa, mensagemSucesso, animarTransicao)
    void preencherEnderecoPerfil(empresa.cep)

    // Busca candidatos e os status de curtida/match em paralelo
    const [candidatosDoBanco, statusMatches, vagasDaEmpresa] = await Promise.all([
        buscarCandidatosDoBanco(),
        buscarStatusMatches(empresa.cnpj.replace(/\D/g, '')),
        buscarVagasDaEmpresa(empresa.cnpj.replace(/\D/g, ''))
    ])

    renderizarGraficoCompetencias(candidatosDoBanco)
    renderizarTabelaCandidatos(empresa, candidatosDoBanco, statusMatches, vagasDaEmpresa)
    configurarEventos(empresa, statusMatches, candidatosDoBanco)
}

function renderizarTemplatePerfil(app: HTMLDivElement, empresa: Empresa, mensagemSucesso?: string, animarTransicao: boolean = true): void {
    const competencias = Array.isArray(empresa.competencias) ? empresa.competencias : []

    app.innerHTML = `
        <section class="perfil-empresa ${animarTransicao ? 'tela-com-transicao' : ''}">
            <h1>Perfil da Empresa</h1>
            <p id="mensagem" class="${mensagemSucesso ? 'mensagem-sucesso' : ''}">${mensagemSucesso ?? ''}</p>

            <div class="card-perfil card-perfil-empresa">
                <header class="perfil-cabecalho">
                    <h2>${escaparHtml(empresa.nome)}</h2>
                    <p class="perfil-subtitulo">Empresa</p>
                </header>
                <div class="perfil-grade">
                    <section class="perfil-bloco">
                        <h3>Sobre a empresa</h3>
                        <p>${escaparHtml(empresa.descricao || 'Adicione uma descrição para apresentar sua empresa.')}</p>
                    </section>
                    <section class="perfil-bloco">
                        <h3>Competências desejadas</h3>
                        <div class="perfil-tags">
                            ${competencias.length
                                ? competencias.map(competencia => `<span class="perfil-tag">${escaparHtml(competencia)}</span>`).join('')
                                : '<span>Nenhuma competência informada</span>'}
                        </div>
                    </section>
                </div>
                <div class="perfil-resumo-empresa">
                    <p><strong>E-mail</strong><span>${escaparHtml(empresa.email)}</span></p>
                    <p><strong>CNPJ</strong><span>${escaparHtml(empresa.cnpj)}</span></p>
                    <p><strong>Localização</strong><span>${escaparHtml([empresa.estado, empresa.pais].filter(Boolean).join(', '))}</span></p>
                    <p><strong>Endereço</strong><span id="endereco-perfil-empresa">Consultando CEP...</span></p>
                </div>
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
                        <th class="coluna-acao" colspan="2">Ação</th>
                    </tr>
                </thead>
                <tbody id="lista-candidatos-empresa">
                    <tr><td colspan="5">Carregando candidatos...</td></tr>
                </tbody>
            </table>

        </section>
    `
}

function escaparHtml(valor: string): string {
    return valor.replace(/[&<>"']/g, caractere => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
    })[caractere]!)
}

async function preencherEnderecoPerfil(cep: string): Promise<void> {
    const enderecoPerfil = document.querySelector<HTMLSpanElement>('#endereco-perfil-empresa')
    if (!enderecoPerfil) return

    try {
        const endereco = await consultarEnderecoPorCep(cep)
        enderecoPerfil.textContent = formatarEndereco(endereco)
    } catch (erro) {
        console.error('Não foi possível carregar o endereço da empresa:', erro)
        enderecoPerfil.textContent = 'Endereço indisponível para este CEP.'
    }
}

async function buscarCandidatosDoBanco(): Promise<Candidato[]> {
    try {
        const resposta = await fetch('http://localhost:8080/api/candidatos')
        if (resposta.ok) {
            const dadosDoServidor = await resposta.json()
            const resultado = dadosDoServidor.dados ? dadosDoServidor.dados : dadosDoServidor

            return Array.isArray(resultado) ? resultado : []
        }
        return []
    } catch (erro) {
        console.error("Erro ao buscar candidatos:", erro)
        return []
    }
}

function renderizarGraficoCompetencias(candidatos: Candidato[]): void {
    try {
        if (!Array.isArray(candidatos)) return;
        const quantidadePorCompetencia: Record<string, number> = {}

        candidatos.forEach(candidato => {
            candidato.competencias.forEach(competencia => {
                const chave = competencia.trim().toLowerCase()
                quantidadePorCompetencia[chave] = (quantidadePorCompetencia[chave] || 0) + 1
            })
        })

        const canvas = document.querySelector<HTMLCanvasElement>('#grafico-competencias')
        if (!canvas || Object.keys(quantidadePorCompetencia).length === 0) return

        const context = canvas.getContext('2d')
        if (!context) return

        const gradiente = context.createLinearGradient(0, 0, 0, canvas.height)
        gradiente.addColorStop(0, '#20b486')
        gradiente.addColorStop(1, '#2855a6')

        new Chart(canvas, {
            type: 'bar',
            data: {
                labels: Object.keys(quantidadePorCompetencia),
                datasets: [{
                    label: 'Quantidade de candidatos',
                    data: Object.values(quantidadePorCompetencia),
                    backgroundColor: gradiente,
                    hoverBackgroundColor: '#168a68',
                    borderRadius: 8,
                    borderSkipped: false,
                    maxBarThickness: 48
                }]
            },
            options: {
                responsive: true,
                animation: {
                    duration: window.matchMedia('(prefers-reduced-motion: reduce)').matches ? 0 : 900,
                    easing: 'easeOutQuart',
                    delay: context => context.dataIndex * 45
                },
                plugins: {
                    legend: {
                        labels: {
                            color: '#25324b',
                            usePointStyle: true,
                            pointStyle: 'rectRounded',
                            padding: 20
                        }
                    },
                    tooltip: {
                        backgroundColor: '#173b80',
                        titleColor: '#ffffff',
                        bodyColor: '#ffffff',
                        padding: 12,
                        cornerRadius: 10,
                        displayColors: false
                    }
                },
                scales: {
                    x: {
                        grid: { display: false },
                        ticks: { color: '#526176' }
                    },
                    y: {
                        beginAtZero: true,
                        ticks: { stepSize: 1, color: '#526176' },
                        grid: { color: 'rgba(23, 59, 128, 0.08)' }
                    }
                }
            }
        })
    } catch (erro) {
        console.error('Erro ao criar gráfico:', erro)
    }
}

function renderizarTabelaCandidatos(empresa: Empresa, candidatos: Candidato[], statusMatches: StatusMatch[], vagas: Vaga[]): void {
    const lista = document.querySelector<HTMLTableSectionElement>('#lista-candidatos-empresa')
    if (!lista) return

    if (candidatos.length === 0) {
        lista.innerHTML = '<tr><td colspan="5" class="tabela-vazia">Nenhum candidato disponível no momento.</td></tr>'
        return
    }

    lista.innerHTML = ''

    candidatos.forEach((candidato) => {
        const linha = document.createElement('tr')
        linha.dataset.competencias = candidato.competencias.join(' ').toLowerCase()

        const cpfLimpo = candidato.cpf.replace(/\D/g, '')
        const statusCandidato = statusMatches.filter(s =>
            s.cpfCandidato.replace(/\D/g, '') === cpfLimpo
        )
        const vagaSelecionada = statusCandidato.find(s => s.status === 'match')?.idVaga
            ?? statusCandidato.find(s => s.status === 'curtido')?.idVaga
            ?? ''
        const status = statusCandidato.find(s => s.idVaga === vagaSelecionada)?.status
        const temMatch = status === 'match'
        const temMatchComCandidato = statusCandidato.some(s => s.status === 'match')
        const curtiu = status === 'curtido' || temMatch
        const iconeCurtida = temMatch ? '🤝' : curtiu ? '♥' : '♡'
        const opcoesVaga = vagas.map(vaga => `
            <option value="${vaga.id}" ${vaga.id === vagaSelecionada ? 'selected' : ''}>
                ${vaga.titulo}
            </option>
        `).join('')

        linha.innerHTML = `
            <td class="coluna-candidato">
                ${temMatchComCandidato ? candidato.nome : 'Match necessário para visualização'}
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
                    data-id-vaga="${vagaSelecionada ?? ''}"
                    data-curtido="${curtiu}"
                    data-match="${temMatch}"
                    title="${temMatch ? 'Match confirmado' : curtiu ? 'Remover curtida' : 'Curtir candidato'}"
                    ${vagas.length === 0 || !vagaSelecionada || temMatch ? 'disabled' : ''}
                >
                    ${iconeCurtida}
                </button>
            </td>
            <td class="coluna-vaga">
                <div class="acoes-curtida-empresa">
                    <select
                        class="select-vaga-curtir"
                        data-cpf="${candidato.cpf}"
                        aria-label="Selecionar vaga para curtir candidato"
                        ${vagas.length === 0 ? 'disabled' : ''}
                    >
                        <option value="" ${vagaSelecionada ? '' : 'selected'} disabled>Selecionar vaga</option>
                        ${opcoesVaga}
                    </select>
                </div>
            </td>
        `
        lista.appendChild(linha)
    })
}

function configurarEventos(empresa: Empresa, statusMatches: StatusMatch[], candidatos: Candidato[]): void {
    configurarBotoesCurtirCandidato(empresa, statusMatches, candidatos)
    configurarFiltroCompetencia()
}

function configurarBotoesCurtirCandidato(empresa: Empresa, statusMatches: StatusMatch[], candidatos: Candidato[]): void {
    const botoesCurtir = document.querySelectorAll<HTMLButtonElement>('.btn-curtir-candidato')

    botoesCurtir.forEach(botao => {
        const seletorVaga = botao.parentElement?.querySelector<HTMLSelectElement>('.select-vaga-curtir')
            ?? botao.closest('tr')?.querySelector<HTMLSelectElement>('.select-vaga-curtir')
        const cpf = botao.dataset.cpf?.replace(/\D/g, '')
        if (!cpf) return

        seletorVaga?.addEventListener('change', () => {
            animarEscolhaVaga(seletorVaga)
            const idVagaSelecionada = Number(seletorVaga.value)
            const status = buscarStatusMatchesEmMemoria(statusMatches, cpf, idVagaSelecionada)
            atualizarBotaoCurtida(botao, status)
            botao.dataset.idVaga = seletorVaga.value
            botao.disabled = status === 'match' || !seletorVaga.value
        })

        botao.addEventListener('click', async (e) => {
            e.preventDefault()
            const cpfCandidato = botao.dataset.cpf
            if (!cpfCandidato) return

            const idVaga = Number(seletorVaga?.value)
            if (!Number.isInteger(idVaga) || idVaga <= 0) {
                alert('Selecione uma vaga válida para curtir este candidato.')
                return
            }

            const jaCurtido = botao.dataset.curtido === 'true'
            if (botao.dataset.match === 'true') return
            const metodoHTTP = jaCurtido ? 'DELETE' : 'POST'
            botao.disabled = true

            try {
                const cnpjLimpo = empresa.cnpj.replace(/\D/g, '')
                const payload = {
                    autor: "EMPRESA",
                    cnpjEmpresa: cnpjLimpo,
                    cpfCandidato: cpfCandidato.replace(/\D/g, ''),
                    idVaga
                }

                const resposta = await fetch('http://localhost:8080/api/matches', {
                    method: metodoHTTP,
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                })

                if (resposta.ok) {
                    const resultado = await resposta.json()
                    const deuMatch = resultado.match === true
                    const novoStatus = deuMatch ? 'match' : jaCurtido ? undefined : 'curtido'
                    atualizarStatusEmMemoria(statusMatches, cpfCandidato.replace(/\D/g, ''), idVaga, novoStatus)
                    atualizarBotaoCurtida(botao, novoStatus)
                    if (novoStatus === 'curtido') {
                        animarCurtida(botao)
                    } else {
                        botao.classList.remove('curtida-animada')
                    }
                    if (deuMatch) {
                        mostrarAnimacaoMatch()
                        const candidato = candidatos.find(item =>
                            item.cpf.replace(/\D/g, '') === cpfCandidato.replace(/\D/g, '')
                        )
                        const celulaNome = botao.closest('tr')?.querySelector<HTMLTableCellElement>('.coluna-candidato')
                        if (celulaNome && candidato) celulaNome.textContent = candidato.nome
                    }
                } else {
                    const resultado = await resposta.json()
                    alert(`Erro: ${resultado.mensagem}`)
                    void renderPerfilEmpresa(empresa)
                }
            } catch (erro) {
                console.error("Erro ao processar ação:", erro)
                alert('Falha ao processar curtida. Verifique a conexão e tente novamente.')
            } finally {
                botao.disabled = botao.dataset.match === 'true' || !seletorVaga?.value
            }
        })
    })
}

function buscarStatusMatchesEmMemoria(statusMatches: StatusMatch[], cpf: string, idVaga: number): 'curtido' | 'match' | undefined {
    return statusMatches.find(status =>
        status.cpfCandidato.replace(/\D/g, '') === cpf && status.idVaga === idVaga
    )?.status
}

function atualizarStatusEmMemoria(
    statusMatches: StatusMatch[],
    cpf: string,
    idVaga: number,
    status?: 'curtido' | 'match'
): void {
    const indice = statusMatches.findIndex(item =>
        item.cpfCandidato.replace(/\D/g, '') === cpf && item.idVaga === idVaga
    )
    if (indice >= 0) statusMatches.splice(indice, 1)
    if (status) statusMatches.push({ cpfCandidato: cpf, idVaga, status })
}

function atualizarBotaoCurtida(botao: HTMLButtonElement, status?: 'curtido' | 'match'): void {
    const temMatch = status === 'match'
    const curtiu = status === 'curtido' || temMatch

    botao.innerHTML = temMatch ? '🤝' : curtiu ? '♥' : '♡'
    botao.title = temMatch ? 'Match confirmado' : curtiu ? 'Remover curtida' : 'Curtir candidato'
    botao.dataset.curtido = String(curtiu)
    botao.dataset.match = String(temMatch)
    botao.disabled = temMatch
}

function animarCurtida(botao: HTMLButtonElement): void {
    botao.classList.remove('curtida-animada')
    void botao.offsetWidth
    botao.classList.add('curtida-animada')
}

function animarEscolhaVaga(seletor: HTMLSelectElement): void {
    const acoes = seletor.closest<HTMLElement>('.acoes-curtida-empresa')
    acoes?.classList.remove('vaga-selecionada')
    seletor.classList.remove('vaga-escolhida')
    void seletor.offsetWidth
    acoes?.classList.add('vaga-selecionada')
    seletor.classList.add('vaga-escolhida')
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

async function buscarStatusMatches(cnpj: string): Promise<StatusMatch[]> {
    try {
        const resposta = await fetch(`http://localhost:8080/api/matches?cnpj=${cnpj}`)
        if (resposta.ok) {
            const dados = await resposta.json()
            const status = dados.dados ? dados.dados : dados
            return Array.isArray(status) ? status.map((item: StatusMatch) => ({
                ...item,
                idVaga: Number(item.idVaga)
            })) : []
        }
        return []
    } catch (erro) {
        console.error("Erro ao buscar matches:", erro)
        return []
    }
}

async function buscarVagasDaEmpresa(cnpj: string): Promise<Vaga[]> {
    try {
        const resposta = await fetch(`http://localhost:8080/api/vagas?cnpj=${encodeURIComponent(cnpj)}`)
        if (!resposta.ok) {
            console.error(`Falha ao buscar vagas da empresa (HTTP ${resposta.status}).`)
            return []
        }

        const dados: { dados?: VagaDaApi[] } | VagaDaApi[] = await resposta.json()
        const vagas = Array.isArray(dados) ? dados : dados.dados ?? []
        return vagas.map(vaga => ({
            ...vaga,
            competencias: vaga.competencias ?? vaga.competencia ?? []
        }))
    } catch (erro) {
        console.error('Erro ao buscar vagas da empresa:', erro)
        return []
    }
}

export default renderPerfilEmpresa