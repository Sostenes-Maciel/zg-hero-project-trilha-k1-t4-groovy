package br.com.zg.acelera.service

import br.com.zg.acelera.interfaces.ICandidatoDAO
import br.com.zg.acelera.interfaces.ICompetenciaDAO
import br.com.zg.acelera.interfaces.ICurtidaDAO
import br.com.zg.acelera.interfaces.IEmpresaDAO
import br.com.zg.acelera.interfaces.IMatchDAO
import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga
import br.com.zg.acelera.model.Recrutador
import br.com.zg.acelera.utils.ValidacaoUtils

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
        candidato.cpf = ValidacaoUtils.limparFormatacao(candidato.cpf)
        ValidarCandidatoService.validarIdade(candidato.idade)
        ValidarCandidatoService.validarEstado(candidato.estado)
        ValidarCandidatoService.validarCep(candidato.cep)
        ValidarCandidatoService.validarDescricao(candidato.descricao)
        ValidarCandidatoService.validarCompetencias(candidato.competencias)

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
        ValidarCandidatoService.validarCompetencias(empresa.competencias)

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

    List<Candidato> listarCandidatosPorArea(Recrutador recrutador) {
        if (!recrutador?.ativo) throw new SecurityException("Recrutador inativo.")

        List<Vaga> vagas = gerenciadorDeVagas.listarVagas(recrutador)
        Set<String> competenciasDaArea = vagas
                .collectMany { it.competencia ?: [] }
                .findAll { it?.trim() }
                .collect { it.trim().toLowerCase() } as Set

        if (competenciasDaArea.isEmpty()) {
            return []
        }

        candidatoDAO.listarTodos().findAll { Candidato candidato ->
            Set<String> competencias = (candidato.competencias ?: [])
                    .findAll { it?.trim() }
                    .collect { it.trim().toLowerCase() } as Set
            competenciasDaArea.isEmpty() || !competencias.intersect(competenciasDaArea).isEmpty()
        }
    }

    List<Candidato> listarCandidatos(Recrutador recrutador, Vaga vaga) {
        if (!recrutador?.ativo) throw new SecurityException("Recrutador inativo.")
        if (vaga == null || vaga.empresa?.cnpj != recrutador.empresaId ||
                (vaga.areaAtuacao && vaga.areaAtuacao != recrutador.areaAtuacao)) {
            throw new SecurityException("Vaga fora do escopo do recrutador.")
        }
        Set<String> competenciasDaVaga = (vaga.competencia ?: [])*.toLowerCase() as Set
        candidatoDAO.listarTodos().findAll { Candidato candidato ->
            Set<String> competencias = (competenciaDAO.listarDoCandidato(candidato.cpf) ?: [])*.toLowerCase() as Set
            competenciasDaVaga.isEmpty() || !competencias.intersect(competenciasDaVaga).isEmpty()
        }
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
        ValidarCandidatoService.validarCpf(candidato.cpf)
        ValidarCandidatoService.validarIdade(candidato.idade)
        ValidarCandidatoService.validarEstado(candidato.estado)
        ValidarCandidatoService.validarCep(candidato.cep)
        ValidarCandidatoService.validarDescricao(candidato.descricao)
        ValidarCandidatoService.validarCompetencias(candidato.competencias)

        Candidato candidatoExistente = candidatoDAO.buscarPorId(candidato.cpf)
        if (candidatoExistente == null) {
            throw new IllegalArgumentException("Candidato não encontrado.")
        }

        candidatoDAO.atualizar(candidato)
        sincronizarCompetenciasCandidato(candidato)
    }

    void excluirCandidato(String cpf) {
        candidatoDAO.excluir(cpf)
    }

    void atualizarEmpresa(Empresa empresa) {
        ValidarEmpresaService.validarNome(empresa.nome)
        ValidarEmpresaService.validarEmail(empresa.email)
        ValidarEmpresaService.validarCnpj(empresa.cnpj)
        ValidarEmpresaService.validarPais(empresa.pais)
        ValidarEmpresaService.validarEstado(empresa.estado)
        ValidarEmpresaService.validarCep(empresa.cep)
        ValidarEmpresaService.validarDescricao(empresa.descricao)
        ValidarCandidatoService.validarCompetencias(empresa.competencias)

        Empresa empresaExistente = empresaDAO.buscarPorId(empresa.cnpj)
        if (empresaExistente == null) {
            throw new IllegalArgumentException("Empresa não encontrada.")
        }

        empresaDAO.atualizar(empresa)
        sincronizarCompetenciasEmpresa(empresa)
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

    private void sincronizarCompetenciasCandidato(Candidato candidato) {
        List<String> atuais = competenciaDAO.listarDoCandidato(candidato.cpf)
        List<String> desejadas = (candidato.competencias ?: [])
                .collect { String competencia -> competencia?.trim() }
                .findAll { String competencia -> competencia }
                .unique()

        (atuais - desejadas).each { String competencia ->
            competenciaDAO.removerDoCandidato(candidato.cpf, competencia)
        }
        (desejadas - atuais).each { String competencia ->
            adicionarCompetenciaAoCandidato(candidato, competencia)
        }
    }

    private void sincronizarCompetenciasEmpresa(Empresa empresa) {
        List<String> atuais = competenciaDAO.listarDaEmpresa(empresa.cnpj)
        List<String> desejadas = (empresa.competencias ?: [])
                .collect { String competencia -> competencia?.trim() }
                .findAll { String competencia -> competencia }
                .unique()

        (atuais - desejadas).each { String competencia ->
            competenciaDAO.removerDaEmpresa(empresa.cnpj, competencia)
        }
        (desejadas - atuais).each { String competencia ->
            adicionarCompetenciaAEmpresa(empresa, competencia)
        }
    }

    boolean curtirVaga(String cpfCandidato, Integer idVaga) {
        cpfCandidato = ValidacaoUtils.limparFormatacao(cpfCandidato)
        Vaga vagaCompleta = gerenciadorDeVagas.buscarVagaPorId(idVaga)

        if (vagaCompleta == null) {
            throw new IllegalStateException("Vaga não encontrada com o ID informado.")
        }

        if (curtidaDAO.jaCurtiuVaga(cpfCandidato, idVaga)) {
            if (matchDAO.existeMatch(cpfCandidato, vagaCompleta.empresa.cnpj, idVaga)) {
                return true
            }
            if (processarPossivelMatch(cpfCandidato, vagaCompleta.empresa.cnpj, idVaga)) {
                return true
            }
            throw new IllegalStateException("Você já curtiu esta vaga!")
        }

        curtidaDAO.registrarCurtidaCandidato(cpfCandidato, idVaga)
        return processarPossivelMatch(cpfCandidato, vagaCompleta.empresa.cnpj, idVaga)
    }

    boolean curtirCandidato(String cnpjEmpresa, String cpfCandidato, Integer idVaga) {
        cpfCandidato = ValidacaoUtils.limparFormatacao(cpfCandidato)
        if (idVaga == null) {
            throw new IllegalArgumentException("Selecione uma vaga para curtir o candidato.")
        }

        Vaga vaga = gerenciadorDeVagas.buscarVagaPorId(idVaga)
        if (vaga == null) {
            throw new IllegalStateException("Vaga não encontrada com o ID informado.")
        }
        if (vaga.empresa?.cnpj?.replaceAll(/\D/, '') != cnpjEmpresa?.replaceAll(/\D/, '')) {
            throw new IllegalStateException("A vaga selecionada não pertence a esta empresa.")
        }

        if (matchDAO.existeMatch(cpfCandidato, cnpjEmpresa, idVaga)) {
            return true
        }
        if (curtidaDAO.jaCurtiuCandidato(cnpjEmpresa, cpfCandidato, idVaga)) {
            return processarPossivelMatch(cpfCandidato, cnpjEmpresa, idVaga)
        }

        curtidaDAO.registrarCurtidaEmpresa(cpfCandidato, cnpjEmpresa, idVaga)
        return processarPossivelMatch(cpfCandidato, cnpjEmpresa, idVaga)
    }

    boolean curtirCandidato(Recrutador recrutador, String cpfCandidato, Integer idVaga) {
        if (!recrutador?.ativo) throw new SecurityException("Recrutador inativo.")
        cpfCandidato = ValidacaoUtils.limparFormatacao(cpfCandidato)
        Vaga vaga = gerenciadorDeVagas.buscarVagaPorId(idVaga)
        if (vaga == null || vaga.empresa?.cnpj != recrutador.empresaId) {
            throw new SecurityException("Vaga fora do escopo da empresa.")
        }
        if (vaga.areaAtuacao && vaga.areaAtuacao != recrutador.areaAtuacao) {
            throw new SecurityException("Vaga fora da área de atuação do recrutador.")
        }
        if (matchDAO.existeMatch(cpfCandidato, recrutador.empresaId, idVaga)) return true
        if (!curtidaDAO.jaCurtiuCandidatoPorRecrutador(recrutador.id, cpfCandidato, idVaga)) {
            curtidaDAO.registrarCurtidaEmpresa(cpfCandidato, recrutador.empresaId, idVaga, recrutador.id)
        }
        processarPossivelMatch(cpfCandidato, recrutador.empresaId, idVaga)
    }

    boolean removerCurtidaEmpresa(String cnpjEmpresa, String cpfCandidato, Integer idVaga) {
        cpfCandidato = ValidacaoUtils.limparFormatacao(cpfCandidato)
        // Verifica se os dados chegaram corretamente
        if (!cnpjEmpresa || !cpfCandidato || !idVaga) {
            return false
        }

        if (matchDAO.existeMatch(cpfCandidato, cnpjEmpresa, idVaga)) {
            return false
        }

        return curtidaDAO.removerCurtidaEmpresa(cnpjEmpresa, cpfCandidato, idVaga)
    }

    boolean removerCurtidaCandidato(String cpfCandidato, Integer idVaga) {
        cpfCandidato = ValidacaoUtils.limparFormatacao(cpfCandidato)
        // Verifica se os dados chegaram corretamente
        if (!cpfCandidato || !idVaga) {
            return false
        }

        return curtidaDAO.removerCurtidaCandidato(cpfCandidato, idVaga)
    }

    boolean processarPossivelMatch(String cpf, String cnpj, Integer vagaId) {
        if (curtidaDAO.verificarMatch(cpf, cnpj, vagaId)) {
            if (matchDAO.existeMatch(cpf, cnpj, vagaId)) {
                throw new IllegalStateException("Atenção: Vocês já deram Match anteriormente!")
            }
            matchDAO.registrarMatch(cpf, cnpj, vagaId)
            return true
        }
        return false
    }
    Candidato validarLoginCandidato(String email, String cpf) {
        return candidatoDAO.validarLogin(email, cpf)
    }
    Empresa validarLoginEmpresa(String email, String cnpj) {
        return empresaDAO.validarLogin(email, cnpj)
    }
    List listarStatusCurtidasCandidato(String cpf) {
        return curtidaDAO.listarStatusCurtidas(cpf)
    }
    List listarStatusCurtidasEmpresa(String cnpj) {
        return curtidaDAO.listarStatusCurtidasEmpresa(cnpj)
    }

    Map listarMatchesCompletosCandidato(String cpf) {
        def matchesReais = matchDAO.listarMatchesDoCandidato(cpf)
        def pendentes = matchDAO.listarCurtidasPendentesDoCandidato(cpf)

        def pendentesFormatadas = pendentes.collect { item ->
            [vaga: item['vaga']]
        }

        return [
                matches: matchesReais,
                curtidasPendentes: pendentesFormatadas
        ]
    }
    Map listarMatchesCompletosEmpresa(String cnpj) {
        def matchesReais = matchDAO.listarMatchesDaEmpresa(cnpj)
        def pendentes = matchDAO.listarCurtidasPendentesDaEmpresa(cnpj)

        def pendentesFormatadas = pendentes.collect { item ->
            Map candidato = item['candidato'] as Map
            Map vaga = item['vaga'] as Map

            [
                    // A identidade só é liberada em matches reais.
                    candidato: [competencias: candidato?.competencias ?: []],
                    vaga: [
                            id: vaga?.id,
                            titulo: vaga?.titulo,
                            pais: vaga?.pais,
                            estado: vaga?.estado
                    ]
            ]
        }

        return [
                matches: matchesReais,
                curtidasPendentes: pendentesFormatadas
        ]
    }
}