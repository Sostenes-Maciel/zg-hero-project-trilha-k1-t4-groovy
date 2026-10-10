const REGEX_LETRAS_ESPACOS = /^[a-zA-ZÀ-ÿ\s]+$/
const REGEX_EMAIL = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/
const REGEX_CPF = /^\d{11}$/
const REGEX_CEP_BR = /^\d{8}$/
const REGEX_CODIGO_POSTAL_GERAL = /^[A-Za-z0-9][A-Za-z0-9 -]{2,14}$/

export class ValidarCandidato {

    static validarNome(nome: string): void {
        if (!nome?.trim() || !REGEX_LETRAS_ESPACOS.test(nome)) {
            throw new Error('Nome inválido.')
        }
    }

    static validarEmail(email: string): void {
        if (!email?.trim() || !REGEX_EMAIL.test(email)) {
            throw new Error('E-mail inválido.')
        }
    }

    static validarCpf(cpf: string): void {
        if (!cpf?.trim()) {
            throw new Error('O CPF é obrigatório.')
        }

        const cpfLimpo = cpf.replace(/[.-]/g, '')
        if (!REGEX_CPF.test(cpfLimpo)) {
            throw new Error('CPF deve conter 11 números.')
        }
    }

    static validarIdade(idade: number): void {
        if (idade < 18 || idade > 120) {
            throw new Error('Idade inválida. Deve ter entre 18 e 120 anos.')
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

    static validarPais(pais: string): void {
        if (!pais?.trim()) {
            throw new Error('O país é obrigatório.')
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

        // Validação Genérica Internacional
        if (!REGEX_CODIGO_POSTAL_GERAL.test(valorLimpo)) {
            throw new Error('Código postal inválido.')
        }
    }
}