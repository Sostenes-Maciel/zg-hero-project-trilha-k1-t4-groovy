import type { Recrutador, RecrutadorSessao } from '../model/Recrutador.ts'

const API = 'http://localhost:8080/api/recrutadores'
const CHAVE_USUARIO = 'usuarioLogado'

export async function autenticarRecrutador(email: string, senha: string): Promise<RecrutadorSessao> {
    const resposta = await fetch(`${API}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, senha })
    })
    const payload = await resposta.json() as { recrutador?: Recrutador, mensagem?: string }
    if (!resposta.ok || !payload.recrutador) {
        throw new Error(payload.mensagem ?? 'Não foi possível autenticar o recrutador.')
    }

    const recrutador = payload.recrutador
    const sessao: RecrutadorSessao = {
        id: recrutador.id,
        empresaId: recrutador.empresa_id ?? (recrutador as Recrutador & { empresaId?: string }).empresaId ?? '',
        nome: recrutador.nome,
        areaAtuacao: recrutador.area_atuacao ?? (recrutador as Recrutador & { areaAtuacao?: Recrutador['area_atuacao'] }).areaAtuacao!,
        nivelPermissao: recrutador.nivel_permissao ?? (recrutador as Recrutador & { nivelPermissao?: Recrutador['nivel_permissao'] }).nivelPermissao!,
        email: recrutador.email,
        ativo: recrutador.ativo
    }
    localStorage.setItem(CHAVE_USUARIO, JSON.stringify(sessao))
    localStorage.setItem('tipoUsuario', 'recrutador')
    return sessao
}

export function obterRecrutadorLogado(): RecrutadorSessao | null {
    const salvo = localStorage.getItem(CHAVE_USUARIO)
    if (!salvo || localStorage.getItem('tipoUsuario') !== 'recrutador') return null
    try {
        const sessao = JSON.parse(salvo) as RecrutadorSessao
        return {
            id: sessao.id,
            empresaId: sessao.empresaId,
            nome: sessao.nome,
            areaAtuacao: sessao.areaAtuacao,
            nivelPermissao: sessao.nivelPermissao,
            email: sessao.email,
            ativo: sessao.ativo
        }
    } catch {
        localStorage.removeItem(CHAVE_USUARIO)
        return null
    }
}

export function encerrarSessaoRecrutador(): void {
    if (localStorage.getItem('tipoUsuario') === 'recrutador') {
        localStorage.removeItem(CHAVE_USUARIO)
        localStorage.removeItem('tipoUsuario')
    }
}
