import type { AreaAtuacao, NivelPermissao, Recrutador } from '../model/Recrutador.ts'
import { ValidarRecrutador } from './ValidarRecrutador.ts'

const API = 'http://localhost:8080/api/recrutadores'

export type CadastroRecrutador = Omit<Recrutador, 'id' | 'ativo' | 'senhaHash'> & {
    empresaId: string
    area_atuacao: AreaAtuacao
    nivel_permissao: NivelPermissao
    senha: string
}

export async function cadastrarRecrutador(dados: CadastroRecrutador): Promise<void> {
    validarCadastro(dados)
    const resposta = await fetch(API, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            empresaId: dados.empresaId,
            nome: dados.nome,
            areaAtuacao: dados.area_atuacao,
            nivelPermissao: dados.nivel_permissao,
            descricao: dados.descricao,
            email: dados.email,
            cpf: ValidarRecrutador.validarCpf(dados.cpf ?? ''),
            dataNascimento: dados.dataNascimento,
            senha: dados.senha
        })
    })
    if (!resposta.ok) {
        const erro = await resposta.json() as { mensagem?: string }
        throw new Error(erro.mensagem ?? 'Não foi possível cadastrar o recrutador.')
    }
}

function validarCadastro(dados: CadastroRecrutador): void {
    ValidarRecrutador.validarNome(dados.nome)
    ValidarRecrutador.validarEmail(dados.email)
    ValidarRecrutador.validarCpf(dados.cpf ?? '')
    ValidarRecrutador.validarDataNascimento(dados.dataNascimento ?? '')
    ValidarRecrutador.validarSenha(dados.senha)
    if (!dados.empresaId?.trim()) throw new Error('Empresa é obrigatória.')
}
