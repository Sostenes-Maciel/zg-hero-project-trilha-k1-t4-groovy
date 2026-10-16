package br.com.zg.acelera.dao

import br.com.zg.acelera.interfaces.IConexao

class ConexaoFactory {

    static IConexao getBancoDeDados(String tipoBanco) {
        if (tipoBanco.equalsIgnoreCase("POSTGRES")) {
            return ConexaoPostgres.getInstancia()
        }
        throw new IllegalArgumentException("Banco de dados não suportado: " + tipoBanco)
    }
}