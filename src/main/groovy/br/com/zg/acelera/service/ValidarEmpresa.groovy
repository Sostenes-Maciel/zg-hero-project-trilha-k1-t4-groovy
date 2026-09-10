package br.com.zg.acelera.service

class ValidarEmpresa {

    static void validarNome(String nome) {
        if (!nome || !nome.matches(/[a-zA-ZÀ-ÿ\s]+/)) {
            throw new IllegalArgumentException("Nome da empresa inválido.")
        }
    }

    static void validarEmail(String email) {
        if (!email.matches(/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/)) {
            throw new IllegalArgumentException("E-mail inválido.")
        }
    }

    static void validarCnpj(String cnpj) {
        cnpj = cnpj.replaceAll(/[.\/-]/, '')
        if (!cnpj.matches(/\d{14}/)) {
            throw new IllegalArgumentException("CNPJ deve conter 14 números.")
        }
    }

    static void validarPais(String pais) {
        if (!pais || !pais.matches(/[a-zA-ZÀ-ÿ\s]+/)) {
            throw new IllegalArgumentException("País inválido.")
        }
    }

    static void validarEstado(String estado) {
        if (!estado || !estado.matches(/[a-zA-ZÀ-ÿ\s]+/)) {
            throw new IllegalArgumentException("Estado inválido.")
        }
    }

    static void validarCep(String cep) {
        cep = cep.replaceAll(/[.-]/, '')

        if (!cep.matches(/\d{8}/)) {
            throw new IllegalArgumentException("CEP deve conter 8 números.")
        }
    }

    static void validarDescricao(String descricao) {
        if (!descricao) {
            throw new IllegalArgumentException("Descrição não pode estar vazia.")
        }
    }

    static void validarCompetencias(List<String> competencias) {
        if (!competencias || competencias.isEmpty()) {
            throw new IllegalArgumentException("Informe pelo menos uma competência.")
        }
    }
}
