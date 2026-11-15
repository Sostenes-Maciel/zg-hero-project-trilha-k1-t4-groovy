package br.com.zg.acelera.controller

import br.com.zg.acelera.model.Recrutador
import br.com.zg.acelera.service.GerenciadorDeRecrutadoresService

class RecrutadorController {

    private final GerenciadorDeRecrutadoresService service

    RecrutadorController(GerenciadorDeRecrutadoresService service) {
        this.service = service
    }

    Map cadastrar(Recrutador recrutador, String empresaId) {
        try {
            service.cadastrar(recrutador, empresaId)
            [status: 201, mensagem: "Recrutador cadastrado com sucesso.", id: recrutador.id]
        } catch (IllegalArgumentException e) {
            [status: 400, mensagem: e.message]
        } catch (SecurityException e) {
            [status: 403, mensagem: e.message]
        } catch (RuntimeException e) {
            if (violacaoDeUnicidade(e)) {
                [status: 409, mensagem: "E-mail ou CPF já cadastrado."]
            } else {
                [status: 500, mensagem: "Erro interno ao cadastrar recrutador."]
            }
        } catch (Exception e) {
            [status: 500, mensagem: "Erro interno ao cadastrar recrutador."]
        }
    }

    Map login(String email, String senha) {
        try {
            Recrutador recrutador = service.autenticar(email, senha)
            recrutador
                    ? [status: 200, recrutador: recrutador]
                    : [status: 401, mensagem: "E-mail ou senha incorretos."]
        } catch (Exception e) {
            [status: 500, mensagem: "Erro interno ao autenticar recrutador."]
        }
    }

    Map inativar(Integer id, String empresaId) {
        try {
            service.inativar(id, empresaId)
                    ? [status: 200, mensagem: "Recrutador inativado com sucesso."]
                    : [status: 404, mensagem: "Recrutador não encontrado."]
        } catch (IllegalArgumentException e) {
            [status: 400, mensagem: e.message]
        } catch (Exception e) {
            [status: 500, mensagem: "Erro interno ao inativar recrutador."]
        }
    }

    private static boolean violacaoDeUnicidade(Throwable erro) {
            Throwable atual = erro
            while (atual != null) {
                if (atual.respondsTo("getSQLState") && atual.getSQLState() == "23505") return true
                atual = atual.cause
            }
            false
    }
}
