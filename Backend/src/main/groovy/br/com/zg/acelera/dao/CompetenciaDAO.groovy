package br.com.zg.acelera.dao

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class CompetenciaDAO {

    void cadastrar(String nome) {
        String sql = """
            INSERT INTO competencia (nome)
            VALUES (?)
        """

        try (Connection conexao = ConexaoBD.conectar()
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, nome)
            stmt.executeUpdate()
        }
    }

    List<String> listarTodos() {
        String sql = """
            SELECT nome
            FROM competencia
            ORDER BY nome
        """

        List<String> competencias = []

        try (Connection conexao = ConexaoBD.conectar()
             PreparedStatement stmt = conexao.prepareStatement(sql)
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                competencias.add(rs.getString("nome"))
            }
        }

        return competencias
    }

    void atualizar(String nomeAtual, String novoNome) {
        String sql = """
            UPDATE competencia
            SET nome = ?
            WHERE nome = ?
        """

        try (Connection conexao = ConexaoBD.conectar()
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, novoNome)
            stmt.setString(2, nomeAtual)
            stmt.executeUpdate()
        }
    }

    void excluir(String nome) {
        String sql = """
            DELETE FROM competencia
            WHERE nome = ?
        """

        try (Connection conexao = ConexaoBD.conectar()
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, nome)
            stmt.executeUpdate()
        }
    }
    void adicionarAoCandidato(String cpf, String nomeCompetencia) {
        String sql = """
        INSERT INTO candidato_competencia (cpf_candidato, id_competencia)
        SELECT ?, id_competencia
        FROM competencia
        WHERE nome = ?
        ON CONFLICT DO NOTHING
    """

        try (Connection conexao = ConexaoBD.conectar()
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, cpf)
            stmt.setString(2, nomeCompetencia)

            stmt.executeUpdate()
        }
    }

    void removerDoCandidato(String cpf, String nomeCompetencia) {
        String sql = """
        DELETE FROM candidato_competencia
        WHERE cpf_candidato = ?
          AND id_competencia = (
              SELECT id_competencia
              FROM competencia
              WHERE nome = ?
          )
    """

        try (Connection conexao = ConexaoBD.conectar()
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, cpf)
            stmt.setString(2, nomeCompetencia)

            stmt.executeUpdate()
        }
    }

    List<String> listarDoCandidato(String cpf) {
        String sql = """
        SELECT c.nome
        FROM competencia c
        INNER JOIN candidato_competencia cc
            ON cc.id_competencia = c.id_competencia
        WHERE cc.cpf_candidato = ?
        ORDER BY c.nome
    """

        List<String> competencias = []

        try (Connection conexao = ConexaoBD.conectar()
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, cpf)

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    competencias.add(rs.getString("nome"))
                }
            }
        }

        return competencias
    }
    void adicionarAoEmpresa(String cnpj, String nomeCompetencia) {
        String sql = """
        INSERT INTO empresa_competencia (cnpj_empresa, id_competencia)
        SELECT ?, id_competencia
        FROM competencia
        WHERE nome = ?
        ON CONFLICT DO NOTHING
    """

        try (Connection conexao = ConexaoBD.conectar()
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, cnpj)
            stmt.setString(2, nomeCompetencia)

            stmt.executeUpdate()
        }
    }

    void removerDaEmpresa(String cnpj, String nomeCompetencia) {
        String sql = """
        DELETE FROM empresa_competencia
        WHERE cnpj_empresa = ?
          AND id_competencia = (
              SELECT id_competencia
              FROM competencia
              WHERE nome = ?
          )
    """

        try (Connection conexao = ConexaoBD.conectar()
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, cnpj)
            stmt.setString(2, nomeCompetencia)

            stmt.executeUpdate()
        }
    }

    List<String> listarDaEmpresa(String cnpj) {
        String sql = """
        SELECT c.nome
        FROM competencia c
        INNER JOIN empresa_competencia ec
            ON ec.id_competencia = c.id_competencia
        WHERE ec.cnpj_empresa = ?
        ORDER BY c.nome
    """

        List<String> competencias = []

        try (Connection conexao = ConexaoBD.conectar()
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, cnpj)

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    competencias.add(rs.getString("nome"))
                }
            }
        }

        return competencias
    }

    void adicionarAVaga(Integer idVaga, String nomeCompetencia) {
        String sql = """
        INSERT INTO vaga_competencia (id_vaga, id_competencia)
        SELECT ?, id_competencia
        FROM competencia
        WHERE nome = ?
        ON CONFLICT DO NOTHING
    """

        try (Connection conexao = ConexaoBD.conectar()
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idVaga)
            stmt.setString(2, nomeCompetencia)

            stmt.executeUpdate()
        }
    }

    void removerDaVaga(Integer idVaga, String nomeCompetencia) {
        String sql = """
        DELETE FROM vaga_competencia
        WHERE id_vaga = ?
          AND id_competencia = (
              SELECT id_competencia
              FROM competencia
              WHERE nome = ?
          )
    """

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idVaga)
            stmt.setString(2, nomeCompetencia)

            stmt.executeUpdate()
        }
    }

    List<String> listarDaVaga(Integer idVaga) {
        String sql = """
        SELECT c.nome
        FROM competencia c
        INNER JOIN vaga_competencia vc
            ON vc.id_competencia = c.id_competencia
        WHERE vc.id_vaga = ?
        ORDER BY c.nome
    """

        List<String> competencias = []

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idVaga)

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    competencias.add(rs.getString("nome"))
                }
            }
        }

        return competencias
    }
    void garantirCompetencia(String nome) {
        String sql = """
        INSERT INTO competencia (nome)
        VALUES (?)
        ON CONFLICT (nome) DO NOTHING
    """

        try (Connection conexao = ConexaoBD.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, nome)
            stmt.executeUpdate()
        }
    }
}