package br.com.zg.acelera.controller

import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.service.GerenciadorDePerfisService
import br.com.zg.acelera.utils.ValidacaoUtils
import br.com.zg.acelera.dao.CompetenciaDAO

class CandidatoController {

    private GerenciadorDePerfisService perfilService
    private CompetenciaDAO competenciaDAO

    CandidatoController(GerenciadorDePerfisService perfilService) {
        this.perfilService = perfilService
    }

    Map cadastrar(Candidato candidato) {
        try {
            perfilService.cadastrarCandidato(candidato)

            return [status: 201, mensagem: "Candidato cadastrado com sucesso!"]
        } catch (IllegalArgumentException e) {
            return [status: 400, mensagem: "Erro de validação: ${e.message}".toString()]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro interno: ${e.message}".toString()]
        }
    }

    Map listar() {
        try {
            List<Candidato> candidatos = perfilService.listarCandidatos()
            return [status: 200, dados: candidatos]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao listar candidatos: ${e.message}".toString()]
        }
    }

    Map atualizar(Candidato candidato) {
        try {
            perfilService.atualizarCandidato(candidato)
            return [status: 200, mensagem: "Candidato atualizado com sucesso!"]
        } catch (IllegalArgumentException e) {
            return [status: 400, mensagem: "Erro de validação: ${e.message}".toString()]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro interno: ${e.message}".toString()]
        }
    }

    Map excluir(String cpf) {
        try {
            perfilService.excluirCandidato(cpf)
            return [status: 200, mensagem: "Candidato excluído com sucesso!"]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao excluir candidato: ${e.message}".toString()]
        }
    }

    void adicionarCompetencia(Candidato candidato, String competencia) {
        perfilService.adicionarCompetenciaAoCandidato(candidato, competencia)
    }

    void removerCompetencia(Candidato candidato, String competencia) {
        perfilService.removerCompetenciaDoCandidato(candidato, competencia)
    }

    Map buscarPorCpf(String cpf) {
        try {
            String cpfLimpo = ValidacaoUtils.limparFormatacao(cpf)
            Candidato candidato = perfilService.buscarCandidatoPorCpf(cpfLimpo)

            if (candidato) {
                return [status: 200, dados: candidato]
            } else {
                return [status: 404, mensagem: "Candidato não encontrado."]
            }
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao buscar candidato: ${e.message}".toString()]
        }
    }
    Map login(String email, String cpf) {
        try {
            Candidato candidato = perfilService.validarLoginCandidato(email, cpf)

            if (candidato) {
                return [status: 200, candidato: candidato]
            } else {
                return [status: 401, mensagem: "E-mail ou CPF incorretos."]
            }
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro interno no servidor."]
        }
    }
}