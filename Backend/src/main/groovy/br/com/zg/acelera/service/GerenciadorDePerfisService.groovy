package br.com.zg.acelera.service

import br.com.zg.acelera.dao.CandidatoDAO
import br.com.zg.acelera.dao.CompetenciaDAO
import br.com.zg.acelera.dao.CurtidaDAO
import br.com.zg.acelera.dao.EmpresaDAO
import br.com.zg.acelera.dao.MatchDAO
import br.com.zg.acelera.dao.VagaDAO
import br.com.zg.acelera.interfaces.IConexao
import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga

class GerenciadorDePerfisService {

    private  CandidatoDAO candidatoDAO
    private  CompetenciaDAO competenciaDAO
    private  EmpresaDAO empresaDAO
    private  CurtidaDAO curtidaDAO
    private  MatchDAO matchDAO
    private  GerenciadorDeVagasService gerenciadorDeVagas

    GerenciadorDePerfisService(IConexao conexaoFactory) {
        this.candidatoDAO = new CandidatoDAO(conexaoFactory)
        this.competenciaDAO = new CompetenciaDAO(conexaoFactory)
        this.empresaDAO = new EmpresaDAO(conexaoFactory)
        this.curtidaDAO = new CurtidaDAO(conexaoFactory)
        this.matchDAO = new MatchDAO(conexaoFactory)
        this.gerenciadorDeVagas = new GerenciadorDeVagasService(new VagaDAO(conexaoFactory), this.competenciaDAO)
    }

    void cadastrarCandidato(Candidato candidato) {
        ValidarCandidatoService.validarNome(candidato.nome)
        ValidarCandidatoService.validarEmail(candidato.email)
        ValidarCandidatoService.validarCpf(candidato.cpf)
        ValidarCandidatoService.validarIdade(candidato.idade)
        ValidarCandidatoService.validarEstado(candidato.estado)
        ValidarCandidatoService.validarCep(candidato.cep)
        ValidarCandidatoService.validarDescricao(candidato.descricao)
        candidatoDAO.cadastrar(candidato)
    }

    void cadastrarEmpresa(Empresa empresa) {
        ValidarEmpresaService.validarNome(empresa.nome)
        ValidarEmpresaService.validarEmail(empresa.email)
        ValidarEmpresaService.validarCnpj(empresa.cnpj)
        ValidarEmpresaService.validarPais(empresa.pais)
        ValidarEmpresaService.validarEstado(empresa.estado)
        ValidarEmpresaService.validarCep(empresa.cep)
        ValidarEmpresaService.validarDescricao(empresa.descricao)
        empresaDAO.cadastrar(empresa)
    }

    List<Candidato> listarCandidatos() {
        return candidatoDAO.listarTodos()
    }

    List<Empresa> listarEmpresas() {
        return empresaDAO.listarTodos()
    }

    void atualizarCandidato(Candidato candidato) {
        ValidarCandidatoService.validarNome(candidato.nome)
        ValidarCandidatoService.validarEmail(candidato.email)
        ValidarCandidatoService.validarIdade(candidato.idade)
        ValidarCandidatoService.validarEstado(candidato.estado)
        ValidarCandidatoService.validarCep(candidato.cep)
        ValidarCandidatoService.validarDescricao(candidato.descricao)
        candidatoDAO.atualizar(candidato)
    }

    void excluirCandidato(String cpf) {
        candidatoDAO.excluir(cpf)
    }

    void atualizarEmpresa(Empresa empresa) {
        ValidarEmpresaService.validarNome(empresa.nome)
        ValidarEmpresaService.validarEmail(empresa.email)
        ValidarEmpresaService.validarPais(empresa.pais)
        ValidarEmpresaService.validarEstado(empresa.estado)
        ValidarEmpresaService.validarCep(empresa.cep)
        ValidarEmpresaService.validarDescricao(empresa.descricao)
        empresaDAO.atualizar(empresa)
    }

    void excluirEmpresa(String cnpj) {
        empresaDAO.excluir(cnpj)
    }

    List<String> listarCompetenciasDoCandidato(Candidato candidato) {
        return competenciaDAO.listarDoCandidato(candidato.cpf)
    }

    void adicionarCompetenciaAoCandidato(Candidato candidato, String competencia) {
        competenciaDAO.garantirCompetencia(competencia)
        competenciaDAO.adicionarAoCandidato(candidato.cpf, competencia)
    }

    void removerCompetenciaDoCandidato(Candidato candidato, String competencia) {
        competenciaDAO.removerDoCandidato(candidato.cpf, competencia)
    }

    List<String> listarCompetenciasDaEmpresa(Empresa empresa) {
        return competenciaDAO.listarDaEmpresa(empresa.cnpj)
    }

    void adicionarCompetenciaAEmpresa(Empresa empresa, String competencia) {
        competenciaDAO.garantirCompetencia(competencia)
        competenciaDAO.adicionarAoEmpresa(empresa.cnpj, competencia)
    }

    void removerCompetenciaDaEmpresa(Empresa empresa, String competencia) {
        competenciaDAO.removerDaEmpresa(empresa.cnpj, competencia)
    }

    boolean curtirVaga(String cpfCandidato, Vaga vaga) {
        curtidaDAO.registrarCurtidaCandidato(cpfCandidato, vaga.id)
        return processarPossivelMatch(cpfCandidato, vaga.empresa.cnpj, vaga.id)
    }

    boolean curtirCandidato(String cnpjEmpresa, String cpfCandidato, Integer idVaga) {
        curtidaDAO.registrarCurtidaEmpresa(cpfCandidato, cnpjEmpresa)
        return processarPossivelMatch(cpfCandidato, cnpjEmpresa, idVaga)
    }

    boolean processarPossivelMatch(String cpf, String cnpj, int vagaId) {
        if (curtidaDAO.verificarMatch(cpf, cnpj, vagaId)) {
            if (!matchDAO.existeMatch(cpf, cnpj, vagaId)) {
                matchDAO.registrarMatch(cpf, cnpj, vagaId)
            }
            return true
        }
        return false
    }
}