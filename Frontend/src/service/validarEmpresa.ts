export class ValidarEmpresa {

    static validarNome(nome: string): void {
        if (!nome || !/^[a-zA-ZÀ-ÿ\s]+$/.test(nome)) {
            throw new Error('Nome da empresa inválido.')
        }
    }

    static validarEmail(email: string): void {
        if (!/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(email)) {
            throw new Error('E-mail inválido.')
        }
    }

    static validarCnpj(cnpj: string): void {
        const cnpjLimpo = cnpj.replace(/[./-]/g, '')

        if (!/^\d{14}$/.test(cnpjLimpo)) {
            throw new Error('CNPJ deve conter 14 números.')
        }
    }

    static validarPais(pais: string): void {
        if (!pais || !/^[a-zA-ZÀ-ÿ\s]+$/.test(pais)) {
            throw new Error('País inválido.')
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
            throw new Error(
                'Informe pelo menos uma competência.'
            )
        }
    }
}