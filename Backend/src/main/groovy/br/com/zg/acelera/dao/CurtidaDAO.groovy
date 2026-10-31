package br.com.zg.acelera.dao

import br.com.zg.acelera.interfaces.IConexao
import br.com.zg.acelera.interfaces.ICurtidaDAO
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
    boolean jaCurtiuCandidato(String cnpjEmpresa, String cpfCandidato) {
        String sqlVerificar = "SELECT 1 FROM curtida WHERE cpf_candidato = ? AND cnpj_empresa = ? AND id_vaga IS NULL"
        return executarQueryBooleana(sqlVerificar, [cpfCandidato, cnpjEmpresa])
    }

    @Override
    void registrarCurtidaCandidato(String cpfCandidato, Integer idVaga) {
        if (jaCurtiuVaga(cpfCandidato, idVaga)) return

        String sqlInserir = "INSERT INTO curtida (cpf_candidato, id_vaga) VALUES (?, ?)"
        executarUpdate(sqlInserir, [cpfCandidato, idVaga])
    }

    @Override
    void registrarCurtidaEmpresa(String cpfCandidato, String cnpjEmpresa) {
        if (jaCurtiuCandidato(cnpjEmpresa, cpfCandidato)) return

        String sqlInserir = "INSERT INTO curtida (cpf_candidato, cnpj_empresa) VALUES (?, ?)"
        executarUpdate(sqlInserir, [cpfCandidato, cnpjEmpresa])
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
                    AND c2.id_vaga IS NULL
              )
        """
        return executarQueryBooleana(sql, [cpfCandidato, idVaga, cpfCandidato, cnpjEmpresa])
    }
    @Override
    boolean removerCurtidaEmpresa(String cnpjEmpresa, String cpfCandidato) {
        if (!jaCurtiuCandidato(cnpjEmpresa, cpfCandidato)) {
            return false
        }

        String sqlDeletar = "DELETE FROM curtida WHERE cnpj_empresa = ? AND cpf_candidato = ? AND id_vaga IS NULL"

        executarUpdate(sqlDeletar, [cnpjEmpresa, cpfCandidato])

        return true
    }
}