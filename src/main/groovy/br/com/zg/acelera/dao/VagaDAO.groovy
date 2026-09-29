package br.com.zg.acelera.dao

import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class VagaDAO {

    Integer cadastrar(Vaga vaga) {
        String sql = """
        INSERT INTO vaga (
            titulo,
            pais,
            estado,
            descricao,
            cnpj_empresa
        )
        VALUES (?, ?, ?, ?, ?)
        RETURNING id_vaga
    """

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, vaga.titulo)
            stmt.setString(2, vaga.pais)
            stmt.setString(3, vaga.estado)
            stmt.setString(4, vaga.descricao)
            stmt.setString(5, vaga.empresa.cnpj)

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_vaga")
                }
            }
        }

        return null
    }

    List<Vaga> listarTodos() {
        String sql = """
            SELECT
                v.id_vaga,
                v.titulo,
                v.pais,
                v.estado,
                v.descricao,
                e.cnpj,
                e.nome
            FROM vaga v
            INNER JOIN empresa e
                ON v.cnpj_empresa = e.cnpj
            ORDER BY v.id_vaga
        """

        List<Vaga> vagas = []

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                vagas.add(mapearVaga(rs))
            }
        }

        return vagas
    }

    Vaga buscarPorId(Integer id) {
        String sql = """
            SELECT
                v.id_vaga,
                v.titulo,
                v.pais,
                v.estado,
                v.descricao,
                e.cnpj,
                e.nome
            FROM vaga v
            INNER JOIN empresa e
                ON v.cnpj_empresa = e.cnpj
            WHERE v.id_vaga = ?
        """

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id)

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearVaga(rs)
                }
            }
        }

        return null
    }

    List<Vaga> listarPorEmpresa(String cnpj) {
        String sql = """
            SELECT
                v.id_vaga,
                v.titulo,
                v.pais,
                v.estado,
                v.descricao,
                e.cnpj,
                e.nome
            FROM vaga v
            INNER JOIN empresa e
                ON v.cnpj_empresa = e.cnpj
            WHERE v.cnpj_empresa = ?
            ORDER BY v.id_vaga
        """

        List<Vaga> vagas = []

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, cnpj)

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vagas.add(mapearVaga(rs))
                }
            }
        }

        return vagas
    }

    void atualizar(Vaga vaga) {
        String sql = """
            UPDATE vaga
            SET titulo = ?,
                pais = ?,
                estado = ?,
                descricao = ?,
                cnpj_empresa = ?
            WHERE id_vaga = ?
        """

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, vaga.titulo)
            stmt.setString(2, vaga.pais)
            stmt.setString(3, vaga.estado)
            stmt.setString(4, vaga.descricao)
            stmt.setString(5, vaga.empresa.cnpj)
            stmt.setInt(6, vaga.id)

            stmt.executeUpdate()
        }
    }

    void excluir(Integer id) {
        String sql = """
            DELETE FROM vaga
            WHERE id_vaga = ?
        """

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id)
            stmt.executeUpdate()
        }
    }

    private static Vaga mapearVaga(ResultSet rs) {
        Empresa empresa = new Empresa(
                cnpj: rs.getString("cnpj"),
                nome: rs.getString("nome")
        )

        new Vaga(
                id: rs.getInt("id_vaga"),
                titulo: rs.getString("titulo"),
                pais: rs.getString("pais"),
                estado: rs.getString("estado"),
                descricao: rs.getString("descricao"),
                empresa: empresa
        )
    }
}