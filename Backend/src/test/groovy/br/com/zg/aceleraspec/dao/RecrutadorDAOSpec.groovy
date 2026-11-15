package br.com.zg.aceleraspec.dao

import br.com.zg.acelera.dao.RecrutadorDAO
import br.com.zg.acelera.interfaces.IConexao
import br.com.zg.acelera.model.Recrutador
import spock.lang.Specification

import java.sql.Connection
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.LocalDate

class RecrutadorDAOSpec extends Specification {

    IConexao conexaoMock
    RecrutadorDAO dao

    def setup() {
        conexaoMock = Mock(IConexao)
        // Usamos Spy para conseguir "interceptar" as chamadas ao AbstractDAO (executarQuery, executarUpdate, etc)
        dao = Spy(new RecrutadorDAO(conexaoMock))
    }

    def "deve mapear registro do ResultSet para objeto Recrutador"() {
        given: "Um ResultSet com dados simulados do banco"
        ResultSet rsMock = Mock(ResultSet)
        rsMock.getInt("id_recrutador") >> 1
        rsMock.getString("empresa_id") >> "12345678901234"
        rsMock.getString("nome") >> "Sóstenes"
        rsMock.getString("area_atuacao") >> "TI"
        rsMock.getString("nivel_permissao") >> "Total"
        rsMock.getString("descricao") >> "Tech Recruiter"
        rsMock.getString("email") >> "sostenes@email.com"
        rsMock.getString("cpf") >> "12345678900"
        rsMock.getDate("data_nascimento") >> Date.valueOf(LocalDate.of(1995, 5, 10))
        rsMock.getBoolean("ativo") >> true

        when: "O método mapearRegistro é chamado"
        Recrutador recrutador = dao.mapearRegistro(rsMock)

        then: "Os dados devem ser mapeados corretamente para a model"
        recrutador.id == 1
        recrutador.empresaId == "12345678901234"
        recrutador.nome == "Sóstenes"
        recrutador.nivelPermissao == "Total"
        recrutador.dataNascimento == LocalDate.of(1995, 5, 10)
        recrutador.ativo == true
    }

    def "deve salvar recrutador e setar o id retornado"() {
        given: "Um novo recrutador"
        Recrutador recrutador = new Recrutador(nome: "Maria", email: "maria@teste.com", senhaHash: "hash-secreto")

        and: "Interceptamos o insert do AbstractDAO para retornar o ID 10"
        dao.executarInsertComRetorno(_ as String, _ as List) >> 10

        when:
        dao.salvar(recrutador)

        then:
        recrutador.id == 10
    }

    def "deve buscar recrutador por ID, Email ou CPF"() {
        given:
        Recrutador esperado = new Recrutador(id: 1, nome: "Ana")

        and: "Simulamos o AbstractDAO retornando nossa Ana quando buscar"
        dao.executarQuery(_ as String, _) >> [esperado]

        expect: "Garantimos que as 3 consultas retornem a mesma Ana"
        dao.buscarPorId(1) == esperado
        dao.buscarPorEmail("ana@teste.com") == esperado
        dao.buscarPorCpf("11122233344") == esperado
    }

    def "deve retornar nulo ao buscar recrutador que nao existe"() {
        given: "Simulamos o banco vazio"
        dao.executarQuery(_ as String, _) >> []

        expect:
        dao.buscarPorId(99) == null
    }

    def "deve listar recrutadores por empresa"() {
        given:
        Recrutador rec1 = new Recrutador(id: 1, nome: "Rec 1")
        Recrutador rec2 = new Recrutador(id: 2, nome: "Rec 2")
        dao.executarQuery(_ as String, ["12345678901234"]) >> [rec1, rec2]

        when:
        List<Recrutador> lista = dao.buscarPorEmpresa("12345678901234")

        then:
        lista.size() == 2
        lista[0].nome == "Rec 1"
    }

    def "deve inativar recrutador com sucesso"() {
        given: "Simulamos que o update afetou 1 linha"
        dao.executarUpdate(_ as String, [5]) >> 1

        when:
        boolean inativou = dao.inativar(5)

        then:
        inativou == true
    }

    def "deve buscar hash da senha por email utilizando JDBC manual"() {
        given: "Mocks do JDBC (necessário porque o executarValor abre conexão manual)"
        Connection connMock = Mock(Connection)
        PreparedStatement stmtMock = Mock(PreparedStatement)
        ResultSet rsMock = Mock(ResultSet)

        and: "Configuração do fluxo de conexão"
        conexaoMock.conectar() >> connMock
        connMock.prepareStatement(_) >> stmtMock
        stmtMock.executeQuery() >> rsMock

        and: "Simulando o cursor do ResultSet (true na primeira leitura, false para encerrar o loop)"
        rsMock.next() >>> [true, false]
        rsMock.getString(1) >> "meu-hash-criptografado"

        when:
        String hash = dao.buscarHashPorEmail("recrutador@email.com")

        then: "O hash deve ser retornado e as conexões devem ser fechadas com segurança"
        hash == "meu-hash-criptografado"
        1 * stmtMock.setObject(1, "recrutador@email.com")
        1 * rsMock.close()
        1 * stmtMock.close()
        1 * connMock.close()
    }

    def "deve retornar nulo ao buscar hash de um email inexistente"() {
        given: "Mocks do JDBC"
        Connection connMock = Mock(Connection)
        PreparedStatement stmtMock = Mock(PreparedStatement)
        ResultSet rsMock = Mock(ResultSet)

        conexaoMock.conectar() >> connMock
        connMock.prepareStatement(_) >> stmtMock
        stmtMock.executeQuery() >> rsMock

        and: "Simula ResultSet vazio"
        rsMock.next() >> false

        when:
        String hash = dao.buscarHashPorEmail("inexistente@email.com")

        then:
        hash == null
        1 * connMock.close() // Garante que até falhas fecham a conexão
    }
}