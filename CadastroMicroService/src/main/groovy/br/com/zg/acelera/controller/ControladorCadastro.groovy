package br.com.zg.acelera.controller

import br.com.zg.acelera.model.ServicoCadastro

class ControladorCadastro {
    private final ServicoCadastro servico

    ControladorCadastro(ServicoCadastro servico) {
        this.servico = servico
    }

    Map<String, String> cadastrarCandidato(Map<String, Object> dados) {
        servico.cadastrarCandidato(dados)
    }

    Map<String, String> cadastrarEmpresa(Map<String, Object> dados) {
        servico.cadastrarEmpresa(dados)
    }
}
