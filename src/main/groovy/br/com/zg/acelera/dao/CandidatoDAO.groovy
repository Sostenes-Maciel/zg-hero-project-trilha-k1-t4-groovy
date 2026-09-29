package br.com.zg.acelera.dao

import br.com.zg.acelera.model.Candidato

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class CandidatoDAO {

    void cadastrar(Candidato candidato) {
        String sql = """
            INSERT INTO candidato (
                cpf,
                nome,
                email,
                pais,
                estado,
                cep,
                descricao,
                idade
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, candidato.cpf)
            stmt.setString(2, candidato.nome)
            stmt.setString(3, candidato.email)
            stmt.setString(4, candidato.pais)
            stmt.setString(5, candidato.estado)
            stmt.setString(6, candidato.cep)
            stmt.setString(7, candidato.descricao)
            stmt.setInt(8, candidato.idade)

            stmt.executeUpdate()
        }
    }

    List<Candidato> listarTodos() {
        String sql = """
            SELECT
                cpf,
                nome,
                email,
                pais,
                estado,
                cep,
                descricao,
                idade
            FROM candidato
            ORDER BY nome
        """

        List<Candidato> candidatos = []

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                candidatos.add(mapearCandidato(rs))
            }
        }

        return candidatos
    }

    Candidato buscarPorCpf(String cpf) {
        String sql = """
            SELECT
                cpf,
                nome,
                email,
                pais,
                estado,
                cep,
                descricao,
                idade
            FROM candidato
            WHERE cpf = ?
        """

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, cpf)

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCandidato(rs)
                }
            }
        }

        return null
    }

    void atualizar(Candidato candidato) {
        String sql = """
            UPDATE candidato
            SET nome = ?,
                email = ?,
                pais = ?,
                estado = ?,
                cep = ?,
                descricao = ?,
                idade = ?
            WHERE cpf = ?
        """

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, candidato.nome)
            stmt.setString(2, candidato.email)
            stmt.setString(3, candidato.pais)
            stmt.setString(4, candidato.estado)
            stmt.setString(5, candidato.cep)
            stmt.setString(6, candidato.descricao)
            stmt.setInt(7, candidato.idade)
            stmt.setString(8, candidato.cpf)

            stmt.executeUpdate()
        }
    }

    void excluir(String cpf) {
        String sql = "DELETE FROM candidato WHERE cpf = ?"

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, cpf)
            stmt.executeUpdate()
        }
    }

    private static Candidato mapearCandidato(ResultSet rs) {
        new Candidato(
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
}