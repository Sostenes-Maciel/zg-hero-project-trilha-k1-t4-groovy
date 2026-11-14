package br.com.zg.aceleraspec.dao

import br.com.zg.acelera.dao.ConexaoFactory
import br.com.zg.acelera.dao.RecrutadorDAO
import br.com.zg.acelera.interfaces.IConexao
import br.com.zg.acelera.model.Recrutador
import spock.lang.Specification

import java.sql.Connection
import java.sql.PreparedStatement
import java.time.LocalDate

class RecrutadorDAOSpec extends Specification {

    private static final String EMPRESA_ID = "98.765.432/0001-11"
    private RecrutadorDAO recrutadorDAO

    def setup() {
        IConexao conexao = ConexaoFactory.getBancoDeDados("POSTGRES")
        recrutadorDAO = new RecrutadorDAO(conexao)
    }

    def "deve salvar e buscar recrutador sem retornar senha hash"() {
        given:
        String email = "recrutador.dao.${System.nanoTime()}@teste.com"
        String cpf = "${System.nanoTime()}".takeRight(11)
        Recrutador recrutador = new Recrutador(
                empresaId: EMPRESA_ID,
                nome: "Recrutador DAO",
                areaAtuacao: "TI",
                nivelPermissao: "Limitado",
                descricao: "Teste de persistência",
                email: email,
                cpf: cpf,
                dataNascimento: LocalDate.of(1990, 1, 1),
                senhaHash: "hash-nao-retornado"
        )

        when:
        recrutadorDAO.salvar(recrutador)
        Recrutador porEmail = recrutadorDAO.buscarPorEmail(email)
        Recrutador porCpf = recrutadorDAO.buscarPorCpf(cpf)
        List<Recrutador> porEmpresa = recrutadorDAO.buscarPorEmpresa(EMPRESA_ID)

        then:
        recrutador.id != null
        porEmail.id == recrutador.id
        porEmail.email == email
        porEmail.senhaHash == null
        porCpf.id == recrutador.id
        porEmpresa*.id.contains(recrutador.id)

        cleanup:
        removerPorEmail(email)
    }

    def "deve inativar recrutador sem apagar o registro"() {
        given:
        String email = "recrutador.inativo.${System.nanoTime()}@teste.com"
        String cpf = "${System.nanoTime()}".takeRight(11)
        Recrutador recrutador = new Recrutador(
                empresaId: EMPRESA_ID,
                nome: "Recrutador Inativo",
                areaAtuacao: "RH",
                nivelPermissao: "Total",
                email: email,
                cpf: cpf,
                dataNascimento: LocalDate.of(1988, 2, 2),
                senhaHash: "hash"
        )
        recrutadorDAO.salvar(recrutador)

        when:
        boolean inativado = recrutadorDAO.inativar(recrutador.id)

        then:
        inativado
        !recrutadorDAO.buscarPorId(recrutador.id).ativo

        cleanup:
        removerPorEmail(email)
    }

    private static void removerPorEmail(String email) {
        IConexao conexaoFactory = ConexaoFactory.getBancoDeDados("POSTGRES")
        Connection conexao = null
        PreparedStatement stmt = null
        try {
            conexao = conexaoFactory.conectar()
            stmt = conexao.prepareStatement("DELETE FROM recrutador WHERE email = ?")
            stmt.setString(1, email)
            stmt.executeUpdate()
        } finally {
            stmt?.close()
            conexao?.close()
        }
    }
}
