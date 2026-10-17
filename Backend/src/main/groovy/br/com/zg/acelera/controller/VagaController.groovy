package br.com.zg.acelera.controller

import br.com.zg.acelera.model.Vaga
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
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao cadastrar vaga: ${e.message}"]
        }
    }

    Map listar() {
        try {
            List<Vaga> vagas = vagaService.listarVagas()
            return [status: 200, dados: vagas]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao listar vagas: ${e.message}"]
        }
    }

    Map listarDaEmpresa(String cnpj) {
        try {
            List<Vaga> vagas = vagaService.listarVagasDaEmpresa(cnpj)
            return [status: 200, dados: vagas]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao listar vagas da empresa: ${e.message}"]
        }
    }

    Map atualizar(Vaga vaga) {
        try {
            vagaService.atualizarVaga(vaga)
            return [status: 200, mensagem: "Vaga atualizada com sucesso!"]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao atualizar vaga: ${e.message}"]
        }
    }

    Map excluir(Integer id) {
        try {
            vagaService.excluirVaga(id)
            return [status: 200, mensagem: "Vaga excluída com sucesso!"]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao excluir vaga: ${e.message}"]
        }
    }
}