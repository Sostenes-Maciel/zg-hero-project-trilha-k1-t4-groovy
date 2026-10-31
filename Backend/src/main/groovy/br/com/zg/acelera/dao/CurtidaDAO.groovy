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

    @Override
    boolean removerCurtidaCandidato(String cpfCandidato, Integer idVaga) {
        if (!jaCurtiuVaga(cpfCandidato, idVaga)) {
            return false
        }

        String sqlDeletar = "DELETE FROM curtida WHERE cpf_candidato = ? AND id_vaga = ? AND cnpj_empresa IS NULL"
        executarUpdate(sqlDeletar, [cpfCandidato, idVaga])
        return true
    }

    @Override
    List listarStatusCurtidas(String cpf) {
        String sql = "SELECT id_vaga FROM curtida WHERE cpf_candidato = ? AND id_vaga IS NOT NULL AND cnpj_empresa IS NULL"
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
                resultados.add([idVaga: rs.getInt("id_vaga"), status: "curtido"])
            }
        } catch (Exception e) {
            e.printStackTrace()
        } finally {
            if (rs != null) rs.close()
            if (stmt != null) stmt.close()
            if (conexao != null) conexao.close()
        }
        return resultados
    }
    @Override
    List listarStatusCurtidasEmpresa(String cnpj) {
        String sql = "SELECT cpf_candidato FROM curtida WHERE cnpj_empresa = ? AND id_vaga IS NULL"
        List resultados = []
        Connection conexao = null
        PreparedStatement stmt = null
        ResultSet rs = null

        try {
            conexao = conexaoFactory.conectar()
            stmt = conexao.prepareStatement(sql)
            stmt.setString(1, cnpj)
            rs = stmt.executeQuery()

            while (rs.next()) {
                // Formato que o frontend da empresa vai ler: { cpfCandidato: "11122233394", status: "curtido" }
                resultados.add([cpfCandidato: rs.getString("cpf_candidato"), status: "curtido"])
            }
        } catch (Exception e) {
            e.printStackTrace()
        } finally {
            if (rs != null) rs.close()
            if (stmt != null) stmt.close()
            if (conexao != null) conexao.close()
        }
        return resultados
    }
}