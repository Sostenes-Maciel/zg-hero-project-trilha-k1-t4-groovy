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
}