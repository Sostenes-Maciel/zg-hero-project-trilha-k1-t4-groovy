package br.com.zg.acelera.controller

import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.service.GerenciadorDePerfisService

class EmpresaController {

    private GerenciadorDePerfisService perfilService

    EmpresaController(GerenciadorDePerfisService perfilService) {
        this.perfilService = perfilService
    }

    Map cadastrar(Empresa empresa) {
        try {
            perfilService.cadastrarEmpresa(empresa)
            return [status: 201, mensagem: "Empresa cadastrada com sucesso!"]
        } catch (IllegalArgumentException e) {
            return [status: 400, mensagem: "Erro de validação: ${e.message}"]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro interno: ${e.message}"]
        }
    }

    Map listar() {
        try {
            List<Empresa> empresas = perfilService.listarEmpresas()
            return [status: 200, dados: empresas]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao listar empresas: ${e.message}"]
        }
    }

    Map atualizar(Empresa empresa) {
        try {
            perfilService.atualizarEmpresa(empresa)
            return [status: 200, mensagem: "Empresa atualizada com sucesso!"]
        } catch (IllegalArgumentException e) {
            return [status: 400, mensagem: "Erro de validação: ${e.message}"]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro interno: ${e.message}"]
        }
    }

    Map excluir(String cnpj) {
        try {
            perfilService.excluirEmpresa(cnpj)
            return [status: 200, mensagem: "Empresa excluída com sucesso!"]
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao excluir empresa: ${e.message}"]
        }
    }
    void adicionarCompetencia(Empresa empresa, String competencia) {
        perfilService.adicionarCompetenciaAEmpresa(empresa, competencia)
    }

    void removerCompetencia(Empresa empresa, String competencia) {
        perfilService.removerCompetenciaDaEmpresa(empresa, competencia)
    }
}