package br.com.zg.aceleraspec.dao

import br.com.zg.acelera.dao.ConexaoBD
import spock.lang.Specification

import java.sql.Connection

class ConexaoBDSpec extends Specification {

    def "deve conectar ao banco PostgreSQL"() {
        when:
        Connection conexao = ConexaoBD.conectar()

        then:
        conexao != null
        !conexao.isClosed()

        cleanup:
        conexao?.close()
    }
}