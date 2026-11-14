import type { RecrutadorSessao } from '../model/Recrutador.ts'
import type { Vaga } from '../model/Vaga.ts'

export async function renderListaVagasRecrutador(recrutador: RecrutadorSessao): Promise<void> {
    const app = document.querySelector<HTMLDivElement>('#app')
    if (!app) return

    app.innerHTML = `
        <section class="lista-vagas lista-vagas-recrutador tela-com-transicao">
            <h1>Vagas da minha área</h1>
            <table class="tabela-vagas">
                <thead><tr><th>Vaga</th><th>Área</th><th>Descrição</th><th>Ação</th></tr></thead>
                <tbody id="lista-vagas-recrutador"><tr><td colspan="4">Carregando vagas...</td></tr></tbody>
            </table>
        </section>
    `

    try {
        const resposta = await fetch(`http://localhost:8080/api/vagas?cnpj=${encodeURIComponent(recrutador.empresaId)}`)
        if (!resposta.ok) throw new Error(`HTTP ${resposta.status}`)
        const payload = await resposta.json() as { dados?: Vaga[] } | Vaga[]
        const vagas = Array.isArray(payload) ? payload : payload.dados ?? []
        const areaRecrutador = normalizarArea(recrutador.areaAtuacao)
        const autorizadas = vagas.filter(vaga =>
            normalizarArea((vaga as Vaga & { areaAtuacao?: string }).areaAtuacao) === areaRecrutador
        )
        const lista = document.querySelector<HTMLTableSectionElement>('#lista-vagas-recrutador')
        if (!lista) return
        lista.innerHTML = autorizadas.length
            ? autorizadas.map(vaga => `
                <tr>
                    <td>${escaparHtml(vaga.titulo || 'Vaga')}</td>
                    <td>${escaparHtml((vaga as Vaga & { areaAtuacao?: string }).areaAtuacao || 'Não informada')}</td>
                    <td>${escaparHtml(vaga.descricao || 'Descrição não informada')}</td>
                    <td>
                        ${recrutador.nivelPermissao === 'Total'
                            ? `<button class="btn-editar-vaga-recrutador" data-id="${vaga.id}">Editar</button>`
                            : '<span class="acao-bloqueada">Somente leitura</span>'}
                    </td>
                </tr>
            `).join('')
            : '<tr><td colspan="4" class="tabela-vazia">Nenhuma vaga disponível para sua área.</td></tr>'
    } catch (erro) {
        console.error('Erro ao carregar vagas do recrutador:', erro)
        document.querySelector<HTMLTableSectionElement>('#lista-vagas-recrutador')!.innerHTML =
            '<tr><td colspan="4" class="tabela-vazia">Não foi possível carregar as vagas.</td></tr>'
    }
}

function normalizarArea(area: string | undefined): string {
    return (area ?? '').trim().toLocaleLowerCase('pt-BR')
}

function escaparHtml(valor: string): string {
    return valor.replace(/[&<>"']/g, caractere => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    })[caractere]!)
}
