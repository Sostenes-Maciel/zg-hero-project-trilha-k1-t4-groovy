package br.com.zg.acelera.dao

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class MatchDAO {

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
            return rs.next()
        } catch (Exception e) {
            throw new RuntimeException("Erro ao verificar dados no banco: ${e.message}", e)
        } finally {
            if (rs != null) rs.close()
            if (stmt != null) stmt.close()
            if (conexao != null) conexao.close()
        }
    }

    private List<Map<String, Object>> executarQueryMatches(String sql, List parametros) {
        List<Map<String, Object>> resultados = []
        Connection conexao = null
        PreparedStatement stmt = null
        ResultSet rs = null
        try {
            conexao = ConexaoBD.conectar()
            stmt = conexao.prepareStatement(sql)
            parametros.eachWithIndex { param, i -> stmt.setObject(i + 1, param) }
            rs = stmt.executeQuery()
            while (rs.next()) {
                resultados.add(mapearMatch(rs))
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar matches: ${e.message}", e)
        } finally {
            if (rs != null) rs.close()
            if (stmt != null) stmt.close()
            if (conexao != null) conexao.close()
        }
        return resultados
    }

    private Map<String, Object> mapearMatch(ResultSet rs) {
        return [
                idMatch      : rs.getInt("id_match"),
                nomeCandidato: rs.getString("nome_candidato"),
                nomeEmpresa  : rs.getString("nome_empresa"),
                tituloVaga   : rs.getString("titulo_vaga")
        ]
    }

    boolean existeMatch(String cpfCandidato, String cnpjEmpresa, Integer idVaga) {
        String sql = """
            SELECT 1 FROM match_vaga
            WHERE cpf_candidato = ? AND cnpj_empresa = ? AND id_vaga = ?
        """
        return executarQueryBooleana(sql, [cpfCandidato, cnpjEmpresa, idVaga])
    }

    void registrarMatch(String cpfCandidato, String cnpjEmpresa, Integer idVaga) {
        if (existeMatch(cpfCandidato, cnpjEmpresa, idVaga)) {
            println "Esse match já está registrado."
            return
        }

        String sql = "INSERT INTO match_vaga (cpf_candidato, cnpj_empresa, id_vaga) VALUES (?, ?, ?)"
        executarUpdate(sql, [cpfCandidato, cnpjEmpresa, idVaga])
        println "Match registrado com sucesso."
    }

    List<Map<String, Object>> listarMatches() {
        String sql = """
            SELECT m.id_match, c.nome AS nome_candidato, e.nome AS nome_empresa, v.titulo AS titulo_vaga
            FROM match_vaga m
            JOIN candidato c ON c.cpf = m.cpf_candidato
            JOIN empresa e ON e.cnpj = m.cnpj_empresa
            JOIN vaga v ON v.id_vaga = m.id_vaga
            ORDER BY m.id_match
        """
        return executarQueryMatches(sql, [])
    }

    List<Map<String, Object>> listarMatchesDoCandidato(String cpfCandidato) {
        String sql = """
            SELECT m.id_match, c.nome AS nome_candidato, e.nome AS nome_empresa, v.titulo AS titulo_vaga
            FROM match_vaga m
            JOIN candidato c ON c.cpf = m.cpf_candidato
            JOIN empresa e ON e.cnpj = m.cnpj_empresa
            JOIN vaga v ON v.id_vaga = m.id_vaga
            WHERE m.cpf_candidato = ?
            ORDER BY m.id_match
        """
        return executarQueryMatches(sql, [cpfCandidato])
    }

    List<Map<String, Object>> listarMatchesDaEmpresa(String cnpjEmpresa) {
        String sql = """
            SELECT m.id_match, c.nome AS nome_candidato, e.nome AS nome_empresa, v.titulo AS titulo_vaga
            FROM match_vaga m
            JOIN candidato c ON c.cpf = m.cpf_candidato
            JOIN empresa e ON e.cnpj = m.cnpj_empresa
            JOIN vaga v ON v.id_vaga = m.id_vaga
            WHERE m.cnpj_empresa = ?
            ORDER BY m.id_match
        """
        return executarQueryMatches(sql, [cnpjEmpresa])
    }
}