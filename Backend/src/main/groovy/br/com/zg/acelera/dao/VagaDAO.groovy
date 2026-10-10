package br.com.zg.acelera.dao

import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class VagaDAO implements IVagaDAO {

    private final IConexaoFactory conexaoFactory

    VagaDAO(IConexaoFactory conexaoFactory) {
        this.conexaoFactory = conexaoFactory
    }

    private void executarUpdate(String sql, List parametros) {
        Connection conexao = null
        PreparedStatement stmt = null
        try {
            conexao = conexaoFactory.conectar()
            stmt = conexao.prepareStatement(sql)
            parametros.eachWithIndex { param, i -> stmt.setObject(i + 1, param) }
            stmt.executeUpdate()
        } catch (Exception e) {
            throw new RuntimeException("Erro no Banco de Dados: ${e.message}", e)
        } finally {
            if (stmt != null) stmt.close()
            if (conexao != null) conexao.close()
        }
    }

    private Integer executarInsertComRetorno(String sql, List parametros) {
        Connection conexao = null
        PreparedStatement stmt = null
        ResultSet rs = null
        try {
            conexao = conexaoFactory.conectar()
            stmt = conexao.prepareStatement(sql)
            parametros.eachWithIndex { param, i -> stmt.setObject(i + 1, param) }

            rs = stmt.executeQuery()
            if (rs.next()) {
                return rs.getInt("id_vaga")
            }
            return null
        } catch (Exception e) {
            throw new RuntimeException("Erro ao cadastrar Vaga: ${e.message}", e)
        } finally {
            if (rs != null) rs.close()
            if (stmt != null) stmt.close()
            if (conexao != null) conexao.close()
        }
    }

    private List<Vaga> executarQuery(String sql, List parametros) {
        List<Vaga> resultados = []
        Connection conexao = null
        PreparedStatement stmt = null
        ResultSet rs = null
        try {
            conexao = conexaoFactory.conectar()
            stmt = conexao.prepareStatement(sql)
            parametros.eachWithIndex { param, i -> stmt.setObject(i + 1, param) }
            rs = stmt.executeQuery()
            while (rs.next()) {
                resultados.add(mapearVaga(rs))
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

    private static Vaga mapearVaga(ResultSet rs) {
        Empresa empresa = new Empresa(
                cnpj: rs.getString("cnpj"),
                nome: rs.getString("nome")
        )
        return new Vaga(
                id: rs.getInt("id_vaga"),
                titulo: rs.getString("titulo"),
                pais: rs.getString("pais"),
                estado: rs.getString("estado"),
                descricao: rs.getString("descricao"),
                empresa: empresa
        )
    }

    @Override
    void cadastrar(Vaga vaga) {
        String sql = """
            INSERT INTO vaga (titulo, pais, estado, descricao, cnpj_empresa)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id_vaga
        """
        Integer idGerado = executarInsertComRetorno(sql, [
                vaga.titulo, vaga.pais, vaga.estado, vaga.descricao, vaga.empresa.cnpj
        ])

        if (idGerado != null) {
            vaga.id = idGerado
        }
    }

    @Override
    List<Vaga> listarTodos() {
        String sql = """
            SELECT v.id_vaga, v.titulo, v.pais, v.estado, v.descricao, e.cnpj, e.nome
            FROM vaga v
            INNER JOIN empresa e ON v.cnpj_empresa = e.cnpj
            ORDER BY v.id_vaga
        """
        return executarQuery(sql, [])
    }

    @Override
    Vaga buscarPorId(Integer id) {
        String sql = """
            SELECT v.id_vaga, v.titulo, v.pais, v.estado, v.descricao, e.cnpj, e.nome
            FROM vaga v
            INNER JOIN empresa e ON v.cnpj_empresa = e.cnpj
            WHERE v.id_vaga = ?
        """
        List<Vaga> resultados = executarQuery(sql, [id])
        return resultados.isEmpty() ? null : resultados[0]
    }

    @Override
    List<Vaga> listarPorEmpresa(String cnpj) {
        String sql = """
            SELECT v.id_vaga, v.titulo, v.pais, v.estado, v.descricao, e.cnpj, e.nome
            FROM vaga v
            INNER JOIN empresa e ON v.cnpj_empresa = e.cnpj
            WHERE v.cnpj_empresa = ?
            ORDER BY v.id_vaga
        """
        return executarQuery(sql, [cnpj])
    }

    @Override
    void atualizar(Vaga vaga) {
        String sql = """
            UPDATE vaga
            SET titulo = ?, pais = ?, estado = ?, descricao = ?, cnpj_empresa = ?
            WHERE id_vaga = ?
        """
        executarUpdate(sql, [
                vaga.titulo, vaga.pais, vaga.estado, vaga.descricao, vaga.empresa.cnpj, vaga.id
        ])
    }

    @Override
    void excluir(Integer id) {
        String sql = "DELETE FROM vaga WHERE id_vaga = ?"
        executarUpdate(sql, [id])
    }
}