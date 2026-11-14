package br.com.zg.acelera.service

import br.com.zg.acelera.interfaces.IEmpresaDAO
import br.com.zg.acelera.interfaces.IRecrutadorDAO
import br.com.zg.acelera.model.Recrutador

class GerenciadorDeRecrutadoresService {

    private final IRecrutadorDAO recrutadorDAO
    private final IEmpresaDAO empresaDAO

    GerenciadorDeRecrutadoresService(IRecrutadorDAO recrutadorDAO, IEmpresaDAO empresaDAO) {
        this.recrutadorDAO = recrutadorDAO
        this.empresaDAO = empresaDAO
    }

    void cadastrar(Recrutador recrutador, String empresaId) {
        validarEmpresa(empresaId)
        validarDados(recrutador)
        recrutador.empresaId = empresaId
        recrutador.cpf = recrutador.cpf?.replaceAll(/\D/, '')
        recrutador.senhaHash = ValidarRecrutadorService.gerarHashSenha(recrutador.senhaHash)
        recrutadorDAO.salvar(recrutador)
    }

    void atualizar(Recrutador recrutador, String empresaId) {
        validarEmpresa(empresaId)
        validarDados(recrutador)
        if (recrutador.id == null || recrutadorDAO.buscarPorId(recrutador.id) == null) {
            throw new IllegalArgumentException("Recrutador não encontrado.")
        }
        recrutador.empresaId = empresaId
        recrutador.cpf = recrutador.cpf?.replaceAll(/\D/, '')
        recrutadorDAO.atualizar(recrutador)
    }

    boolean inativar(Integer recrutadorId, String empresaId) {
        Recrutador recrutador = recrutadorDAO.buscarPorId(recrutadorId)
        if (recrutador == null || recrutador.empresaId != empresaId) return false
        recrutadorDAO.inativar(recrutadorId)
    }

    Recrutador autenticar(String email, String senha) {
        Recrutador recrutador = recrutadorDAO.buscarPorEmail(email)
        String hash = recrutador == null ? null : recrutadorDAO.buscarHashPorEmail(email)
        if (recrutador == null || !recrutador.ativo ||
                !ValidarRecrutadorService.verificarSenha(senha, hash)) {
            return null
        }
        recrutador
    }

    List<Recrutador> listarPorEmpresa(String empresaId) {
        validarEmpresa(empresaId)
        recrutadorDAO.buscarPorEmpresa(empresaId)
    }

    private void validarEmpresa(String empresaId) {
        if (!empresaId || empresaDAO.buscarPorId(empresaId) == null) {
            throw new IllegalArgumentException("Empresa não encontrada.")
        }
    }

    private static void validarDados(Recrutador recrutador) {
        if (!recrutador?.nome?.trim() || !recrutador.areaAtuacao ||
                !recrutador.nivelPermissao || !recrutador.cpf?.trim()) {
            throw new IllegalArgumentException("Dados obrigatórios do recrutador ausentes.")
        }
        ValidarRecrutadorService.validarEmail(recrutador.email)
        ValidarRecrutadorService.validarDataNascimento(recrutador.dataNascimento)
        ValidarRecrutadorService.validarSenha(recrutador.senhaHash)
    }
}
