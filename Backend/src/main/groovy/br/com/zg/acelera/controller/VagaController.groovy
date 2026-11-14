package br.com.zg.acelera.controller

import br.com.zg.acelera.model.Vaga
import br.com.zg.acelera.model.Recrutador
import br.com.zg.acelera.service.GerenciadorDeVagasService

class VagaController {

    private GerenciadorDeVagasService vagaService

    VagaController(GerenciadorDeVagasService vagaService) {
        this.vagaService = vagaService
    }

    Map cadastrar(Vaga vaga) {
        try {
            Integer id = vagaService.cadastrarVaga(vaga)
            return [status: 201, mensagem: "Vaga cadastrada com sucesso!", id: id]
        } catch (SecurityException e) {
            return [status: 403, mensagem: e.message]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao cadastrar vaga: ${e.message}".toString()]
        }
    }

    Map listar() {
        try {
            List<Vaga> vagas = vagaService.listarVagas()
            return [status: 200, dados: vagas]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao listar vagas: ${e.message}".toString()]
        }
    }

    Map listar(Recrutador recrutador) {
            try {
                [status: 200, dados: vagaService.listarVagas(recrutador)]
            } catch (SecurityException e) {
                [status: 403, mensagem: e.message]
            } catch (Exception e) {
                [status: 500, mensagem: "Erro ao listar vagas: ${e.message}".toString()]
            }
    }

    Map cadastrar(Vaga vaga, Recrutador recrutador) {
            try {
                [status: 201, mensagem: "Vaga cadastrada com sucesso!",
                 id: vagaService.cadastrarVaga(vaga, recrutador)]
            } catch (SecurityException e) {
                [status: 403, mensagem: e.message]
            } catch (Exception e) {
                [status: 500, mensagem: "Erro ao cadastrar vaga: ${e.message}".toString()]
            }
    }

    Map atualizar(Vaga vaga, Recrutador recrutador) {
            try {
                vagaService.atualizarVaga(vaga, recrutador)
                [status: 200, mensagem: "Vaga atualizada com sucesso!"]
            } catch (SecurityException e) {
                [status: 403, mensagem: e.message]
            } catch (Exception e) {
                [status: 500, mensagem: "Erro ao atualizar vaga: ${e.message}".toString()]
            }
    }

    Map excluir(Integer id, Recrutador recrutador) {
            try {
                vagaService.excluirVaga(id, recrutador)
                [status: 200, mensagem: "Vaga excluída com sucesso!"]
            } catch (SecurityException e) {
                [status: 403, mensagem: e.message]
            } catch (Exception e) {
                [status: 500, mensagem: "Erro ao excluir vaga: ${e.message}".toString()]
        }
    }

    Map listarDaEmpresa(String cnpj) {
        try {
            List<Vaga> vagas = vagaService.listarVagasDaEmpresa(cnpj)
            return [status: 200, dados: vagas]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao listar vagas da empresa: ${e.message}".toString()]
        }
    }

    Map atualizar(Vaga vaga) {
        try {
            vagaService.atualizarVaga(vaga)
            return [status: 200, mensagem: "Vaga atualizada com sucesso!"]
        } catch (SecurityException e) {
            return [status: 403, mensagem: e.message]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao atualizar vaga: ${e.message}".toString()]
        }
    }

    Map excluir(Integer id) {
        try {
            vagaService.excluirVaga(id)
            return [status: 200, mensagem: "Vaga excluída com sucesso!"]
        } catch (SecurityException e) {
            return [status: 403, mensagem: e.message]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao excluir vaga: ${e.message}".toString()]
        }
    }

    Map buscarPorId(Integer id) {
        try {
            Vaga vaga = vagaService.buscarVaga(id)

            if (vaga != null) {
                return [status: 200, dados: vaga]
            } else {
                return [status: 404, mensagem: "Vaga não encontrada."]
            }
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao buscar vaga: " + e.message]
        }
    }
}