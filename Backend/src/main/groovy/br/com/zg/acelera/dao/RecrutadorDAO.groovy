package br.com.zg.acelera.dao

import br.com.zg.acelera.interfaces.IConexao
import br.com.zg.acelera.interfaces.IRecrutadorDAO
import br.com.zg.acelera.model.Recrutador

import java.sql.Date
import java.sql.ResultSet

class RecrutadorDAO extends AbstractDAO<Recrutador> implements IRecrutadorDAO {

    RecrutadorDAO(IConexao conexaoFactory) {
        super(conexaoFactory)
    }

    @Override
    protected Recrutador mapearRegistro(ResultSet rs) {
        Date dataNascimento = rs.getDate("data_nascimento")
        return new Recrutador(
                id: rs.getInt("id_recrutador"),
                empresaId: rs.getString("empresa_id"),
                nome: rs.getString("nome"),
                areaAtuacao: rs.getString("area_atuacao"),
                nivelPermissao: rs.getString("nivel_permissao"),
                descricao: rs.getString("descricao"),
                email: rs.getString("email"),
                cpf: rs.getString("cpf"),
                dataNascimento: dataNascimento?.toLocalDate(),
                ativo: rs.getBoolean("ativo")
        )
    }

    @Override
    void salvar(Recrutador recrutador) {
        String sql = """
            INSERT INTO recrutador
                (empresa_id, nome, area_atuacao, nivel_permissao, descricao,
                 email, cpf, data_nascimento, senha_hash, ativo)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id_recrutador
        """
        Integer idGerado = executarInsertComRetorno(sql, [
                recrutador.empresaId,
                recrutador.nome,
                recrutador.areaAtuacao,
                recrutador.nivelPermissao,
                recrutador.descricao,
                recrutador.email,
                recrutador.cpf,
                recrutador.dataNascimento == null ? null : Date.valueOf(recrutador.dataNascimento),
                recrutador.senhaHash,
                recrutador.ativo
        ])
        recrutador.id = idGerado
    }

    @Override
    void atualizar(Recrutador recrutador) {
        String sql = """
            UPDATE recrutador
               SET empresa_id = ?, nome = ?, area_atuacao = ?,
                   nivel_permissao = ?, descricao = ?, email = ?, cpf = ?,
                   data_nascimento = ?, ativo = ?
             WHERE id_recrutador = ?
        """
        executarUpdate(sql, [
                recrutador.empresaId,
                recrutador.nome,
                recrutador.areaAtuacao,
                recrutador.nivelPermissao,
                recrutador.descricao,
                recrutador.email,
                recrutador.cpf,
                recrutador.dataNascimento == null ? null : Date.valueOf(recrutador.dataNascimento),
                recrutador.ativo,
                recrutador.id
        ])
    }

    @Override
    Recrutador buscarPorId(Integer id) {
        return buscarUnico("""
            SELECT id_recrutador, empresa_id, nome, area_atuacao, nivel_permissao,
                   descricao, email, cpf, data_nascimento, ativo
              FROM recrutador
             WHERE id_recrutador = ?
        """, [id])
    }

    @Override
    Recrutador buscarPorEmail(String email) {
        return buscarUnico("""
            SELECT id_recrutador, empresa_id, nome, area_atuacao, nivel_permissao,
                   descricao, email, cpf, data_nascimento, ativo
              FROM recrutador
             WHERE email = ?
        """, [email])
    }

    @Override
    Recrutador buscarPorCpf(String cpf) {
        return buscarUnico("""
            SELECT id_recrutador, empresa_id, nome, area_atuacao, nivel_permissao,
                   descricao, email, cpf, data_nascimento, ativo
              FROM recrutador
             WHERE cpf = ?
        """, [cpf])
    }

    @Override
    List<Recrutador> buscarPorEmpresa(String empresaId) {
        return executarQuery("""
            SELECT id_recrutador, empresa_id, nome, area_atuacao, nivel_permissao,
                   descricao, email, cpf, data_nascimento, ativo
              FROM recrutador
             WHERE empresa_id = ?
             ORDER BY nome
        """, [empresaId])
    }

    @Override
    boolean inativar(Integer id) {
        return executarUpdate("""
            UPDATE recrutador
               SET ativo = FALSE
             WHERE id_recrutador = ? AND ativo = TRUE
        """, [id]) > 0
    }

    private Recrutador buscarUnico(String sql, List parametros) {
        List<Recrutador> resultados = executarQuery(sql, parametros)
        return resultados.isEmpty() ? null : resultados[0]
    }
}
