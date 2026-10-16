package br.com.zg.acelera.utils

class ValidacaoUtils {

    static void validarTamanhoNumerico(String valor, int tamanhoExato, String nomeCampo) {
        String limpo = valor ? valor.replaceAll(/\D/, '') : ''

        if (!limpo.matches("\\d{" + tamanhoExato + "}")) {
            throw new IllegalArgumentException("${nomeCampo} deve conter ${tamanhoExato} números.")
        }
    }
}