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

    Map curtirVaga(String cpfCandidato, Integer idVaga) {
        try {
            boolean deuMatch = perfilService.curtirVaga(cpfCandidato, idVaga)
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
            // Agora pegamos a lista perfeitamente formatada
            def statusCurtidas = perfilService.listarStatusCurtidasCandidato(cpf)
            return [status: 200, dados: statusCurtidas]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao buscar matches: ${e.message}"]
        }
    }

    Map listarMatchesEmpresa(String cnpj) {
        try {
            def statusCurtidas = perfilService.listarStatusCurtidasEmpresa(cnpj)
            return [status: 200, dados: statusCurtidas]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao buscar matches da empresa: ${e.message}"]
        }
    }
    Map removerCurtidaEmpresa(String cnpj, String cpf) {
        try {
            // Repassa para a regra de negócio no Service
            boolean sucesso = perfilService.removerCurtidaEmpresa(cnpj, cpf)

            if (sucesso) {
                return [status: 200, mensagem: "Curtida removida com sucesso."]
            } else {
                return [status: 400, mensagem: "Não foi possível remover a curtida (talvez ela não exista ou já seja um Match)."]
            }
        } catch (Exception e) {
            e.printStackTrace()
            return [status: 500, mensagem: "Erro ao remover curtida: ${e.message}"]
        }
    }
    Map removerCurtidaCandidato(String cpf, Integer idVaga) {
        try {
            boolean sucesso = perfilService.removerCurtidaCandidato(cpf, idVaga)

            if (sucesso) {
                return [status: 200, mensagem: "Curtida na vaga removida com sucesso."]
            } else {
                return [status: 400, mensagem: "Não foi possível remover a curtida (talvez já seja um Match)."]
            }
        } catch (Exception e) {
            e.printStackTrace()
            return [status: 500, mensagem: "Erro ao remover curtida: ${e.message}"]
        }
    }
    Map listarMatchesCompletosCandidato(String cpf) {
        try {
            def resultado = perfilService.listarMatchesCompletosCandidato(cpf)
            return [
                    status: 200,
                    matches: resultado.matches,
                    curtidasPendentes: resultado.curtidasPendentes
            ]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao buscar matches completos: ${e.message}"]
        }
    }

    Map listarMatchesCompletosEmpresa(String cnpj) {
        try {
            def resultado = perfilService.listarMatchesCompletosEmpresa(cnpj)
            return [
                    status: 200,
                    matches: resultado.matches,
                    curtidasPendentes: resultado.curtidasPendentes
            ]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao buscar matches completos da empresa: ${e.message}"]
        }
    }
}