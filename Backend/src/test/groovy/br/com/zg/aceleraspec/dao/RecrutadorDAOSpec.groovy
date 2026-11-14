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
        porEmpresa.find { it.id == recrutador.id }.senhaHash == null

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

    def "deve atualizar dados permitidos e continuar sem mapear a senha"() {
        given:
        String email = "recrutador.update.${System.nanoTime()}@teste.com"
        String cpf = "${System.nanoTime()}".takeRight(11)
        Recrutador recrutador = new Recrutador(
                empresaId: EMPRESA_ID, nome: "Antes", areaAtuacao: "TI",
                nivelPermissao: "Limitado", descricao: "Antes", email: email,
                cpf: cpf, dataNascimento: LocalDate.of(1990, 1, 1),
                senhaHash: "hash-original"
        )
        recrutadorDAO.salvar(recrutador)
        recrutador.nome = "Depois"
        recrutador.areaAtuacao = "RH"
        recrutador.nivelPermissao = "Total"
        recrutador.descricao = "Atualizado"

        when:
        recrutadorDAO.atualizar(recrutador)
        Recrutador atualizado = recrutadorDAO.buscarPorId(recrutador.id)

        then:
        atualizado.nome == "Depois"
        atualizado.areaAtuacao == "RH"
        atualizado.nivelPermissao == "Total"
        atualizado.descricao == "Atualizado"
        atualizado.senhaHash == null

        cleanup:
        removerPorEmail(email)
    }

    def "deve rejeitar e-mail duplicado pela constraint real do banco"() {
        given:
        String email = "recrutador.dup.email.${System.nanoTime()}@teste.com"
        String cpf1 = "${System.nanoTime()}".takeRight(11)
        String cpf2 = "${(System.nanoTime() + 1)}".takeRight(11)
        Recrutador primeiro = novo(email, cpf1)
        recrutadorDAO.salvar(primeiro)
        Recrutador duplicado = novo(email, cpf2)

        when:
        recrutadorDAO.salvar(duplicado)

        then:
        RuntimeException erro = thrown()
        erro.cause != null
        erro.cause.getSQLState() == "23505"

        cleanup:
        removerPorEmail(email)
    }

    def "deve rejeitar CPF duplicado pela constraint real do banco"() {
        given:
        String email1 = "recrutador.dup.cpf.1.${System.nanoTime()}@teste.com"
        String email2 = "recrutador.dup.cpf.2.${System.nanoTime()}@teste.com"
        String cpf = "${System.nanoTime()}".takeRight(11)
        recrutadorDAO.salvar(novo(email1, cpf))
        Recrutador duplicado = novo(email2, cpf)

        when:
        recrutadorDAO.salvar(duplicado)

        then:
        RuntimeException erro = thrown()
        erro.cause != null
        erro.cause.getSQLState() == "23505"

        cleanup:
        removerPorEmail(email1)
        removerPorEmail(email2)
    }

    private Recrutador novo(String email, String cpf) {
        new Recrutador(
                empresaId: EMPRESA_ID, nome: "Teste DAO", areaAtuacao: "TI",
                nivelPermissao: "Limitado", descricao: "Persistência",
                email: email, cpf: cpf, dataNascimento: LocalDate.of(1990, 1, 1),
                senhaHash: "hash"
        )
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
