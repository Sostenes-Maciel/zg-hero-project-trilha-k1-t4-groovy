package br.com.zg.acelera.dao

import br.com.zg.acelera.interfaces.IConexao
import br.com.zg.acelera.interfaces.IVagaDAO
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga

import java.sql.ResultSet

class VagaDAO extends AbstractDAO<Vaga> implements IVagaDAO {

    VagaDAO(IConexao conexaoFactory) {
        super(conexaoFactory)
    }

    @Override
    protected Vaga mapearRegistro(ResultSet rs) {
        Empresa empresa = new Empresa(
                cnpj: rs.getString("cnpj"),
                nome: rs.getString("nome")
        )
        return new Vaga(
                id: rs.getInt("id_vaga"),
                titulo: rs.getString("titulo"),
                pais: rs.getString("pais"),
                estado: rs.getString("estado"),
                descricao: rs.getString("descricao"),
                empresa: empresa
        )
    }

    @Override
    void cadastrar(Vaga vaga) {
        String sql = """
            INSERT INTO vaga (titulo, pais, estado, descricao, cnpj_empresa)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id_vaga
        """
        Integer idGerado = executarInsertComRetorno(sql, [
                vaga.titulo, vaga.pais, vaga.estado, vaga.descricao, vaga.empresa.cnpj
        ])

        if (idGerado != null) {
            vaga.id = idGerado
        }
    }

    @Override
    List<Vaga> listarTodos() {
        String sql = """
            SELECT v.id_vaga, v.titulo, v.pais, v.estado, v.descricao, e.cnpj, e.nome
            FROM vaga v
            INNER JOIN empresa e ON v.cnpj_empresa = e.cnpj
            ORDER BY v.id_vaga
        """
        return executarQuery(sql, [])
    }

    @Override
    Vaga buscarPorId(Integer id) {
        String sql = """
            SELECT v.id_vaga, v.titulo, v.pais, v.estado, v.descricao, e.cnpj, e.nome
            FROM vaga v
            INNER JOIN empresa e ON v.cnpj_empresa = e.cnpj
            WHERE v.id_vaga = ?
        """
        List<Vaga> resultados = executarQuery(sql, [id])
        return resultados.isEmpty() ? null : resultados[0]
    }

    @Override
    List<Vaga> listarPorEmpresa(String cnpj) {
        String sql = """
            SELECT v.id_vaga, v.titulo, v.pais, v.estado, v.descricao, e.cnpj, e.nome
            FROM vaga v
            INNER JOIN empresa e ON v.cnpj_empresa = e.cnpj
            WHERE v.cnpj_empresa = ?
            ORDER BY v.id_vaga
        """
        return executarQuery(sql, [cnpj])
    }

    @Override
    void atualizar(Vaga vaga) {
        String sql = """
            UPDATE vaga
            SET titulo = ?, pais = ?, estado = ?, descricao = ?, cnpj_empresa = ?
            WHERE id_vaga = ?
        """
        executarUpdate(sql, [
                vaga.titulo, vaga.pais, vaga.estado, vaga.descricao, vaga.empresa.cnpj, vaga.id
        ])
    }

    @Override
    void excluir(Integer id) {
        String sql = "DELETE FROM vaga WHERE id_vaga = ?"
        executarUpdate(sql, [id])
    }

}