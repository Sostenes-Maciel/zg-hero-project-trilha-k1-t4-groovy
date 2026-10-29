package br.com.zg.acelera.service

import br.com.zg.acelera.interfaces.ICandidatoDAO
import br.com.zg.acelera.interfaces.ICompetenciaDAO
import br.com.zg.acelera.interfaces.ICurtidaDAO
import br.com.zg.acelera.interfaces.IEmpresaDAO
import br.com.zg.acelera.interfaces.IMatchDAO
import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga

class GerenciadorDePerfisService {

    private final ICandidatoDAO candidatoDAO
    private final ICompetenciaDAO competenciaDAO
    private final IEmpresaDAO empresaDAO
    private final ICurtidaDAO curtidaDAO
    private final IMatchDAO matchDAO
    private final GerenciadorDeVagasService gerenciadorDeVagas

    GerenciadorDePerfisService(ICandidatoDAO candidatoDAO, ICompetenciaDAO competenciaDAO,
                               IEmpresaDAO empresaDAO, ICurtidaDAO curtidaDAO,
                               IMatchDAO matchDAO, GerenciadorDeVagasService gerenciadorDeVagas) {
        this.candidatoDAO = candidatoDAO
        this.competenciaDAO = competenciaDAO
        this.empresaDAO = empresaDAO
        this.curtidaDAO = curtidaDAO
        this.matchDAO = matchDAO
        this.gerenciadorDeVagas = gerenciadorDeVagas
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

        candidato.competencias?.each { String competencia ->
            if (competencia?.trim()) {
                adicionarCompetenciaAoCandidato(candidato, competencia.trim())
            }
        }
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

        empresa.competencias?.each { String competencia ->
            if (competencia?.trim()) {
                adicionarCompetenciaAEmpresa(empresa, competencia.trim())
            }
        }
    }

    List<Candidato> listarCandidatos() {
        return candidatoDAO.listarTodos()
    }

    List<Empresa> listarEmpresas() {
        return empresaDAO.listarTodos()
    }

    Candidato buscarCandidatoPorCpf(String cpf) {
        return candidatoDAO.buscarPorId(cpf)
    }

    Empresa buscarEmpresaPorCnpj(String cnpj) {
        return empresaDAO.buscarPorId(cnpj)
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


    void adicionarCompetenciaAoCandidato(Candidato candidato, String competencia) {
        competenciaDAO.garantirCompetencia(competencia)
        competenciaDAO.adicionarAoCandidato(candidato.cpf, competencia)
    }

    void removerCompetenciaDoCandidato(Candidato candidato, String competencia) {
        competenciaDAO.removerDoCandidato(candidato.cpf, competencia)
    }


    void adicionarCompetenciaAEmpresa(Empresa empresa, String competencia) {
        competenciaDAO.garantirCompetencia(competencia)
        competenciaDAO.adicionarAoEmpresa(empresa.cnpj, competencia)
    }

    void removerCompetenciaDaEmpresa(Empresa empresa, String competencia) {
        competenciaDAO.removerDaEmpresa(empresa.cnpj, competencia)
    }

    boolean curtirVaga(String cpfCandidato, Vaga vaga) {
        if (curtidaDAO.jaCurtiuVaga(cpfCandidato, vaga.id)) {
            throw new IllegalStateException("Você já curtiu esta vaga!")
        }

        curtidaDAO.registrarCurtidaCandidato(cpfCandidato, vaga.id)
        return processarPossivelMatch(cpfCandidato, vaga.empresa.cnpj, vaga.id)
    }

    boolean curtirCandidato(String cnpjEmpresa, String cpfCandidato, Integer idVaga) {
        if (curtidaDAO.jaCurtiuCandidato(cnpjEmpresa, cpfCandidato)) {
            throw new IllegalStateException("Sua empresa já curtiu este candidato!")
        }

        curtidaDAO.registrarCurtidaEmpresa(cpfCandidato, cnpjEmpresa)
        return processarPossivelMatch(cpfCandidato, cnpjEmpresa, idVaga)
    }

    boolean processarPossivelMatch(String cpf, String cnpj, int vagaId) {
        if (curtidaDAO.verificarMatch(cpf, cnpj, vagaId)) {
            if (matchDAO.existeMatch(cpf, cnpj, vagaId)) {
                throw new IllegalStateException("Atenção: Vocês já deram Match anteriormente!")
            }
            matchDAO.registrarMatch(cpf, cnpj, vagaId)
            return true
        }
        return false
    }
}