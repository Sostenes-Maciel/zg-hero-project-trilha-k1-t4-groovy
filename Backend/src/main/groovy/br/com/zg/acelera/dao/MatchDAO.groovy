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
        return [
                idMatch      : rs.getInt("id_match"),
                nomeCandidato: rs.getString("nome_candidato"),
                nomeEmpresa  : rs.getString("nome_empresa"),
                tituloVaga   : rs.getString("titulo_vaga")
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
    List<Map<String, Object>> listarMatchesDoCandidato(String cpfCandidato) {
        String sql = """
            SELECT m.id_match, c.nome AS nome_candidato, e.nome AS nome_empresa, v.titulo AS titulo_vaga
            FROM match_vaga m
            JOIN candidato c ON c.cpf = m.cpf_candidato
            JOIN empresa e ON e.cnpj = m.cnpj_empresa
            JOIN vaga v ON v.id_vaga = m.id_vaga
            WHERE m.cpf_candidato = ?
            ORDER BY m.id_match
        """
        return executarQuery(sql, [cpfCandidato])
    }

    @Override
    List<Map<String, Object>> listarMatchesDaEmpresa(String cnpjEmpresa) {
        String sql = """
            SELECT m.id_match, c.nome AS nome_candidato, e.nome AS nome_empresa, v.titulo AS titulo_vaga
            FROM match_vaga m
            JOIN candidato c ON c.cpf = m.cpf_candidato
            JOIN empresa e ON e.cnpj = m.cnpj_empresa
            JOIN vaga v ON v.id_vaga = m.id_vaga
            WHERE m.cnpj_empresa = ?
            ORDER BY m.id_match
        """
        return executarQuery(sql, [cnpjEmpresa])
    }
}