package br.com.zg.acelera.dao

import br.com.zg.acelera.interfaces.ICandidatoDAO
import br.com.zg.acelera.interfaces.ICompetenciaDAO
import br.com.zg.acelera.interfaces.IConexao
import br.com.zg.acelera.model.Candidato
import java.sql.ResultSet

class CandidatoDAO extends AbstractDAO<Candidato> implements ICandidatoDAO {

    private ICompetenciaDAO competenciaDAO

    CandidatoDAO(IConexao conexaoFactory, ICompetenciaDAO competenciaDAO) {
        super(conexaoFactory)
        this.competenciaDAO = competenciaDAO
    }

    @Override
    protected Candidato mapearRegistro(ResultSet rs) {
        return new Candidato(
                cpf: rs.getString("cpf"),
                nome: rs.getString("nome"),
                email: rs.getString("email"),
                pais: rs.getString("pais"),
                estado: rs.getString("estado"),
                cep: rs.getString("cep"),
                descricao: rs.getString("descricao"),
                idade: rs.getInt("idade"),
                competencias: []
        )
    }

    @Override
    void cadastrar(Candidato candidato) {
        String sql = """
            INSERT INTO candidato (cpf, nome, email, pais, estado, cep, descricao, idade)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """
        executarUpdate(sql, [
                candidato.cpf, candidato.nome, candidato.email, candidato.pais,
                candidato.estado, candidato.cep, candidato.descricao, candidato.idade
        ])
    }

    @Override
    List<Candidato> listarTodos() {
        String sql = "SELECT cpf, nome, email, pais, estado, cep, descricao, idade FROM candidato ORDER BY nome"
        List<Candidato> candidatos = executarQuery(sql, [])

        candidatos.each { c ->
            c.competencias = competenciaDAO.listarDoCandidato(c.cpf)
        }

        return candidatos
    }

    @Override
    Candidato buscarPorId(String cpf) {
        String sql = "SELECT cpf, nome, email, pais, estado, cep, descricao, idade FROM candidato WHERE cpf = ?"
        List<Candidato> resultados = executarQuery(sql, [cpf])

        if (resultados.isEmpty()) {
            return null
        }

        Candidato candidato = resultados[0]
        candidato.competencias = competenciaDAO.listarDoCandidato(candidato.cpf)

        return candidato
    }

    @Override
    void atualizar(Candidato candidato) {
        String sql = """
            UPDATE candidato
            SET nome = ?, email = ?, pais = ?, estado = ?, cep = ?, descricao = ?, idade = ?
            WHERE cpf = ?
        """
        executarUpdate(sql, [
                candidato.nome, candidato.email, candidato.pais, candidato.estado,
                candidato.cep, candidato.descricao, candidato.idade, candidato.cpf
        ])
    }

    @Override
    void excluir(String cpf) {
        String sql = "DELETE FROM candidato WHERE cpf = ?"
        executarUpdate(sql, [cpf])
    }
}