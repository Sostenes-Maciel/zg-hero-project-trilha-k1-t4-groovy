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
            e.printStackTrace()
            throw new RuntimeException("Falha ao executar query", e)
        } finally {
            fecharRecursos(null, stmt, conexao)
        }
    }

    protected Integer executarInsertComRetorno(String sql, List parametros) {
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
            if (rs.next()) {
                return rs.getInt(1)
            }
            return null
        } catch (Exception e) {
            throw new RuntimeException("Falha ao executar insert com retorno", e)
        } finally {
            fecharRecursos(rs, stmt, conexao)
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
            e.printStackTrace()
            throw new RuntimeException("Erro na Busca", e)
        } finally {
            fecharRecursos(rs, stmt, conexao)
        }
        return resultados
    }
    protected boolean executarQueryBooleana(String sql, List parametros) {
        Connection conexao = null
        PreparedStatement stmt = null
        ResultSet rs = null
        try {
            conexao = conexaoFactory.conectar()
            stmt = conexao.prepareStatement(sql)
            parametros.eachWithIndex { param, i -> stmt.setObject(i + 1, param) }
            rs = stmt.executeQuery()
            return rs.next()
        } catch (Exception e) {
            throw new RuntimeException("Erro ao verificar dados no banco", e)
        } finally {
            fecharRecursos(rs, stmt, conexao)
        }
    }
    private static void fecharRecursos(ResultSet rs, PreparedStatement stmt, Connection conexao) {
        try {
            if (rs != null) rs.close()
        } catch (Exception e) {
            e.printStackTrace()
        }

        try {
            if (stmt != null) stmt.close()
        } catch (Exception e) {
            e.printStackTrace()
        }

        try {
            if (conexao != null) conexao.close()
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    protected abstract T mapearRegistro(ResultSet rs)
}