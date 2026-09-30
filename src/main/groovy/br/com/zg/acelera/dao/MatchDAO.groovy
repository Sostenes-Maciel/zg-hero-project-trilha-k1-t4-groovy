package br.com.zg.acelera.dao

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class MatchDAO {

    boolean existeMatch(String cpfCandidato, String cnpjEmpresa, Integer idVaga) {

        String sql = """
            SELECT 1
            FROM match
            WHERE cpf_candidato = ?
              AND cnpj_empresa = ?
              AND id_vaga = ?
        """

        try (
                Connection conexao = ConexaoBD.conectar();
                PreparedStatement comando = conexao.prepareStatement(sql)
        ) {
            comando.setString(1, cpfCandidato)
            comando.setString(2, cnpjEmpresa)
            comando.setInt(3, idVaga)

            ResultSet resultado = comando.executeQuery()

            return resultado.next()

        } catch (SQLException e) {
            println "Erro ao verificar match: ${e.message}"
            throw e
        }
    }

    void registrarMatch(String cpfCandidato, String cnpjEmpresa, Integer idVaga) {

        if (existeMatch(cpfCandidato, cnpjEmpresa, idVaga)) {
            println "Esse match já está registrado."
            return
        }

        String sql = """
            INSERT INTO match (cpf_candidato, cnpj_empresa, id_vaga)
            VALUES (?, ?, ?)
        """

        try (
                Connection conexao = ConexaoBD.conectar();
                PreparedStatement comando = conexao.prepareStatement(sql)
        ) {
            comando.setString(1, cpfCandidato)
            comando.setString(2, cnpjEmpresa)
            comando.setInt(3, idVaga)

            comando.executeUpdate()

            println "Match registrado com sucesso."

        } catch (SQLException e) {
            println "Erro ao registrar match: ${e.message}"
            throw e
        }
    }
}