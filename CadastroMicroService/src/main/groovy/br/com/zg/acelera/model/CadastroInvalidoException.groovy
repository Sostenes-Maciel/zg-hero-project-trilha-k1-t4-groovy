package br.com.zg.acelera.model

class CadastroInvalidoException extends RuntimeException {
    CadastroInvalidoException(String mensagem) {
        super(mensagem)
    }
}
