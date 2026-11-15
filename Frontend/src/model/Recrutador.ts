export type AreaAtuacao = 'TI' | 'RH' | 'Marketing' | 'Vendas' | 'Segurança'
export type NivelPermissao = 'Total' | 'Limitado'

export interface Recrutador {
    id: number
    empresa_id: string
    nome: string
    area_atuacao: AreaAtuacao
    nivel_permissao: NivelPermissao
    descricao?: string
    email: string
    cpf?: string
    dataNascimento?: string
    senha?: string
    senhaHash?: string
    ativo: boolean
}

export interface RecrutadorSessao {
    id: number
    empresaId: string
    nome: string
    areaAtuacao: AreaAtuacao
    nivelPermissao: NivelPermissao
    email: string
    ativo: boolean
}
