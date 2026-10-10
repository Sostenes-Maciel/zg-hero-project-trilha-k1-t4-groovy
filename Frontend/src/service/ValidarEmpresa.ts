const REGEX_LETRAS_ESPACOS = /^[a-zA-ZÀ-ÿ\s]+$/
const REGEX_EMAIL = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/
const REGEX_CNPJ = /^\d{14}$/
const REGEX_CEP_BR = /^\d{8}$/
const REGEX_CODIGO_POSTAL_GERAL = /^[A-Za-z0-9][A-Za-z0-9 -]{2,14}$/

export class ValidarEmpresa {

    static validarNome(nome: string): void {
        if (!nome?.trim() || !REGEX_LETRAS_ESPACOS.test(nome)) {
            throw new Error('Nome da empresa inválido.')
        }
    }

    static validarEmail(email: string): void {
        if (!email?.trim() || !REGEX_EMAIL.test(email)) {
            throw new Error('E-mail inválido.')
        }
    }

    static validarCnpj(cnpj: string): void {
        if (!cnpj?.trim()) {
            throw new Error('O CNPJ é obrigatório.')
        }

        const cnpjLimpo = cnpj.replace(/[./-]/g, '')
        if (!REGEX_CNPJ.test(cnpjLimpo)) {
            throw new Error('CNPJ deve conter 14 números.')
        }
    }

    static validarPais(pais: string): void {
        if (!pais?.trim()) {
            throw new Error('O país é obrigatório.')
        }
    }

    static validarEstado(estado: string): void {
        if (!estado?.trim() || !REGEX_LETRAS_ESPACOS.test(estado)) {
            throw new Error('Estado inválido.')
        }
    }

    static validarDescricao(descricao: string): void {
        if (!descricao?.trim()) {
            throw new Error('A descrição não pode estar vazia.')
        }
    }

    static validarCompetencias(competencias: string[]): void {
        if (!competencias || competencias.length === 0) {
            throw new Error('Informe pelo menos uma competência.')
        }
    }

    static validarCodigoPostal(codigoPostal: string, codigoPais: string): void {
        const valorLimpo = codigoPostal?.trim()

        if (!valorLimpo) {
            throw new Error('O código postal é obrigatório.')
        }

        if (codigoPais === 'BR') {
            const cepApenasNumeros = valorLimpo.replace('-', '')
            if (!REGEX_CEP_BR.test(cepApenasNumeros)) {
                throw new Error('No Brasil, o código postal (CEP) deve conter 8 números.')
            }
            return
        }

        if (!REGEX_CODIGO_POSTAL_GERAL.test(valorLimpo)) {
            throw new Error('Código postal inválido.')
        }
    }
}