package br.com.zg.acelera.interfaces

import br.com.zg.acelera.model.Recrutador

interface IRecrutadorDAO {
    void salvar(Recrutador recrutador)
    void atualizar(Recrutador recrutador)
    Recrutador buscarPorId(Integer id)
    Recrutador buscarPorEmail(String email)
    String buscarHashPorEmail(String email)
    Recrutador buscarPorCpf(String cpf)
    List<Recrutador> buscarPorEmpresa(String empresaId)
    boolean inativar(Integer id)
}
