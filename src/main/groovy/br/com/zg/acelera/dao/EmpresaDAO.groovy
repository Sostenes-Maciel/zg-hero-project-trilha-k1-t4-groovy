package br.com.zg.acelera.dao

import br.com.zg.acelera.model.Empresa

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class EmpresaDAO {

    void cadastrar(Empresa empresa) {
        String sql = """
            INSERT INTO empresa (
                cnpj,
                nome,
                email,
                pais,
                estado,
                cep,
                descricao
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, empresa.cnpj)
            stmt.setString(2, empresa.nome)
            stmt.setString(3, empresa.email)
            stmt.setString(4, empresa.pais)
            stmt.setString(5, empresa.estado)
            stmt.setString(6, empresa.cep)
            stmt.setString(7, empresa.descricao)

            stmt.executeUpdate()
        }
    }

    List<Empresa> listarTodos() {
        String sql = """
            SELECT
                cnpj,
                nome,
                email,
                pais,
                estado,
                cep,
                descricao
            FROM empresa
            ORDER BY nome
        """

        List<Empresa> empresas = []

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                empresas.add(mapearEmpresa(rs))
            }
        }

        return empresas
    }

    Empresa buscarPorCnpj(String cnpj) {
        String sql = """
            SELECT
                cnpj,
                nome,
                email,
                pais,
                estado,
                cep,
                descricao
            FROM empresa
            WHERE cnpj = ?
        """

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, cnpj)

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearEmpresa(rs)
                }
            }
        }

        return null
    }

    void atualizar(Empresa empresa) {
        String sql = """
            UPDATE empresa
            SET nome = ?,
                email = ?,
                pais = ?,
                estado = ?,
                cep = ?,
                descricao = ?
            WHERE cnpj = ?
        """

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, empresa.nome)
            stmt.setString(2, empresa.email)
            stmt.setString(3, empresa.pais)
            stmt.setString(4, empresa.estado)
            stmt.setString(5, empresa.cep)
            stmt.setString(6, empresa.descricao)
            stmt.setString(7, empresa.cnpj)

            stmt.executeUpdate()
        }
    }

    void excluir(String cnpj) {
        String sql = """
            DELETE FROM empresa
            WHERE cnpj = ?
        """

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, cnpj)
            stmt.executeUpdate()
        }
    }

    private static Empresa mapearEmpresa(ResultSet rs) {
        new Empresa(
                cnpj: rs.getString("cnpj"),
                nome: rs.getString("nome"),
                email: rs.getString("email"),
                pais: rs.getString("pais"),
                estado: rs.getString("estado"),
                cep: rs.getString("cep"),
                descricao: rs.getString("descricao")
        )
    }
}