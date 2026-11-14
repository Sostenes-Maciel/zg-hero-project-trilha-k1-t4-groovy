import type { RecrutadorSessao } from '../model/Recrutador.ts'
import type { Match } from '../model/Match.ts'
import type { Curtida } from '../model/Curtida.ts'

export async function renderListaMatchesRecrutador(recrutador: RecrutadorSessao): Promise<void> {
    const app = document.querySelector<HTMLDivElement>('#app')
    if (!app) return
    app.innerHTML = '<section class="lista-matches tela-com-transicao"><h2>Carregando matches...</h2></section>'
    try {
        const resposta = await fetch(`http://localhost:8080/api/matches?cnpj=${encodeURIComponent(recrutador.empresaId.replace(/\D/g, ''))}&completo=true`)
        if (!resposta.ok) throw new Error(`HTTP ${resposta.status}`)
        const dados = await resposta.json() as { matches?: Match[]; curtidasPendentes?: Curtida[] }
        const matches = deduplicarMatches(dados.matches ?? [])
        const pendentes = deduplicarCurtidas(dados.curtidasPendentes ?? [])
        app.innerHTML = `
            <section class="lista-matches lista-matches-recrutador tela-com-transicao">
                <h1>Matches da minha empresa</h1>
                <h2>Curtidas aguardando Match</h2>${renderPendentes(pendentes)}
                <h2>Matches confirmados</h2>${renderMatches(matches)}
            </section>
        `
    } catch (erro) {
        console.error('Erro ao carregar matches do recrutador:', erro)
        app.innerHTML = '<section class="lista-matches"><h2>Não foi possível carregar os matches.</h2></section>'
    }
}

function deduplicarMatches(matches: Match[]): Match[] {
    const vistos = new Set<string>()
    return matches.filter(match => {
        const chave = `${match.candidato?.cpf ?? ''}|${match.empresa?.cnpj ?? ''}|${match.vaga?.id ?? ''}`
        if (vistos.has(chave)) return false
        vistos.add(chave)
        return true
    })
}

function deduplicarCurtidas(curtidas: Curtida[]): Curtida[] {
    const vistos = new Set<string>()
    return curtidas.filter(curtida => {
        const chave = `${curtida.candidato?.cpf ?? ''}|${curtida.empresa?.cnpj ?? ''}|${curtida.vaga?.id ?? ''}`
        if (vistos.has(chave)) return false
        vistos.add(chave)
        return true
    })
}

function renderPendentes(curtidas: Curtida[]): string {
    if (!curtidas.length) return '<div class="estado-vazio"><p>Nenhuma curtida pendente.</p></div>'
    return `<div class="cards-matches">${curtidas.map(curtida => `
        <div class="card-match card-curtida-pendente">
            <h3>♥ Curtida pendente</h3>
            ${curtida.vaga?.titulo ? `<p><strong>Vaga:</strong> ${escaparHtml(curtida.vaga.titulo)}</p>` : ''}
            <p><strong>Candidato:</strong> Candidato Anônimo</p>
        </div>`).join('')}</div>`
}

function renderMatches(matches: Match[]): string {
    if (!matches.length) return '<div class="estado-vazio"><p>Nenhum Match ainda.</p></div>'
    return `<div class="cards-matches">${matches.map(match => `
        <div class="card-match card-match-confirmado">
            <h3>🤝 ${escaparHtml(match.candidato?.nome ?? 'Candidato')}</h3>
            <p><strong>Vaga:</strong> ${escaparHtml(match.vaga?.titulo ?? 'Vaga')}</p>
            <p><strong>Local:</strong> ${escaparHtml([match.vaga?.pais, match.vaga?.estado].filter(Boolean).join(' - ') || 'Não informado')}</p>
        </div>`).join('')}</div>`
}

function escaparHtml(valor: string): string {
    return valor.replace(/[&<>"']/g, caractere => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    })[caractere]!)
}
