import type { RecrutadorSessao } from '../model/Recrutador.ts'
import type { Candidato } from '../model/Candidato.ts'
import type { Vaga } from '../model/Vaga.ts'
import { calcularAfinidadePorCompetencias } from '../service/CalcularAfinidade.ts'
import { mostrarAnimacaoMatch } from './AnimacaoMatch.ts'

// 1. Tipagem para a memória de status
interface StatusMatch {
    cpfCandidato: string
    idVaga: number
    status: 'curtido' | 'match'
}

export async function renderListaCandidatosRecrutador(recrutador: RecrutadorSessao): Promise<void> {
    const app = document.querySelector<HTMLDivElement>('#app')
    if (!app) return

    app.innerHTML = `
        <section class="lista-candidatos-recrutador tela-com-transicao">
            <h1>Candidatos da minha área</h1>
            <table class="tabela-candidatos">
                <thead><tr><th>Candidato</th><th>Competências</th><th>Estado</th><th>Afinidade</th><th>Ação</th><th>Vaga</th></tr></thead>
                <tbody id="lista-candidatos-recrutador"><tr><td colspan="6">Carregando candidatos e vagas...</td></tr></tbody>
            </table>
        </section>
    `

    try {
        const cnpjEmpresa = recrutador.empresaId.replace(/\D/g, '')

        // 2. Agora buscamos TRÊS coisas: Candidatos, Vagas e o Histórico de Status!
        const [resCandidatos, resVagas, resStatus] = await Promise.all([
            fetch(`http://localhost:8080/api/candidatos?area_atuacao=${encodeURIComponent(recrutador.areaAtuacao)}`),
            fetch(`http://localhost:8080/api/vagas?cnpj=${cnpjEmpresa}`),
            fetch(`http://localhost:8080/api/matches?cnpj=${cnpjEmpresa}`)
        ])

        if (!resCandidatos.ok || !resVagas.ok) throw new Error('Falha ao carregar dados principais.')

        const payloadCandidatos = await resCandidatos.json()
        const payloadVagas = await resVagas.json()
        // Pega o status, se a rota falhar por algum motivo, inicia como array vazio
        const payloadStatus = resStatus.ok ? await resStatus.json() : []

        const candidatos: Candidato[] = Array.isArray(payloadCandidatos) ? payloadCandidatos : payloadCandidatos.dados ?? []
        const vagasBrutas: Vaga[] = Array.isArray(payloadVagas) ? payloadVagas : payloadVagas.dados ?? []
        const statusMatches: StatusMatch[] = Array.isArray(payloadStatus) ? payloadStatus : payloadStatus.dados ?? []

        const areaRecrutador = (recrutador.areaAtuacao || '').trim().toLowerCase()
        const vagas = vagasBrutas.filter(v => (v as any).areaAtuacao?.trim().toLowerCase() === areaRecrutador)
        const opcoesVaga = vagas.map(v => `<option value="${v.id}">${v.titulo}</option>`).join('')

        const lista = document.querySelector<HTMLTableSectionElement>('#lista-candidatos-recrutador')
        if (!lista) return

        lista.innerHTML = candidatos.length
            ? candidatos.map(candidato => {
                const cpf = candidato.cpf?.replace(/\D/g, '') ?? ''
                return `
                    <tr data-nome-real="${candidato.nome}">
                        <td class="coluna-candidato">Candidato Oculto</td>
                        <td>${escaparHtml(Array.isArray(candidato.competencias) ? candidato.competencias.join(', ') : 'Não informadas')}</td>
                        <td>${escaparHtml(candidato.estado || 'Não informado')}</td>
                        <td>${calcularAfinidadePorCompetencias(candidato.competencias, [])}%</td>
                        
                        <td>
                            <button class="btn-curtir-candidato" 
                                    style="background: transparent; border: none; cursor: pointer; font-size: 1.5rem;" 
                                    data-cpf="${cpf}" 
                                    data-curtido="false"
                                    title="Selecione uma vaga primeiro" 
                                    disabled>
                                ♡
                            </button>
                        </td>
                        
                        <td>
                            <select class="select-vaga-curtir" data-cpf="${cpf}" style="padding: 4px; border-radius: 4px;">
                                <option value="" selected disabled>
                                    ${vagas.length > 0 ? 'Selecionar vaga...' : 'Nenhuma vaga na sua área'}
                                </option>
                                ${opcoesVaga}
                            </select>
                        </td>
                    </tr>
                `
            }).join('')
            : '<tr><td colspan="6" class="tabela-vazia">Nenhum candidato encontrado.</td></tr>'

        // 3. Passamos a memória de status para a função que configura os botões
        configurarBotoesCurtir(recrutador, statusMatches)

    } catch (erro) {
        console.error('Erro ao carregar candidatos do recrutador:', erro)
        document.querySelector<HTMLTableSectionElement>('#lista-candidatos-recrutador')!.innerHTML =
            '<tr><td colspan="6" class="tabela-vazia">Não foi possível carregar os dados.</td></tr>'
    }
}

function configurarBotoesCurtir(recrutador: RecrutadorSessao, statusMatches: StatusMatch[]): void {
    const botoes = document.querySelectorAll<HTMLButtonElement>('.btn-curtir-candidato')

    botoes.forEach(botao => {
        const linha = botao.closest('tr')
        const seletor = linha?.querySelector<HTMLSelectElement>('.select-vaga-curtir')
        const cpfCandidato = botao.dataset.cpf ?? ''

        // =========================================================
        // EVENTO 1: SELETOR DE VAGAS (Busca na memória)
        // =========================================================
        seletor?.addEventListener('change', () => {
            const idVagaSelecionada = Number(seletor.value)

            // Procura se já existe um histórico para esse CPF e essa Vaga
            const historico = statusMatches.find(s =>
                (s.cpfCandidato?.replace(/\D/g, '') === cpfCandidato) && (s.idVaga === idVagaSelecionada)
            )

            const celulaNome = linha?.querySelector('.coluna-candidato')

            if (historico?.status === 'match') {
                // JÁ ERA MATCH!
                botao.disabled = true
                botao.innerHTML = '🤝'
                botao.style.color = ''
                botao.dataset.curtido = 'true'
                botao.title = 'Match!'
                if (celulaNome) celulaNome.textContent = (linha as HTMLElement).dataset.nomeReal || 'Candidato Oculto'

            } else if (historico?.status === 'curtido') {
                // JÁ ESTAVA CURTIDO!
                botao.disabled = false
                botao.innerHTML = '♥'
                botao.style.color = 'red'
                botao.dataset.curtido = 'true'
                botao.title = 'Descurtir'
                if (celulaNome) celulaNome.textContent = 'Match necessário para visualização'

            } else {
                // NÃO TEM HISTÓRICO (Vazio)
                botao.disabled = false
                botao.innerHTML = '♡'
                botao.style.color = ''
                botao.dataset.curtido = 'false'
                botao.title = 'Curtir Candidato'
                if (celulaNome) celulaNome.textContent = 'Match necessário para visualização'
            }
        })

        // =========================================================
        // EVENTO 2: CLIQUE NO CORAÇÃO (Grava no banco e atualiza memória)
        // =========================================================
        botao.addEventListener('click', async (e) => {
            e.preventDefault()
            const idVaga = Number(seletor?.value)
            const jaCurtido = botao.dataset.curtido === 'true'

            if (!idVaga) {
                alert('Selecione uma vaga antes de curtir o candidato.')
                return
            }

            botao.disabled = true
            const iconeOriginal = botao.innerHTML
            botao.innerHTML = '...'

            try {
                const payload = {
                    autor: "RECRUTADOR",
                    idRecrutador: recrutador.id,
                    cnpjEmpresa: recrutador.empresaId.replace(/\D/g, ''),
                    cpfCandidato: cpfCandidato,
                    idVaga: idVaga
                }

                const metodoHTTP = jaCurtido ? 'DELETE' : 'POST'

                const resposta = await fetch('http://localhost:8080/api/matches', {
                    method: metodoHTTP,
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                })

                if (resposta.ok) {
                    // ATUALIZA A MEMÓRIA LOCAL PARA NÃO PRECISAR DO F5
                    const indexMemoria = statusMatches.findIndex(s => s.cpfCandidato.replace(/\D/g, '') === cpfCandidato && s.idVaga === idVaga)

                    if (jaCurtido) {
                        // SUCESSO AO DESCURTIR
                        botao.innerHTML = '♡'
                        botao.title = 'Curtir Candidato'
                        botao.dataset.curtido = 'false'
                        botao.style.color = ''
                        botao.disabled = false

                        // Remove da memória
                        if (indexMemoria !== -1) statusMatches.splice(indexMemoria, 1)

                    } else {
                        // SUCESSO AO CURTIR
                        const resultado = await resposta.json()
                        const deuMatch = resultado.match === true

                        botao.innerHTML = deuMatch ? '🤝' : '♥'
                        botao.title = deuMatch ? 'Match!' : 'Descurtir'
                        botao.dataset.curtido = 'true'
                        botao.style.color = 'red'

                        // Atualiza/Adiciona na memória
                        const novoStatus = deuMatch ? 'match' : 'curtido'
                        if (indexMemoria !== -1) {
                            statusMatches[indexMemoria].status = novoStatus
                        } else {
                            statusMatches.push({ cpfCandidato: cpfCandidato, idVaga: idVaga, status: novoStatus })
                        }

                        if (deuMatch) {
                            botao.disabled = true
                            mostrarAnimacaoMatch()
                            const celulaNome = linha?.querySelector('.coluna-candidato')
                            if (celulaNome && resultado.nomeRevelado) {
                                celulaNome.textContent = resultado.nomeRevelado
                                if (linha) (linha as HTMLElement).dataset.nomeReal = resultado.nomeRevelado
                            }
                        } else {
                            botao.disabled = false
                        }
                    }
                } else {
                    const erro = await resposta.json()
                    alert(`Erro: ${erro.mensagem}`)
                    botao.innerHTML = iconeOriginal
                    botao.disabled = false
                }
            } catch (erro) {
                console.error(erro)
                botao.innerHTML = iconeOriginal
                botao.disabled = false
            }
        })
    })
}

function escaparHtml(valor: string): string {
    return valor.replace(/[&<>"']/g, caractere => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    })[caractere]!)
}