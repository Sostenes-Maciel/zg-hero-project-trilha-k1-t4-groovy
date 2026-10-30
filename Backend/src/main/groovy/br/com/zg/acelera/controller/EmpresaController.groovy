package br.com.zg.acelera.controller

import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.service.GerenciadorDePerfisService
import br.com.zg.acelera.utils.ValidacaoUtils


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
    Map buscarPorCnpj(String cnpj) {
        try {
            String cnpjLimpo = ValidacaoUtils.limparFormatacao(cnpj)
            Empresa empresa = perfilService.buscarEmpresaPorCnpj(cnpjLimpo)

            if (empresa) {
                return [status: 200, dados: empresa]
            } else {
                return [status: 404, mensagem: "Empresa não encontrada."]
            }
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro ao buscar empresa: ${e.message}".toString()]
        }
    }
    Map login(String email, String cnpj) {
        try {
            Empresa empresa = perfilService.validarLoginEmpresa(email, cnpj)

            if (empresa) {
                return [status: 200, empresa: empresa]
            } else {
                return [status: 401, mensagem: "E-mail ou CNPJ incorretos."]
            }
        } catch (Exception e) {
            return [status: 500, mensagem: "Erro interno no servidor."]
        }
    }
}