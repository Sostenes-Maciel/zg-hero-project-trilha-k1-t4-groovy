import { renderPerfilVaga } from './PerfilVaga.ts'
import type { Candidato } from "../model/Candidato.ts"
import type { Vaga } from '../model/Vaga.ts'
import { calcularAfinidadePorCompetencias } from "../service/CalcularAfinidade.ts"
import { mostrarAnimacaoMatch } from "./AnimacaoMatch.ts"

// Tipagem para a resposta de status das curtidas do candidato
interface StatusMatchVaga {
    idVaga: number
    status: 'curtido' | 'match'
}

type VagaDaApi = Omit<Vaga, 'competencias'> & {
    competencias?: string[]
    competencia?: string[]
}

export async function renderListaVagas(candidato?: Candidato, animarTransicao: boolean = true): Promise<void> {
    const app = document.querySelector<HTMLDivElement>('#app')
    if (!app) return

    renderizarTemplateBase(app, animarTransicao)

    // Busca vagas e os status de curtida/match em paralelo
    const [vagasDoBanco, statusMatches] = await Promise.all([
        buscarVagasDoBanco(),
        candidato ? buscarStatusMatchesCandidato(candidato.cpf.replace(/\D/g, '')) : Promise.resolve([])
    ])

    renderizarLinhasVagas(vagasDoBanco, statusMatches, candidato)
    configurarEventos(vagasDoBanco, statusMatches, candidato)
}

function renderizarTemplateBase(app: HTMLDivElement, animarTransicao: boolean): void {
    app.innerHTML = `
        <section class="lista-vagas ${animarTransicao ? 'tela-com-transicao' : ''}">
            <h1>Vagas disponíveis</h1>
            
            <div class="filtro-vagas">
                <label for="input-filtro-vagas">Buscar vaga</label>
                <input
                    type="text"
                    id="input-filtro-vagas"
                    placeholder="Ex.: Java, Python, Tech Global..."
                >
            </div>

            <table class="tabela-vagas">
                <thead>
                    <tr>
                        <th>Vaga</th>
                        <th>Empresa</th>
                        <th>Afinidade</th>
                        <th>Ação</th>
                    </tr>
                </thead>
                <tbody id="lista-vagas-corpo">
                    <tr><td colspan="4">Carregando vagas...</td></tr>
                </tbody>
            </table>

        </section>
    `
}

async function buscarVagasDoBanco(): Promise<Vaga[]> {
    try {
        const resposta = await fetch('http://localhost:8080/api/vagas')
        if (resposta.ok) {
            const dadosDoServidor: { dados?: VagaDaApi[] } | VagaDaApi[] = await resposta.json()
            const vagas = Array.isArray(dadosDoServidor)
                ? dadosDoServidor
                : dadosDoServidor.dados ?? []

            return vagas.map(vaga => ({
                ...vaga,
                competencias: vaga.competencias ?? vaga.competencia ?? []
            }))
        }
        return []
    } catch (erro) {
        console.error("Erro ao buscar vagas:", erro)
        return []
    }
}

async function buscarStatusMatchesCandidato(cpf: string): Promise<StatusMatchVaga[]> {
    try {
        // Assume que sua API suporta passar o CPF do candidato para retornar o status
        const resposta = await fetch(`http://localhost:8080/api/matches?cpf=${cpf}`)
        if (resposta.ok) {
            const dados = await resposta.json()
            return dados.dados ? dados.dados : dados
        }
        return []
    } catch (erro) {
        console.error("Erro ao buscar matches do candidato:", erro)
        return []
    }
}

function renderizarLinhasVagas(vagas: Vaga[], statusMatches: StatusMatchVaga[], candidato?: Candidato): void {
    const lista = document.querySelector<HTMLTableSectionElement>('#lista-vagas-corpo')
    if (!lista) return

    if (vagas.length === 0) {
        lista.innerHTML = `
            <tr>
                <td colspan="4" class="tabela-vazia">Nenhuma vaga cadastrada.</td>
            </tr>
        `
        return
    }

    lista.innerHTML = ''

    vagas.forEach(vaga => {
        const linha = document.createElement('tr')
        linha.dataset.busca = [
            vaga.titulo,
            vaga.empresa?.nome ?? '',
            ...(vaga.competencias ?? [])
        ].join(' ').toLocaleLowerCase('pt-BR')

        const afinidade = candidato
            ? calcularAfinidadePorCompetencias(candidato.competencias, vaga.competencias)
            : null

        // Verifica o status no array retornado pelo backend
        const status = statusMatches.find(s => s.idVaga === vaga.id)?.status

        const curtiu = status === 'curtido' || status === 'match'
        const temMatch = status === 'match'
        const iconeCurtida = temMatch ? '🤝' : curtiu ? '♥' : '♡'

        linha.innerHTML = `
            <td>${vaga.titulo}</td>
            <td>${temMatch ? vaga.empresa?.nome ?? 'Empresa não informada' : 'Match necessário para visualização'}</td>
            <td>${afinidade !== null ? `${afinidade}%` : '—'}</td>
            <td>
                <button
                    class="btn-curtir-vaga"
                    data-id="${vaga.id}"
                    data-curtido="${curtiu}"
                    data-match="${temMatch}"
                    title="${temMatch ? 'Match confirmado!' : curtiu ? 'Descurtir' : 'Curtir vaga'}"
                    ${temMatch ? 'disabled' : ''}
                >
                    ${iconeCurtida}
                </button>
            
                <button class="btn-perfil-vaga" data-id="${vaga.id}">
                    Ver vaga
                </button>
            </td>
        `
        lista.appendChild(linha)
    })
}

function configurarEventos(vagas?: Vaga[], statusMatches: StatusMatchVaga[] = [], candidato?: Candidato): void {
    configurarFiltroBusca()
    configurarBotoesCurtir(candidato)
    configurarBotoesPerfil(vagas, statusMatches, candidato)
}

function configurarFiltroBusca(): void {
    const inputFiltro = document.querySelector<HTMLInputElement>('#input-filtro-vagas')
    const lista = document.querySelector<HTMLTableSectionElement>('#lista-vagas-corpo')

    inputFiltro?.addEventListener('input', () => {
        const termo = inputFiltro.value.trim().toLowerCase()
        const linhas = lista?.querySelectorAll<HTMLTableRowElement>('tr') ?? []

        linhas.forEach(linha => {
            const textoBusca = linha.dataset.busca ?? ''
            linha.hidden = !textoBusca.includes(termo)
        })
    })
}

function configurarBotoesCurtir(candidato?: Candidato): void {
    const botoesCurtir = document.querySelectorAll<HTMLButtonElement>('.btn-curtir-vaga')

    botoesCurtir.forEach(botao => {
        botao.addEventListener('click', async (e) => {
            e.preventDefault()
            if (!candidato?.cpf) {
                alert('Sessão de candidato inválida. Faça login novamente.')
                return
            }

            const vagaId = Number(botao.dataset.id)
            if (!Number.isInteger(vagaId) || vagaId <= 0) {
                alert('Vaga inválida.')
                return
            }
            const jaCurtido = botao.dataset.curtido === 'true'

            botao.disabled = true
            const iconeOriginal = botao.innerHTML
            botao.innerHTML = '...'

            try {
                const cpfLimpo = candidato.cpf.replace(/\D/g, '')
                const metodoHTTP = jaCurtido ? 'DELETE' : 'POST'

                const payload = {
                    autor: "CANDIDATO", // Importante! Informa ao backend quem está curtindo
                    cpfCandidato: cpfLimpo,
                    idVaga: vagaId
                }

                const resposta = await fetch('http://localhost:8080/api/matches', {
                    method: metodoHTTP,
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                })

                if (resposta.ok) {
                    if (jaCurtido) {
                        // Removeu a curtida
                        botao.innerHTML = '♡'
                        botao.title = 'Curtir vaga'
                        botao.dataset.curtido = 'false'
                        botao.dataset.match = 'false'
                        botao.classList.remove('curtida-animada')
                    } else {
                        // Curtiu a vaga
                        const resultado = await resposta.json()
                        if (resultado.mensagem && resultado.mensagem.toLowerCase().includes('match')) {
                            botao.innerHTML = '🤝'
                            botao.title = 'Match confirmado!'
                            botao.dataset.match = 'true'
                            botao.disabled = true
                            mostrarAnimacaoMatch()
                        } else {
                            botao.innerHTML = '♥'
                            botao.title = 'Descurtir'
                            botao.dataset.match = 'false'
                        }
                        botao.dataset.curtido = 'true'
                        if (botao.dataset.match !== 'true') {
                            animarCurtida(botao)
                        }
                    }
                } else {
                    const resultado = await resposta.json()
                    alert(`Erro: ${resultado.mensagem}`)
                    botao.innerHTML = iconeOriginal
                }
            } catch (erro) {
                console.error("Erro ao processar ação:", erro)
                alert("Falha na comunicação com o servidor.")
                botao.innerHTML = iconeOriginal
            } finally {
                if (botao.title !== 'Match confirmado!') {
                    botao.disabled = false
                }
            }
        })
    })
}

function animarCurtida(botao: HTMLButtonElement): void {
    botao.classList.remove('curtida-animada')
    void botao.offsetWidth
    botao.classList.add('curtida-animada')
}

function configurarBotoesPerfil(vagas?: Vaga[], statusMatches: StatusMatchVaga[] = [], candidato?: Candidato): void {
    const botoesPerfil = document.querySelectorAll<HTMLButtonElement>('.btn-perfil-vaga')

    botoesPerfil.forEach(botao => {
        botao.addEventListener('click', () => {
            const vagaId = Number(botao.dataset.id)
            if (vagas) {
                const temMatch = statusMatches.some(status =>
                    status.idVaga === vagaId && status.status === 'match'
                )
                void renderPerfilVaga(vagaId, () => {
                    void renderListaVagas(candidato)
                }, temMatch)
            }
        })
    })
}