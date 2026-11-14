package br.com.zg.acelera.dao

import br.com.zg.acelera.interfaces.IConexao
import br.com.zg.acelera.interfaces.ICurtidaDAO
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class CurtidaDAO extends AbstractDAO<Void> implements ICurtidaDAO {

    CurtidaDAO(IConexao conexaoFactory) {
        super(conexaoFactory)
    }

    @Override
    protected Void mapearRegistro(ResultSet rs) {
        return null
    }

    @Override
    boolean jaCurtiuVaga(String cpfCandidato, Integer idVaga) {
        String sqlVerificar = "SELECT 1 FROM curtida WHERE cpf_candidato = ? AND id_vaga = ? AND cnpj_empresa IS NULL"
        return executarQueryBooleana(sqlVerificar, [cpfCandidato, idVaga])
    }

    @Override
    boolean jaCurtiuCandidato(String cnpjEmpresa, String cpfCandidato, Integer idVaga) {
        String sqlVerificar = "SELECT 1 FROM curtida WHERE cpf_candidato = ? AND cnpj_empresa = ? AND id_vaga = ?"
        return executarQueryBooleana(sqlVerificar, [cpfCandidato, cnpjEmpresa, idVaga])
    }

    @Override
    boolean jaCurtiuCandidatoPorRecrutador(Integer recrutadorId, String cpfCandidato, Integer idVaga) {
        String sql = "SELECT 1 FROM curtida WHERE recrutador_id = ? AND cpf_candidato = ? AND id_vaga = ?"
        executarQueryBooleana(sql, [recrutadorId, cpfCandidato, idVaga])
    }

    @Override
    void registrarCurtidaCandidato(String cpfCandidato, Integer idVaga) {
        if (jaCurtiuVaga(cpfCandidato, idVaga)) return

        String sqlInserir = "INSERT INTO curtida (cpf_candidato, id_vaga) VALUES (?, ?)"
        executarUpdate(sqlInserir, [cpfCandidato, idVaga])
    }

    @Override
    void registrarCurtidaEmpresa(String cpfCandidato, String cnpjEmpresa, Integer idVaga) {
        registrarCurtidaEmpresa(cpfCandidato, cnpjEmpresa, idVaga, null)
    }

    @Override
    void registrarCurtidaEmpresa(String cpfCandidato, String cnpjEmpresa, Integer idVaga, Integer recrutadorId) {
        String sqlInserir = """
            INSERT INTO curtida (cpf_candidato, cnpj_empresa, id_vaga, recrutador_id)
            VALUES (?, ?, ?, ?)
            ON CONFLICT DO NOTHING
        """
        executarUpdate(sqlInserir, [cpfCandidato, cnpjEmpresa, idVaga, recrutadorId])
    }

    @Override
    boolean verificarMatch(String cpfCandidato, String cnpjEmpresa, Integer idVaga) {
        String sql = """
            SELECT 1
            FROM curtida c1
            WHERE c1.cpf_candidato = ?
              AND c1.id_vaga = ?
              AND c1.cnpj_empresa IS NULL
              AND EXISTS (
                  SELECT 1
                  FROM curtida c2
                  WHERE c2.cpf_candidato = ?
                    AND c2.cnpj_empresa = ?
                    AND c2.id_vaga = ?
              )
        """
        return executarQueryBooleana(sql, [cpfCandidato, idVaga, cpfCandidato, cnpjEmpresa, idVaga])
    }

    @Override
    boolean removerCurtidaEmpresa(String cnpjEmpresa, String cpfCandidato, Integer idVaga) {
        String sqlDeletar = """
            DELETE FROM curtida c
            WHERE c.cnpj_empresa = ?
              AND c.cpf_candidato = ?
              AND c.id_vaga = ?
              AND NOT EXISTS (
                  SELECT 1 FROM match_vaga m
                  WHERE m.cnpj_empresa = c.cnpj_empresa
                    AND m.cpf_candidato = c.cpf_candidato
                    AND m.id_vaga = c.id_vaga
              )
        """
        return executarUpdate(sqlDeletar, [cnpjEmpresa, cpfCandidato, idVaga]) > 0
    }

    @Override
    boolean removerCurtidaCandidato(String cpfCandidato, Integer idVaga) {
        String sqlDeletar = """
            DELETE FROM curtida c
            WHERE c.cpf_candidato = ?
              AND c.id_vaga = ?
              AND c.cnpj_empresa IS NULL
              AND NOT EXISTS (
                  SELECT 1 FROM match_vaga m
                  WHERE m.cpf_candidato = c.cpf_candidato
                    AND m.id_vaga = c.id_vaga
              )
        """
        return executarUpdate(sqlDeletar, [cpfCandidato, idVaga]) > 0
    }

    @Override
    List listarStatusCurtidas(String cpf) {
        String sql = """
            SELECT c.id_vaga,
                   CASE WHEN m.id_match IS NULL THEN 'curtido' ELSE 'match' END AS status
            FROM curtida c
            LEFT JOIN match_vaga m
              ON m.cpf_candidato = c.cpf_candidato
             AND m.id_vaga = c.id_vaga
            WHERE c.cpf_candidato = ?
              AND c.id_vaga IS NOT NULL
              AND c.cnpj_empresa IS NULL
        """
        List resultados = []
        Connection conexao = null
        PreparedStatement stmt = null
        ResultSet rs = null

        try {
            conexao = conexaoFactory.conectar()
            stmt = conexao.prepareStatement(sql)
            stmt.setString(1, cpf)
            rs = stmt.executeQuery()

            while (rs.next()) {
                resultados.add([idVaga: rs.getInt("id_vaga"), status: rs.getString("status")])
            }
        } catch (Exception e) {
            throw new RuntimeException("Falha ao listar curtidas do candidato", e)
        } finally {
            fecharRecursosCurtida(rs, stmt, conexao)
        }
        return resultados
    }
    @Override
    List listarStatusCurtidasEmpresa(String cnpj) {
        String sql = """
            SELECT cpf_candidato, id_vaga, recrutador_id, status
            FROM (
                SELECT c.cpf_candidato, c.id_vaga, c.recrutador_id,
                       CASE WHEN m.id_match IS NULL THEN 'curtido' ELSE 'match' END AS status
                FROM curtida c
                LEFT JOIN match_vaga m
                  ON m.cpf_candidato = c.cpf_candidato
                 AND m.cnpj_empresa = c.cnpj_empresa
                 AND m.id_vaga = c.id_vaga
                WHERE c.cnpj_empresa = ?
                  AND c.id_vaga IS NOT NULL
                UNION
                SELECT m.cpf_candidato, m.id_vaga, NULL::integer AS recrutador_id, 'match' AS status
                FROM match_vaga m
                WHERE m.cnpj_empresa = ?
            ) status_empresa
            ORDER BY cpf_candidato, id_vaga
        """
        List resultados = []
        Connection conexao = null
        PreparedStatement stmt = null
        ResultSet rs = null

        try {
            conexao = conexaoFactory.conectar()
            stmt = conexao.prepareStatement(sql)
            stmt.setString(1, cnpj)
            stmt.setString(2, cnpj)
            rs = stmt.executeQuery()

            while (rs.next()) {
                resultados.add([
                        cpfCandidato: rs.getString("cpf_candidato"),
                        idVaga: rs.getInt("id_vaga"),
                        recrutadorId: rs.getObject("recrutador_id"),
                        status: rs.getString("status")
                ])
            }
        } catch (Exception e) {
            throw new RuntimeException("Falha ao listar curtidas da empresa", e)
        } finally {
            fecharRecursosCurtida(rs, stmt, conexao)
        }
        return resultados
    }

    private static void fecharRecursosCurtida(ResultSet rs, PreparedStatement stmt, Connection conexao) {
        try {
            rs?.close()
        } catch (Exception e) {
            e.printStackTrace()
        }
        try {
            stmt?.close()
        } catch (Exception e) {
            e.printStackTrace()
        }
        try {
            conexao?.close()
        } catch (Exception e) {
            e.printStackTrace()
        }
    }
}