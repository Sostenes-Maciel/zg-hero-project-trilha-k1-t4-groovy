package br.com.zg.acelera.dao

import br.com.zg.acelera.model.Vaga

interface IVagaDAO extends CrudDAO<Vaga, Integer> {

    List<Vaga> listarPorEmpresa(String cnpj)
}