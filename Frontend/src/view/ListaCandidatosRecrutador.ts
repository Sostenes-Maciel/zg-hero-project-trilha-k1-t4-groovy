import type { RecrutadorSessao } from '../model/Recrutador.ts'
import type { Candidato } from '../model/Candidato.ts'
import { calcularAfinidadePorCompetencias } from '../service/CalcularAfinidade.ts'

export async function renderListaCandidatosRecrutador(recrutador: RecrutadorSessao): Promise<void> {
    const app = document.querySelector<HTMLDivElement>('#app')
    if (!app) return
    app.innerHTML = `
        <section class="lista-candidatos-recrutador tela-com-transicao">
            <h1>Candidatos da minha área</h1>
            <table class="tabela-candidatos">
                <thead><tr><th>Candidato</th><th>Competências</th><th>Estado</th><th>Afinidade</th><th>Ação</th></tr></thead>
                <tbody id="lista-candidatos-recrutador"><tr><td colspan="5">Carregando candidatos...</td></tr></tbody>
            </table>
        </section>
    `
    try {
        const resposta = await fetch(`http://localhost:8080/api/candidatos?area_atuacao=${encodeURIComponent(recrutador.areaAtuacao)}`)
        if (!resposta.ok) throw new Error(`HTTP ${resposta.status}`)
        const payload = await resposta.json() as { dados?: Candidato[] } | Candidato[]
        const candidatos = Array.isArray(payload) ? payload : payload.dados ?? []
        const lista = document.querySelector<HTMLTableSectionElement>('#lista-candidatos-recrutador')
        if (!lista) return
        lista.innerHTML = candidatos.length
            ? candidatos.map(candidato => {
                const cpf = candidato.cpf?.replace(/\D/g, '') ?? ''
                return `
                    <tr>
                        <td>Candidato Anônimo</td>
                        <td>${escaparHtml(Array.isArray(candidato.competencias) ? candidato.competencias.join(', ') : 'Não informadas')}</td>
                        <td>${escaparHtml(candidato.estado || 'Não informado')}</td>
                        <td>${calcularAfinidadePorCompetencias(candidato.competencias, [])}%</td>
                        <td>
                            <button class="btn-curtir-candidato" data-cpf="${cpf}" ${cpf ? '' : 'disabled'}>♡</button>
                        </td>
                    </tr>
                `
            }).join('')
            : '<tr><td colspan="5" class="tabela-vazia">Nenhum candidato encontrado.</td></tr>'
    } catch (erro) {
        console.error('Erro ao carregar candidatos do recrutador:', erro)
        document.querySelector<HTMLTableSectionElement>('#lista-candidatos-recrutador')!.innerHTML =
            '<tr><td colspan="5" class="tabela-vazia">Não foi possível carregar os candidatos.</td></tr>'
    }
}

function escaparHtml(valor: string): string {
    return valor.replace(/[&<>"']/g, caractere => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    })[caractere]!)
}
