package br.com.zg.acelera.dao

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class CurtidaDAO {

    private void executarUpdate(String sql, List parametros) {
        Connection conexao = null
        PreparedStatement stmt = null
        try {
            conexao = ConexaoBD.conectar()
            stmt = conexao.prepareStatement(sql)
            parametros.eachWithIndex { param, i -> stmt.setObject(i + 1, param) }
            stmt.executeUpdate()
        } catch (Exception e) {
            throw new RuntimeException("Erro ao executar atualização no banco: ${e.message}", e)
        } finally {
            if (stmt != null) stmt.close()
            if (conexao != null) conexao.close()
        }
    }

    private boolean executarQueryBooleana(String sql, List parametros) {
        Connection conexao = null
        PreparedStatement stmt = null
        ResultSet rs = null
        try {
            conexao = ConexaoBD.conectar()
            stmt = conexao.prepareStatement(sql)
            parametros.eachWithIndex { param, i -> stmt.setObject(i + 1, param) }
            rs = stmt.executeQuery()
            return rs.next() // Retorna true se encontrou algum registro
        } catch (Exception e) {
            throw new RuntimeException("Erro ao verificar dados no banco: ${e.message}", e)
        } finally {
            if (rs != null) rs.close()
            if (stmt != null) stmt.close()
            if (conexao != null) conexao.close()
        }
    }

    void registrarCurtidaCandidato(String cpfCandidato, Integer idVaga) {
        String sqlVerificar = "SELECT 1 FROM curtida WHERE cpf_candidato = ? AND id_vaga = ? AND cnpj_empresa IS NULL"

        if (executarQueryBooleana(sqlVerificar, [cpfCandidato, idVaga])) {
            println "O candidato já curtiu essa vaga."
            return
        }

        String sqlInserir = "INSERT INTO curtida (cpf_candidato, id_vaga) VALUES (?, ?)"
        executarUpdate(sqlInserir, [cpfCandidato, idVaga])
        println "Curtida do candidato registrada com sucesso."
    }

    void registrarCurtidaEmpresa(String cpfCandidato, String cnpjEmpresa) {
        String sqlVerificar = "SELECT 1 FROM curtida WHERE cpf_candidato = ? AND cnpj_empresa = ? AND id_vaga IS NULL"

        if (executarQueryBooleana(sqlVerificar, [cpfCandidato, cnpjEmpresa])) {
            println "A empresa já curtiu esse candidato."
            return
        }

        String sqlInserir = "INSERT INTO curtida (cpf_candidato, cnpj_empresa) VALUES (?, ?)"
        executarUpdate(sqlInserir, [cpfCandidato, cnpjEmpresa])
        println "Curtida da empresa registrada com sucesso."
    }

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
        return executarQueryBooleana(sql, [cpfCandidato, idVaga, cpfCandidato, cnpjEmpresa])
    }
}