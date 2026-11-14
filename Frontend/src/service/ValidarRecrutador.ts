const REGEX_LETRAS_ESPACOS = /^[a-zA-ZÀ-ÿ\s]+$/

export class ValidarRecrutador {
    static validarNome(nome: string): void {
        if (!nome?.trim() || !REGEX_LETRAS_ESPACOS.test(nome)) {
            throw new Error('Nome do recrutador inválido.')
        }
    }

    static validarEmail(email: string): void {
        if (!email?.trim() || !email.includes('@')) {
            throw new Error('E-mail do recrutador inválido.')
        }
    }

    static validarCpf(cpf: string): string {
        const normalizado = cpf?.replace(/\D/g, '') ?? ''
        if (!/^\d{11}$/.test(normalizado)) {
            throw new Error('CPF deve conter 11 números.')
        }
        return normalizado
    }

    static validarDataNascimento(dataNascimento: string): void {
        if (!/^\d{4}-\d{2}-\d{2}$/.test(dataNascimento ?? '')) {
            throw new Error('Data de nascimento deve estar no formato AAAA-MM-DD.')
        }
        const data = new Date(`${dataNascimento}T00:00:00`)
        if (Number.isNaN(data.getTime()) || data > new Date()) {
            throw new Error('Data de nascimento inválida.')
        }
        const hoje = new Date()
        let idade = hoje.getFullYear() - data.getFullYear()
        const aniversario = new Date(hoje.getFullYear(), data.getMonth(), data.getDate())
        if (aniversario > hoje) idade--
        if (idade < 18) throw new Error('O recrutador deve ter pelo menos 18 anos.')
    }

    static validarSenha(senha: string): void {
        if (!senha || senha.length < 6 || senha.length > 8) {
            throw new Error('A senha deve ter entre 6 e 8 caracteres.')
        }
    }
}
