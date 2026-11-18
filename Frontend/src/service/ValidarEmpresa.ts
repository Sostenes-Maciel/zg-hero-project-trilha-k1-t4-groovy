const REGEX_LETRAS_ESPACOS = /^[a-zA-ZÀ-ÿ\s]+$/
const REGEX_EMAIL = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/
const REGEX_CNPJ = /^\d{14}$/
const REGEX_CEP_BR = /^\d{8}$/
const REGEX_CODIGO_POSTAL_GERAL = /^[A-Za-z0-9][A-Za-z0-9 -]{2,14}$/
const REGEX_TEXTO_DESCRICAO = /^[a-zA-ZÀ-ÿ0-9\s.,!?()-]+$/
const REGEX_TAG = /^[a-zA-ZÀ-ÿ0-9\s#.\-]+$/

export class ValidarEmpresa {

    static validarNome(nome: string): void {
        if (!nome?.trim()) {
            throw new Error('O nome da empresa é obrigatório.')
        }
        const nomeLimpo = nome.trim()
        if (nomeLimpo.length < 2) {
            throw new Error('O nome da empresa deve ter pelo menos 2 caracteres.')
        }
        if (nomeLimpo.length > 100) {
            throw new Error('O nome da empresa deve ter no máximo 100 caracteres.')
        }
        if (!REGEX_LETRAS_ESPACOS.test(nomeLimpo)) {
            throw new Error('O nome da empresa deve conter apenas letras e espaços.')
        }
    }

    static validarEmail(email: string): void {
        if (!email?.trim()) {
            throw new Error('O e-mail é obrigatório.')
        }
        const emailLimpo = email.trim()
        if (!REGEX_EMAIL.test(emailLimpo)) {
            throw new Error('E-mail inválido.')
        }
    }

    static validarCnpj(cnpj: string): void {
        if (!cnpj?.trim()) {
            throw new Error('O CNPJ é obrigatório.')
        }
        const cnpjLimpo = cnpj.replace(/[.\/-]/g, '')
        if (!REGEX_CNPJ.test(cnpjLimpo)) {
            throw new Error('CNPJ deve conter 14 números.')
        }
    }

    static validarPais(pais: string): void {
        if (!pais?.trim()) {
            throw new Error('O país é obrigatório.')
        }
        const paisLimpo = pais.trim()
        if (!REGEX_LETRAS_ESPACOS.test(paisLimpo)) {
            throw new Error('O país deve conter apenas letras e espaços.')
        }
    }

    static validarEstado(estado: string): void {
        if (!estado?.trim()) {
            throw new Error('O estado é obrigatório.')
        }
        const estadoLimpo = estado.trim()
        if (!REGEX_LETRAS_ESPACOS.test(estadoLimpo)) {
            throw new Error('O estado deve conter apenas letras e espaços.')
        }
    }

    static validarDescricao(descricao: string): void {
        const descricaoLimpa = descricao?.trim()

        if (!descricaoLimpa) {
            throw new Error('A descrição não pode estar vazia.')
        }
        if (descricaoLimpa.length > 500) {
            throw new Error('A descrição deve ter no máximo 500 caracteres.')
        }
        if (!REGEX_TEXTO_DESCRICAO.test(descricaoLimpa)) {
            throw new Error('A descrição deve conter apenas texto (letras e pontuação básica).')
        }
    }

    static validarCompetencias(competencias: string[]): void {
        if (!competencias || competencias.length === 0) {
            throw new Error('Informe pelo menos uma competência.')
        }
        if (competencias.length > 10) {
            throw new Error('Informe no máximo 10 competências.')
        }

        for (const competencia of competencias) {
            const tag = competencia.trim()
            if (!tag) {
                throw new Error('Uma das competências/tags está vazia.')
            }
            if (!REGEX_TAG.test(tag)) {
                throw new Error(`A tag "${tag}" contém caracteres inválidos. Use apenas letras, números, espaços e hífens.`)
            }
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
