export class ValidarCandidato {

    static validarNome(nome: string): void {
        if (!nome || !/^[a-zA-ZÀ-ÿ\s]+$/.test(nome)) {
            throw new Error('Nome inválido.')
        }
    }

    static validarEmail(email: string): void {
        if (!/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(email)) {
            throw new Error('E-mail inválido.')
        }
    }

    static validarCpf(cpf: string): void {
        const cpfLimpo = cpf.replace(/[.-]/g, '')

        if (!/^\d{11}$/.test(cpfLimpo)) {
            throw new Error('CPF deve conter 11 números.')
        }
    }

    static validarIdade(idade: number): void {
        if (idade < 18 || idade > 120) {
            throw new Error('Idade inválida.')
        }
    }

    static validarEstado(estado: string): void {
        if (!estado || !/^[a-zA-ZÀ-ÿ\s]+$/.test(estado)) {
            throw new Error('Estado inválido.')
        }
    }

    static validarCep(cep: string): void {
        const cepLimpo = cep.replace(/[.-]/g, '')

        if (!/^\d{8}$/.test(cepLimpo)) {
            throw new Error('CEP deve conter 8 números.')
        }
    }

    static validarDescricao(descricao: string): void {
        if (!descricao) {
            throw new Error('Descrição não pode estar vazia.')
        }
    }

    static validarCompetencias(competencias: string[]): void {
        if (!competencias || competencias.length === 0) {
            throw new Error('Informe pelo menos uma competência.')
        }
    }

    static validarPais(pais: string): void {
        if (!pais || pais.trim().length === 0) {
            throw new Error('O país é obrigatório.')
        }
    }

    static validarCodigoPostal(
        codigoPostal: string,
        codigoPais: string
    ): void {
        const valor = codigoPostal.trim()

        if (valor.length === 0) {
            throw new Error('O código postal é obrigatório.')
        }

        if (codigoPais === 'BR') {
            const codigoLimpo = valor.replace('-', '')

            if (!/^\d{8}$/.test(codigoLimpo)) {
                throw new Error('No Brasil, o código postal deve conter 8 números.')
            }

            return
        }

        if (!/^[A-Za-z0-9][A-Za-z0-9 -]{2,14}$/.test(valor)) {
            throw new Error('Código postal inválido.')
        }
    }
}