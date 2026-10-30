package br.com.zg.acelera.dao

import br.com.zg.acelera.interfaces.ICompetenciaDAO
import br.com.zg.acelera.interfaces.IConexao
import br.com.zg.acelera.interfaces.IEmpresaDAO
import br.com.zg.acelera.model.Empresa

import java.sql.ResultSet

class EmpresaDAO extends AbstractDAO<Empresa> implements IEmpresaDAO {

    private final ICompetenciaDAO competenciaDAO

    EmpresaDAO(IConexao conexaoFactory, ICompetenciaDAO competenciaDAO) {
        super(conexaoFactory)
        this.competenciaDAO = competenciaDAO
    }

    @Override
    protected Empresa mapearRegistro(ResultSet rs) {
        return new Empresa(
                cnpj: rs.getString("cnpj"),
                nome: rs.getString("nome"),
                email: rs.getString("email"),
                pais: rs.getString("pais"),
                estado: rs.getString("estado"),
                cep: rs.getString("cep"),
                descricao: rs.getString("descricao"),
                competencias: []
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
        List<Empresa> empresas = executarQuery(sql, [])

        empresas.each { e ->
            e.competencias = competenciaDAO.listarDaEmpresa(e.cnpj)
        }

        return empresas
    }

    @Override
    Empresa buscarPorId(String cnpj) {
        String sql = "SELECT cnpj, nome, email, pais, estado, cep, descricao FROM empresa WHERE cnpj = ?"
        List<Empresa> resultados = executarQuery(sql, [cnpj])

        if (resultados.isEmpty()) {
            return null
        }

        Empresa empresa = resultados[0]
        empresa.competencias = competenciaDAO.listarDaEmpresa(empresa.cnpj)

        return empresa
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

    @Override
    Empresa validarLogin(String email, String cnpj) {
        String sql = "SELECT cnpj, nome, email, pais, estado, cep, descricao FROM empresa WHERE email = ? AND cnpj = ?"

        List<Empresa> resultados = executarQuery(sql, [email, cnpj])

        if (resultados.isEmpty()) {
            return null
        }

        Empresa empresa = resultados[0]

        empresa.competencias = competenciaDAO.listarDaEmpresa(empresa.cnpj)

        return empresa
    }
}