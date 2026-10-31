package br.com.zg.acelera.dao

import br.com.zg.acelera.interfaces.IConexao
import br.com.zg.acelera.interfaces.IMatchDAO
import java.sql.ResultSet

class MatchDAO extends AbstractDAO<Map<String, Object>> implements IMatchDAO {

    MatchDAO(IConexao conexaoFactory) {
        super(conexaoFactory)
    }

    @Override
    protected Map<String, Object> mapearRegistro(ResultSet rs) {

        List<String> competencias = []

        if (possuiColuna(rs, "competencias_candidato")) {
            def sqlArray = rs.getArray("competencias_candidato")

            if (sqlArray != null) {
                try {
                    competencias = sqlArray.getArray().toList().collect {
                        it.toString()
                    }
                } finally {
                    sqlArray.free()
                }
            }
        }

        return [
                idMatch: rs.getInt("id_match"),
                candidato: [
                        nome: rs.getString("nome_candidato"),
                        competencias: competencias
                ],
                empresa: [
                        nome: rs.getString("nome_empresa")
                ],
                vaga: [
                        titulo: rs.getString("titulo_vaga"),
                        pais: rs.getString("pais_vaga"),
                        estado: rs.getString("estado_vaga")
                ]
        ] as Map<String, Object>
    }

    @Override
    boolean existeMatch(String cpfCandidato, String cnpjEmpresa, Integer idVaga) {
        String sql = """
            SELECT 1 FROM match_vaga
            WHERE cpf_candidato = ? AND cnpj_empresa = ? AND id_vaga = ?
        """
        return executarQueryBooleana(sql, [cpfCandidato, cnpjEmpresa, idVaga])
    }

    @Override
    void registrarMatch(String cpfCandidato, String cnpjEmpresa, Integer idVaga) {
        if (existeMatch(cpfCandidato, cnpjEmpresa, idVaga)) {
            return
        }

        String sql = "INSERT INTO match_vaga (cpf_candidato, cnpj_empresa, id_vaga) VALUES (?, ?, ?)"
        executarUpdate(sql, [cpfCandidato, cnpjEmpresa, idVaga])
    }

    @Override
    List<Map<String, Object>> listarMatches() {
        String sql = """
            SELECT m.id_match, c.nome AS nome_candidato, e.nome AS nome_empresa, v.titulo AS titulo_vaga
            FROM match_vaga m
            JOIN candidato c ON c.cpf = m.cpf_candidato
            JOIN empresa e ON e.cnpj = m.cnpj_empresa
            JOIN vaga v ON v.id_vaga = m.id_vaga
            ORDER BY m.id_match
        """
        return executarQuery(sql, [])
    }

    @Override
    List listarMatchesDoCandidato(String cpfCandidato) {
        String sql = """
            SELECT m.id_match, c.nome AS nome_candidato, e.nome AS nome_empresa, 
                   v.titulo AS titulo_vaga, v.pais AS pais_vaga, v.estado AS estado_vaga
            FROM match_vaga m
            JOIN candidato c ON c.cpf = m.cpf_candidato
            JOIN empresa e ON e.cnpj = m.cnpj_empresa
            JOIN vaga v ON v.id_vaga = m.id_vaga
            WHERE m.cpf_candidato = ?
            ORDER BY m.id_match
        """
        return executarQuery(sql, [cpfCandidato])
    }

    List listarCurtidasPendentesDoCandidato(String cpfCandidato) {
        String sql = """
            SELECT 0 AS id_match, '' AS nome_candidato, '' AS nome_empresa, 
                   v.titulo AS titulo_vaga, v.pais AS pais_vaga, v.estado AS estado_vaga
            FROM curtida c
            JOIN vaga v ON v.id_vaga = c.id_vaga
            WHERE c.cpf_candidato = ? AND c.cnpj_empresa IS NULL
              AND NOT EXISTS (
                  SELECT 1 FROM match_vaga m WHERE m.id_vaga = c.id_vaga AND m.cpf_candidato = c.cpf_candidato
              )
        """
        return executarQuery(sql, [cpfCandidato])
    }

    @Override
    List listarMatchesDaEmpresa(String cnpjEmpresa) {
        String sql = """
            SELECT m.id_match, c.nome AS nome_candidato, e.nome AS nome_empresa, 
                   v.titulo AS titulo_vaga, v.pais AS pais_vaga, v.estado AS estado_vaga
            FROM match_vaga m
            JOIN candidato c ON c.cpf = m.cpf_candidato
            JOIN empresa e ON e.cnpj = m.cnpj_empresa
            JOIN vaga v ON v.id_vaga = m.id_vaga
            WHERE m.cnpj_empresa = ?
            ORDER BY m.id_match
        """
        return executarQuery(sql, [cnpjEmpresa])
    }

    List listarCurtidasPendentesDaEmpresa(String cnpjEmpresa) {
        String sql = """
        SELECT
            0 AS id_match,
            NULL::text AS nome_candidato,
            NULL::text AS nome_empresa,
            NULL::text AS titulo_vaga,
            NULL::text AS pais_vaga,
            NULL::text AS estado_vaga,
            (
                SELECT ARRAY_AGG(DISTINCT comp.nome ORDER BY comp.nome)
                FROM candidato_competencia cc
                JOIN competencia comp
                    ON comp.id_competencia = cc.id_competencia
                WHERE cc.cpf_candidato = cu.cpf_candidato
            ) AS competencias_candidato
        FROM curtida cu
        WHERE cu.cnpj_empresa = ?
          AND cu.id_vaga IS NULL
          AND NOT EXISTS (
              SELECT 1
              FROM match_vaga m
              WHERE m.cnpj_empresa = cu.cnpj_empresa
                AND m.cpf_candidato = cu.cpf_candidato
          )
        ORDER BY cu.id_curtida
    """
        return executarQuery(sql, [cnpjEmpresa])
    }
    private static boolean possuiColuna(ResultSet rs, String nomeColuna) {
        def metadata = rs.getMetaData()

        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            if (metadata.getColumnLabel(i).equalsIgnoreCase(nomeColuna)) {
                return true
            }
        }

        return false
    }
}