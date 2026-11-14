import type { RecrutadorSessao } from '../model/Recrutador.ts'

export async function renderPerfilRecrutador(recrutador: RecrutadorSessao): Promise<void> {
    const app = document.querySelector<HTMLDivElement>('#app')
    if (!app) return
    const nomeEmpresa = await buscarNomeEmpresa(recrutador.empresaId)
    app.innerHTML = `
        <section class="perfil-empresa tela-com-transicao">
            <h1>Perfil do Recrutador</h1>
            <div class="card-perfil card-perfil-empresa">
                <header class="perfil-cabecalho">
                    <h2>${escaparHtml(recrutador.nome)}</h2>
                    <p class="perfil-subtitulo">Recrutador</p>
                </header>
                <div class="perfil-grade">
                    <section class="perfil-bloco"><h3>Área de atuação</h3><p>${escaparHtml(recrutador.areaAtuacao)}</p></section>
                    <section class="perfil-bloco"><h3>Permissão</h3><p>${escaparHtml(recrutador.nivelPermissao)}</p></section>
                </div>
                <div class="perfil-resumo-empresa">
                    <p><strong>E-mail:</strong> <span>${escaparHtml(recrutador.email)}</span></p>
                    <p><strong>Empresa:</strong> <span>${escaparHtml(nomeEmpresa)}</span></p>
                </div>
            </div>
        </section>
    `
}

async function buscarNomeEmpresa(empresaId: string): Promise<string> {
    try {
        const resposta = await fetch(`http://localhost:8080/api/empresas?cnpj=${encodeURIComponent(empresaId)}`)
        if (!resposta.ok) return 'Empresa não identificada'

        const payload = await resposta.json() as { dados?: { nome?: string } } | { nome?: string }
        const nome = 'dados' in payload
            ? payload.dados?.nome
            : 'nome' in payload ? payload.nome : undefined
        return nome?.trim() || 'Empresa não identificada'
    } catch (erro) {
        console.error('Erro ao buscar nome da empresa do recrutador:', erro)
        return 'Empresa não identificada'
    }
}

function escaparHtml(valor: string | undefined): string {
    return (valor ?? '').replace(/[&<>"']/g, caractere => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    })[caractere]!)
}
