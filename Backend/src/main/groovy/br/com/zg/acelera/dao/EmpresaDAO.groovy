package br.com.zg.acelera.dao

import br.com.zg.acelera.model.Empresa

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class EmpresaDAO implements IEmpresaDAO {

    private final IConexaoFactory conexaoFactory

    EmpresaDAO(IConexaoFactory conexaoFactory) {
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
            println "Erro no Banco de Dados: ${e.message}"
            throw new RuntimeException("Falha ao executar query", e)
        } finally {
            if (stmt != null) stmt.close()
            if (conexao != null) conexao.close()
        }
    }

    private List<Empresa> executarQuery(String sql, List parametros) {
        List<Empresa> resultados = []
        Connection conexao = null
        PreparedStatement stmt = null
        ResultSet rs = null
        try {
            conexao = conexaoFactory.conectar()
            stmt = conexao.prepareStatement(sql)
            parametros.eachWithIndex { param, i -> stmt.setObject(i + 1, param) }
            rs = stmt.executeQuery()
            while (rs.next()) {
                resultados.add(mapearEmpresa(rs))
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

    private static Empresa mapearEmpresa(ResultSet rs) {
        return new Empresa(
                cnpj: rs.getString("cnpj"),
                nome: rs.getString("nome"),
                email: rs.getString("email"),
                pais: rs.getString("pais"),
                estado: rs.getString("estado"),
                cep: rs.getString("cep"),
                descricao: rs.getString("descricao")
        )
    }

    @Override
    void cadastrar(Empresa empresa) {
        String sql = """
            INSERT INTO empresa (cnpj, nome, email, pais, estado, cep, descricao)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """
        executarUpdate(sql, [
                empresa.cnpj, empresa.nome, empresa.email, empresa.pais,
                empresa.estado, empresa.cep, empresa.descricao
        ])
    }

    @Override
    List<Empresa> listarTodos() {
        String sql = "SELECT cnpj, nome, email, pais, estado, cep, descricao FROM empresa ORDER BY nome"
        return executarQuery(sql, [])
    }

    @Override
    Empresa buscarPorId(String cnpj) {
        String sql = "SELECT cnpj, nome, email, pais, estado, cep, descricao FROM empresa WHERE cnpj = ?"
        List<Empresa> resultados = executarQuery(sql, [cnpj])
        return resultados.isEmpty() ? null : resultados[0]
    }

    @Override
    void atualizar(Empresa empresa) {
        String sql = """
            UPDATE empresa
            SET nome = ?, email = ?, pais = ?, estado = ?, cep = ?, descricao = ?
            WHERE cnpj = ?
        """
        executarUpdate(sql, [
                empresa.nome, empresa.email, empresa.pais, empresa.estado,
                empresa.cep, empresa.descricao, empresa.cnpj
        ])
    }

    @Override
    void excluir(String cnpj) {
        String sql = "DELETE FROM empresa WHERE cnpj = ?"
        executarUpdate(sql, [cnpj])
    }
}