package br.com.zg.acelera.controller

import br.com.zg.acelera.model.Vaga
import br.com.zg.acelera.service.GerenciadorDeMatchesService
import br.com.zg.acelera.service.GerenciadorDePerfisService
import br.com.zg.acelera.service.GerenciadorDeRecrutadoresService
import br.com.zg.acelera.service.GerenciadorDeVagasService

class MatchController {

    private GerenciadorDeMatchesService matchService
    private GerenciadorDePerfisService perfilService
    private GerenciadorDeVagasService vagaService
    private GerenciadorDeRecrutadoresService recrutadorService

    MatchController(GerenciadorDeMatchesService matchService,
                    GerenciadorDePerfisService perfilService,
                    GerenciadorDeVagasService vagaService,
                    GerenciadorDeRecrutadoresService recrutadorService) {
        this.matchService = matchService
        this.perfilService = perfilService
        this.vagaService = vagaService
        this.recrutadorService = recrutadorService
    }

    Map removerCurtidaRecrutador(String idRecStr, String cpf, Integer idVaga) {
        try {
            // O ESCUDO AQUI: Transforma "62.0" em 62.0 e depois corta o zero para virar 62 inteiro!
            Integer idRec = idRecStr.toDouble().toInteger()

            boolean sucesso = perfilService.removerCurtidaRecrutador(idRec, cpf, idVaga)
            if (sucesso) {
                return [status: 200, mensagem: "Curtida removida com sucesso."]
            } else {
                return [status: 400, mensagem: "Não foi possível remover a curtida."]
            }
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao remover: ${e.message}"]
        }
    }

    Map curtirCandidatoPorRecrutador(String idRecrutadorStr, String cnpjEmpresa, String cpfCandidato, Integer idVaga) {
        try {
            // O ESCUDO AQUI TAMBÉM:
            Integer idRec = idRecrutadorStr.toDouble().toInteger()
            def recrutador = recrutadorService.recrutadorDAO.buscarPorId(idRec)

            if (recrutador == null) {
                return [status: 404, mensagem: "Recrutador não encontrado."]
            }

            boolean deuMatch = perfilService.curtirCandidato(recrutador, cpfCandidato, idVaga)
            String msg = deuMatch ? "MATCH ENCONTRADO PELO RECRUTADOR!" : "Candidato curtido com sucesso!"

            Map resposta = [status: 201, mensagem: msg, match: deuMatch]

            if (deuMatch) {
                def candidato = perfilService.buscarCandidatoPorCpf(cpfCandidato)
                if (candidato != null) {
                    resposta.nomeRevelado = candidato.nome
                }
            }
            return resposta
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao registrar curtida do recrutador: ${e.message}"]
        }
    }

    Map curtirVaga(String cpfCandidato, Integer idVaga) {
        try {
            boolean deuMatch = perfilService.curtirVaga(cpfCandidato, idVaga)
            String msg = deuMatch ? "MATCH ENCONTRADO!" : "Vaga curtida com sucesso!"

            Map resposta = [status: 201, mensagem: msg, match: deuMatch]

            if (deuMatch) {
                Vaga vaga = vagaService.buscarVaga(idVaga)
                if (vaga && vaga.empresa) {
                    resposta.nomeRevelado = vaga.empresa.nome
                }
            }
            return resposta
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

            Map resposta = [status: 201, mensagem: msg, match: deuMatch]

            if (deuMatch) {
                def candidato = perfilService.buscarCandidatoPorCpf(cpfCandidato)
                if (candidato != null) {
                    resposta.nomeRevelado = candidato.nome
                }
            }
            return resposta
        } catch (IllegalArgumentException | IllegalStateException e) {
            return [status: 400, mensagem: e.message]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao registrar curtida: ${e.message}"]
        }
    }

    Map listarMatchesCandidato(String cpf) {
        try {
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

    Map removerCurtidaEmpresa(String cnpj, String cpf, Integer idVaga) {
        try {
            boolean sucesso = perfilService.removerCurtidaEmpresa(cnpj, cpf, idVaga)
            return sucesso ? [status: 200, mensagem: "Curtida removida com sucesso."] : [status: 400, mensagem: "Não foi possível remover."]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao remover curtida: ${e.message}"]
        }
    }

    Map removerCurtidaCandidato(String cpf, Integer idVaga) {
        try {
            boolean sucesso = perfilService.removerCurtidaCandidato(cpf, idVaga)
            return sucesso ? [status: 200, mensagem: "Curtida na vaga removida com sucesso."] : [status: 400, mensagem: "Não foi possível remover."]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao remover curtida: ${e.message}"]
        }
    }

    Map listarMatchesCompletosCandidato(String cpf) {
        try {
            def resultado = perfilService.listarMatchesCompletosCandidato(cpf)
            return [status: 200, matches: resultado.matches, curtidasPendentes: resultado.curtidasPendentes]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro: ${e.message}"]
        }
    }

    Map listarMatchesCompletosEmpresa(String cnpj) {
        try {
            def resultado = perfilService.listarMatchesCompletosEmpresa(cnpj)
            return [status: 200, matches: resultado.matches, curtidasPendentes: resultado.curtidasPendentes]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro: ${e.message}"]
        }
    }
    Map listarMatchesCompletosRecrutador(String idRecrutadorStr) {
        try {
            Integer idRec = idRecrutadorStr.toDouble().toInteger()

            def resultado = perfilService.listarMatchesCompletosRecrutador(idRec)

            return [status: 200, matches: resultado.matches, curtidasPendentes: resultado.curtidasPendentes]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao buscar matches do recrutador: ${e.message}"]
        }
    }
}