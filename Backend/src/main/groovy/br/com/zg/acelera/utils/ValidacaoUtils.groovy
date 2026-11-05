package br.com.zg.acelera.utils

class ValidacaoUtils {

    static String validarTamanhoNumerico(String valor, int tamanhoExato, String nomeCampo) {
        String limpo = valor ? valor.replaceAll(/\D/, '') : ''

        if (!limpo.matches("\\d{" + tamanhoExato + "}")) {
            throw new IllegalArgumentException("${nomeCampo} deve conter ${tamanhoExato} números.")
        }

        return limpo
    }

    static String limparFormatacao(String valor) {
        return valor ? valor.replaceAll(/\D/, "") : ""
    }

    static void validarCompetencias(List<String> competencias) {
        if (!competencias || competencias.isEmpty()) {
            throw new IllegalArgumentException(
                    "Informe pelo menos uma competência."
            )
        }

        def duplicadas = competencias
                .findAll { it != null && it.trim() }
                .groupBy { it.trim().toLowerCase(Locale.ROOT) }
                .values()
                .findAll { it.size() > 1 }
                .flatten()

        if (duplicadas) {
            throw new IllegalArgumentException(
                    "Competências repetidas não são permitidas: ${duplicadas.join(', ')}."
            )
        }
    }

    static void validarApenasLetras(String texto, String nomeCampo) {
        if (!texto || !texto.matches(/[a-zA-ZÀ-ÿ\s]+/)) {
            throw new IllegalArgumentException("${nomeCampo} inválido.")
        }
    }

     static List<String> extrairCompetencias(String entrada) {
        if (!entrada) return []

        return entrada.split(",")
                .collect { it.trim() }
                .findAll { it }
    }


}