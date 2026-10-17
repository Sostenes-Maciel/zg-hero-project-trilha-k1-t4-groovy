package br.com.zg.aceleraspec.dao

import spock.lang.Specification
import br.com.zg.acelera.dao.ConexaoFactory
import br.com.zg.acelera.interfaces.IConexao

class ConexaoFactorySpec extends Specification {

    def "deve retornar a instancia de conexao do Postgres quando solicitado 'POSTGRES'"() {
        when: "solicitamos o banco POSTGRES"
        IConexao conexao = ConexaoFactory.getBancoDeDados("POSTGRES")

        then: "a conexao retornada nao deve ser nula"
        conexao != null
    }

    def "deve garantir que a fabrica retorna a mesma instancia (Singleton) ignorando maiusculas/minusculas"() {
        when: "solicitamos a conexao duas vezes, mudando a caixa da palavra"
        IConexao instancia1 = ConexaoFactory.getBancoDeDados("POSTGRES")
        IConexao instancia2 = ConexaoFactory.getBancoDeDados("postgres")

        then: "ambas devem apontar para a mesma referencia de memoria"
        instancia1.is(instancia2)
    }

    def "deve lancar IllegalArgumentException quando o banco de dados nao for suportado"() {
        when: "solicitamos um banco nao implementado, como MYSQL"
        ConexaoFactory.getBancoDeDados("MYSQL")

        then: "uma excecao com a mensagem correta deve ser lancada"
        def exception = thrown(IllegalArgumentException)
        exception.message == "Banco de dados não suportado: MYSQL"
    }
}