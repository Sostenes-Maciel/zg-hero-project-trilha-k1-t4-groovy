package br.com.zg.acelera.dao

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class CurtidaDAO {

    /**
     * Registra a curtida de um candidato em uma vaga.
     *
     * A empresa fica nula nesse primeiro momento.
     */
    void registrarCurtidaCandidato(String cpfCandidato, Integer idVaga) {
        String sqlVerificar = """
            SELECT 1
            FROM curtida
            WHERE cpf_candidato = ?
              AND id_vaga = ?
              AND cnpj_empresa IS NULL
        """

        String sqlInserir = """
            INSERT INTO curtida (cpf_candidato, id_vaga)
            VALUES (?, ?)
        """

        try (
                Connection conexao = ConexaoBD.conectar();
                PreparedStatement verificar = conexao.prepareStatement(sqlVerificar)
        ) {
            verificar.setString(1, cpfCandidato)
            verificar.setInt(2, idVaga)

            ResultSet resultado = verificar.executeQuery()

            if (resultado.next()) {
                println "O candidato já curtiu essa vaga."
                return
            }

            try (PreparedStatement inserir = conexao.prepareStatement(sqlInserir)) {
                inserir.setString(1, cpfCandidato)
                inserir.setInt(2, idVaga)
                inserir.executeUpdate()
            }

            println "Curtida do candidato registrada com sucesso."

        } catch (SQLException e) {
            println "Erro ao registrar curtida do candidato: ${e.message}"
            throw e
        }
    }

    /**
     * Registra a curtida de uma empresa em um candidato.
     *
     * A curtida da empresa é associada à mesma vaga
     * que o candidato curtiu.
     */
    void registrarCurtidaEmpresa(String cpfCandidato, String cnpjEmpresa) {

        String sqlVerificar = """
        SELECT 1
        FROM curtida
        WHERE cpf_candidato = ?
          AND cnpj_empresa = ?
          AND id_vaga IS NULL
    """

        String sqlInserir = """
        INSERT INTO curtida (cpf_candidato, cnpj_empresa)
        VALUES (?, ?)
    """

        try (
                Connection conexao = ConexaoBD.conectar();
                PreparedStatement verificar = conexao.prepareStatement(sqlVerificar)
        ) {

            verificar.setString(1, cpfCandidato)
            verificar.setString(2, cnpjEmpresa)

            ResultSet resultado = verificar.executeQuery()

            if (resultado.next()) {
                println "A empresa já curtiu esse candidato."
                return
            }

            try (PreparedStatement inserir = conexao.prepareStatement(sqlInserir)) {
                inserir.setString(1, cpfCandidato)
                inserir.setString(2, cnpjEmpresa)
                inserir.executeUpdate()
            }

            println "Curtida da empresa registrada com sucesso."

        } catch (SQLException e) {
            println "Erro ao registrar curtida da empresa: ${e.message}"
            throw e
        }
    }

    /**
     * Verifica se existe um match entre candidato, empresa e vaga.
     */
    boolean verificarMatch(String cpfCandidato, String cnpjEmpresa, Integer idVaga) {

        String sql = """
        SELECT 1
        FROM curtida c1
        WHERE c1.cpf_candidato = ?
          AND c1.id_vaga = ?
          AND c1.cnpj_empresa IS NULL
          AND EXISTS (
              SELECT 1
              FROM curtida c2
              WHERE c2.cpf_candidato = ?
                AND c2.cnpj_empresa = ?
                AND c2.id_vaga IS NULL
          )
    """

        try (
                Connection conexao = ConexaoBD.conectar();
                PreparedStatement comando = conexao.prepareStatement(sql)
        ) {

            comando.setString(1, cpfCandidato)
            comando.setInt(2, idVaga)
            comando.setString(3, cpfCandidato)
            comando.setString(4, cnpjEmpresa)

            ResultSet resultado = comando.executeQuery()

            return resultado.next()

        } catch (SQLException e) {
            println "Erro ao verificar match: ${e.message}"
            throw e
        }
    }
}