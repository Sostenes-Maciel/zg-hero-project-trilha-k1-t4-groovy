package br.com.zg.acelera.dao

import br.com.zg.acelera.interfaces.ICandidatoDAO
import br.com.zg.acelera.interfaces.IConexaoFactory
import br.com.zg.acelera.model.Candidato
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class CandidatoDAO implements ICandidatoDAO {

    private final IConexaoFactory conexaoFactory

    CandidatoDAO(IConexaoFactory conexaoFactory) {
        this.conexaoFactory = conexaoFactory
    }

    private void executarUpdate(String sql, List parametros) {
        Connection conexao = null
        PreparedStatement stmt = null
        try {
            conexao = conexaoFactory.conectar()
            stmt = conexao.prepareStatement(sql)

            parametros.eachWithIndex { param, i ->
                stmt.setObject(i + 1, param)
            }

            stmt.executeUpdate()
        } catch (Exception e) {
            println "Erro no Banco de Dados: ${e.message}"
            throw new RuntimeException("Falha ao executar query", e)
        } finally {
            if (stmt != null) stmt.close()
            if (conexao != null) conexao.close()
        }
    }

    private List<Candidato> executarQuery(String sql, List parametros) {
        List<Candidato> resultados = []
        Connection conexao = null
        PreparedStatement stmt = null
        ResultSet rs = null

        try {
            conexao = conexaoFactory.conectar()
            stmt = conexao.prepareStatement(sql)

            parametros.eachWithIndex { param, i ->
                stmt.setObject(i + 1, param)
            }

            rs = stmt.executeQuery()
            while (rs.next()) {
                resultados.add(mapearCandidato(rs))
            }
        } catch (Exception e) {
            println "Erro na Busca: ${e.message}"
        } finally {
            if (rs != null) rs.close()
            if (stmt != null) stmt.close()
            if (conexao != null) conexao.close()
        }
        return resultados
    }

    private static Candidato mapearCandidato(ResultSet rs) {
        return new Candidato(
                cpf: rs.getString("cpf"),
                nome: rs.getString("nome"),
                email: rs.getString("email"),
                pais: rs.getString("pais"),
                estado: rs.getString("estado"),
                cep: rs.getString("cep"),
                descricao: rs.getString("descricao"),
                idade: rs.getInt("idade")
        )
    }

    @Override
    void cadastrar(Candidato candidato) {
        String sql = """
            INSERT INTO candidato (cpf, nome, email, pais, estado, cep, descricao, idade)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """
        executarUpdate(sql, [
                candidato.cpf, candidato.nome, candidato.email, candidato.pais,
                candidato.estado, candidato.cep, candidato.descricao, candidato.idade
        ])
    }

    @Override
    List<Candidato> listarTodos() {
        String sql = "SELECT cpf, nome, email, pais, estado, cep, descricao, idade FROM candidato ORDER BY nome"
        return executarQuery(sql, [])
    }

    @Override
    Candidato buscarPorId(String cpf) {
        String sql = "SELECT cpf, nome, email, pais, estado, cep, descricao, idade FROM candidato WHERE cpf = ?"
        List<Candidato> resultados = executarQuery(sql, [cpf])
        return resultados.isEmpty() ? null : resultados[0]
    }

    @Override
    void atualizar(Candidato candidato) {
        String sql = """
            UPDATE candidato
            SET nome = ?, email = ?, pais = ?, estado = ?, cep = ?, descricao = ?, idade = ?
            WHERE cpf = ?
        """
        executarUpdate(sql, [
                candidato.nome, candidato.email, candidato.pais, candidato.estado,
                candidato.cep, candidato.descricao, candidato.idade, candidato.cpf
        ])
    }

    @Override
    void excluir(String cpf) {
        String sql = "DELETE FROM candidato WHERE cpf = ?"
        executarUpdate(sql, [cpf])
    }
}