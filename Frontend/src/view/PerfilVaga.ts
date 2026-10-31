import type { Vaga } from "../model/Vaga.ts"

type VagaDaApi = Omit<Vaga, 'competencias'> & {
    competencias?: string[]
    competencia?: string[]
}

export async function renderPerfilVaga(vagaId: number, onVoltar?: () => void, temMatch: boolean = false): Promise<void> {
    const app = document.querySelector<HTMLDivElement>('#app')
    if (!app) return

    app.innerHTML = '<section class="perfil-vaga"><p>Carregando vaga...</p></section>'

    try {
        const resposta = await fetch(`http://localhost:8080/api/vagas?id=${encodeURIComponent(vagaId)}`)
        if (resposta.status === 404) {
            renderizarMensagem(app, 'Vaga não encontrada!', onVoltar)
            return
        }
        if (!resposta.ok) {
            throw new Error(`Falha ao buscar vaga (${resposta.status}).`)
        }

        const resultado: unknown = await resposta.json()
        const vagaDaApi = extrairVagaDaApi(resultado)
        if (!vagaDaApi) {
            renderizarMensagem(app, 'Vaga não encontrada!', onVoltar)
            return
        }

        const vaga: Vaga = {
            ...vagaDaApi,
            competencias: vagaDaApi.competencias ?? vagaDaApi.competencia ?? []
        }

        renderizarPerfilVagaHTML(app, vaga, temMatch)
        configurarEventoVoltar(onVoltar)
    } catch (erro) {
        console.error('Erro ao buscar detalhes da vaga:', erro)
        renderizarMensagem(app, 'Não foi possível carregar os detalhes da vaga.', onVoltar)
    }
}

function extrairVagaDaApi(resultado: unknown): VagaDaApi | undefined {
    if (typeof resultado !== 'object' || resultado === null) return undefined

    const dados = 'dados' in resultado ? resultado.dados : resultado
    return validarVagaDaApi(dados) ? dados : undefined
}

function validarVagaDaApi(valor: unknown): valor is VagaDaApi {
    return typeof valor === 'object'
        && valor !== null
        && 'id' in valor
        && typeof valor.id === 'number'
        && 'titulo' in valor
        && typeof valor.titulo === 'string'
        && 'pais' in valor
        && typeof valor.pais === 'string'
        && 'estado' in valor
        && typeof valor.estado === 'string'
        && 'descricao' in valor
        && typeof valor.descricao === 'string'
        && 'empresa' in valor
        && typeof valor.empresa === 'object'
        && valor.empresa !== null
}

function renderizarMensagem(app: HTMLDivElement, mensagem: string, onVoltar?: () => void): void {
    app.innerHTML = `
        <section class="perfil-vaga tela-com-transicao">
            <h2>${mensagem}</h2>
            <button id="btn-voltar-lista">Voltar para vagas</button>
        </section>
    `
    configurarEventoVoltar(onVoltar)
}

function renderizarPerfilVagaHTML(app: HTMLDivElement, vaga: Vaga, temMatch: boolean): void {
    const competenciasTexto = vaga.competencias?.join(', ') || 'Nenhuma competência especificada'
    const nomeEmpresa = temMatch ? vaga.empresa.nome : 'Match necessário para visualização'

    app.innerHTML = `
        <section class="perfil-vaga tela-com-transicao">
            <h1>Detalhes da Vaga</h1>
            
            <div class="card-vaga">
                <h2>${vaga.titulo}</h2>
                <hr>
                
                <h3>Informações da Vaga</h3>
                <p>
                    <strong>Descrição:</strong>
                    ${vaga.descricao}
                </p>
                
                <p>
                    <strong>Local:</strong>
                    ${vaga.pais} - ${vaga.estado}
                </p>
                
                <p>
                    <strong>Competências:</strong>
                    ${competenciasTexto}
                </p>
                
                <hr>
                
                <h3>Informações da Empresa</h3>
                <p>
                    <strong>Empresa:</strong>
                    ${nomeEmpresa}
                </p>
            </div>
            
            <button id="btn-voltar-lista">Voltar para Vagas</button>
        </section>
    `
}

function configurarEventoVoltar(onVoltar?: () => void): void {
    document.querySelector('#btn-voltar-lista')?.addEventListener('click', () => {
        onVoltar?.()
    })
}