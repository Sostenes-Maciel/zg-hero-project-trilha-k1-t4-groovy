package br.com.zg.acelera.service

class ValidarEmpresaService {

    static void validarNome(String nome) {
        validarApenasLetras(nome, "Nome da empresa")
    }

    static void validarEmail(String email) {
        if (!email.matches(/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/)) {
            throw new IllegalArgumentException("E-mail inválido.")
        }
    }

    static void validarCnpj(String cnpj) {
        validarTamanhoNumerico(cnpj, 14, "CNPJ")
    }

    static void validarPais(String pais) {
        validarApenasLetras(pais, "País")
    }

    static void validarEstado(String estado) {
        validarApenasLetras(estado, "Estado")
    }

    static void validarCep(String cep) {
        validarTamanhoNumerico(cep, 8, "CEP")
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
    private static void validarApenasLetras(String texto, String nomeCampo) {
        if (!texto || !texto.matches(/[a-zA-ZÀ-ÿ\s]+/)) {
            throw new IllegalArgumentException("${nomeCampo} inválido.")
        }
    }

    private static void validarTamanhoNumerico(String valor, int tamanhoExato, String nomeCampo) {
        String limpo = valor ? valor.replaceAll(/[.\/-]/, '') : ''
        if (!limpo.matches("\\d{" + tamanhoExato + "}")) {
            throw new IllegalArgumentException("${nomeCampo} deve conter ${tamanhoExato} números.")
        }
    }
}
