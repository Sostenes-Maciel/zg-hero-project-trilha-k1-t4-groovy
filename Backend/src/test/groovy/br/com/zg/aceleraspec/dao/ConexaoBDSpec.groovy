package br.com.zg.aceleraspec.dao

import br.com.zg.acelera.dao.ConexaoPostgres
import spock.lang.Specification

import java.sql.Connection

class ConexaoBDSpec extends Specification {

    def "deve conectar ao banco PostgreSQL"() {
        given: "a fabrica de conexao real"
        ConexaoPostgres fabricaDeConexao = new ConexaoPostgres()

        when: "solicitamos a abertura da conexao"
        Connection conexao = fabricaDeConexao.conectar()

        then: "a conexao deve ser retornada aberta"
        conexao != null
        !conexao.isClosed()

        cleanup:
        conexao?.close()
    }
}