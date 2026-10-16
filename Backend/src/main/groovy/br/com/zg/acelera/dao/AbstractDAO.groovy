package br.com.zg.acelera.dao

import br.com.zg.acelera.interfaces.IConexao
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

abstract class AbstractDAO<T> {

    protected final IConexao conexaoFactory

    AbstractDAO(IConexao conexaoFactory) {
        this.conexaoFactory = conexaoFactory
    }

    protected void executarUpdate(String sql, List parametros) {
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
            throw new RuntimeException("Falha ao executar query", e)
        } finally {
            if (stmt != null) stmt.close()
        }
    }

    protected List<T> executarQuery(String sql, List parametros) {
        List<T> resultados = []
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
                resultados.add(mapearRegistro(rs))
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro na Busca", e)
        } finally {
            if (rs != null) rs.close()
            if (stmt != null) stmt.close()
        }
        return resultados
    }

    protected abstract T mapearRegistro(ResultSet rs)
}