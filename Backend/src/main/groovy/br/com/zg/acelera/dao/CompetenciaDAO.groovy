package br.com.zg.acelera.dao

import br.com.zg.acelera.interfaces.ICompetenciaDAO
import br.com.zg.acelera.interfaces.IConexaoFactory

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class CompetenciaDAO implements ICompetenciaDAO {

    private final IConexaoFactory conexaoFactory

    CompetenciaDAO(IConexaoFactory conexaoFactory) {
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
            throw new RuntimeException("Erro no Banco de Dados: ${e.message}", e)
        } finally {
            if (stmt != null) stmt.close()
            if (conexao != null) conexao.close()
        }
    }

    private List<String> executarQuery(String sql, List parametros) {
        List<String> resultados = []
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
                resultados.add(rs.getString("nome"))
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro na Busca: ${e.message}", e)
        } finally {
            if (rs != null) rs.close()
            if (stmt != null) stmt.close()
            if (conexao != null) conexao.close()
        }
        return resultados
    }

    @Override
    void cadastrar(String nome) {
        String sql = "INSERT INTO competencia (nome) VALUES (?)"
        executarUpdate(sql, [nome])
    }

    @Override
    List<String> listarTodos() {
        String sql = "SELECT nome FROM competencia ORDER BY nome"
        return executarQuery(sql, [])
    }

    @Override
    void atualizar(String nomeAtual, String novoNome) {
        String sql = "UPDATE competencia SET nome = ? WHERE nome = ?"
        executarUpdate(sql, [novoNome, nomeAtual])
    }

    @Override
    void excluir(String nome) {
        String sql = "DELETE FROM competencia WHERE nome = ?"
        executarUpdate(sql, [nome])
    }

    @Override
    void garantirCompetencia(String nome) {
        String sql = "INSERT INTO competencia (nome) VALUES (?) ON CONFLICT (nome) DO NOTHING"
        executarUpdate(sql, [nome])
    }

    @Override
    void adicionarAoCandidato(String cpf, String nomeCompetencia) {
        String sql = """
            INSERT INTO candidato_competencia (cpf_candidato, id_competencia)
            SELECT ?, id_competencia FROM competencia WHERE nome = ?
            ON CONFLICT DO NOTHING
        """
        executarUpdate(sql, [cpf, nomeCompetencia])
    }

    @Override
    void removerDoCandidato(String cpf, String nomeCompetencia) {
        String sql = """
            DELETE FROM candidato_competencia
            WHERE cpf_candidato = ? AND id_competencia = (
                SELECT id_competencia FROM competencia WHERE nome = ?
            )
        """
        executarUpdate(sql, [cpf, nomeCompetencia])
    }

    @Override
    List<String> listarDoCandidato(String cpf) {
        String sql = """
            SELECT c.nome FROM competencia c
            INNER JOIN candidato_competencia cc ON cc.id_competencia = c.id_competencia
            WHERE cc.cpf_candidato = ? ORDER BY c.nome
        """
        return executarQuery(sql, [cpf])
    }

    @Override
    void adicionarAoEmpresa(String cnpj, String nomeCompetencia) {
        String sql = """
            INSERT INTO empresa_competencia (cnpj_empresa, id_competencia)
            SELECT ?, id_competencia FROM competencia WHERE nome = ?
            ON CONFLICT DO NOTHING
        """
        executarUpdate(sql, [cnpj, nomeCompetencia])
    }

    @Override
    void removerDaEmpresa(String cnpj, String nomeCompetencia) {
        String sql = """
            DELETE FROM empresa_competencia
            WHERE cnpj_empresa = ? AND id_competencia = (
                SELECT id_competencia FROM competencia WHERE nome = ?
            )
        """
        executarUpdate(sql, [cnpj, nomeCompetencia])
    }

    @Override
    List<String> listarDaEmpresa(String cnpj) {
        String sql = """
            SELECT c.nome FROM competencia c
            INNER JOIN empresa_competencia ec ON ec.id_competencia = c.id_competencia
            WHERE ec.cnpj_empresa = ? ORDER BY c.nome
        """
        return executarQuery(sql, [cnpj])
    }

    @Override
    void adicionarAVaga(Integer idVaga, String nomeCompetencia) {
        String sql = """
            INSERT INTO vaga_competencia (id_vaga, id_competencia)
            SELECT ?, id_competencia FROM competencia WHERE nome = ?
            ON CONFLICT DO NOTHING
        """
        executarUpdate(sql, [idVaga, nomeCompetencia])
    }

    @Override
    void removerDaVaga(Integer idVaga, String nomeCompetencia) {
        String sql = """
            DELETE FROM vaga_competencia
            WHERE id_vaga = ? AND id_competencia = (
                SELECT id_competencia FROM competencia WHERE nome = ?
            )
        """
        executarUpdate(sql, [idVaga, nomeCompetencia])
    }

    @Override
    List<String> listarDaVaga(Integer idVaga) {
        String sql = """
            SELECT c.nome FROM competencia c
            INNER JOIN vaga_competencia vc ON vc.id_competencia = c.id_competencia
            WHERE vc.id_vaga = ? ORDER BY c.nome
        """
        return executarQuery(sql, [idVaga])
    }
}