package br.com.zg.acelera.controller

import br.com.zg.acelera.model.Vaga
import br.com.zg.acelera.service.GerenciadorDeMatchesService
import br.com.zg.acelera.service.GerenciadorDePerfisService

class MatchController {

    private GerenciadorDeMatchesService matchService
    private GerenciadorDePerfisService perfilService

    MatchController(GerenciadorDeMatchesService matchService, GerenciadorDePerfisService perfilService) {
        this.matchService = matchService
        this.perfilService = perfilService
    }

    Map curtirVaga(String cpfCandidato, Vaga vaga) {
        try {
            boolean deuMatch = perfilService.curtirVaga(cpfCandidato, vaga)
            String msg = deuMatch ? "MATCH ENCONTRADO!" : "Vaga curtida com sucesso!"
            return [status: 201, mensagem: msg, match: deuMatch]
        } catch (IllegalStateException e) {
            return [status: 400, mensagem: e.message]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao registrar curtida: ${e.message}"]
        }
    }

    Map curtirCandidato(String cnpjEmpresa, String cpfCandidato, Integer idVaga) {
        try {
            boolean deuMatch = perfilService.curtirCandidato(cnpjEmpresa, cpfCandidato, idVaga)
            String msg = deuMatch ? "MATCH ENCONTRADO!" : "Candidato curtido com sucesso!"
            return [status: 201, mensagem: msg, match: deuMatch]
        } catch (IllegalStateException e) {
            return [status: 400, mensagem: e.message]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao registrar curtida: ${e.message}"]
        }
    }

    Map listarMatchesCandidato(String cpf) {
        try {
            def matches = matchService.listarMatchesDoCandidato(cpf)
            return [status: 200, dados: matches]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao buscar matches: ${e.message}"]
        }
    }

    Map listarMatchesEmpresa(String cnpj) {
        try {
            def matches = matchService.listarMatchesDaEmpresa(cnpj)
            return [status: 200, dados: matches]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao buscar matches: ${e.message}"]
        }
    }
}