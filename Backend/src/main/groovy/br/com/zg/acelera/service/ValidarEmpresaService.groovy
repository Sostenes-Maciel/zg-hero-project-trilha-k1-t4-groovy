package br.com.zg.acelera.service

import br.com.zg.acelera.utils.ValidacaoUtils

class ValidarEmpresaService {

    static void validarNome(String nome) {
        ValidacaoUtils.validarApenasLetras(nome, "Nome da empresa")
    }

    static void validarEmail(String email) {
        if (!email.matches(/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/)) {
            throw new IllegalArgumentException("E-mail inválido.")
        }
    }

    static void validarCnpj(String cnpj) {
        ValidacaoUtils.validarTamanhoNumerico(cnpj, 14, "CNPJ")
    }

    static void validarPais(String pais) {
        ValidacaoUtils.validarApenasLetras(pais, "País")
    }

    static void validarEstado(String estado) {
        ValidacaoUtils.validarApenasLetras(estado, "Estado")
    }

    static void validarCep(String cep) {
        ValidacaoUtils.validarTamanhoNumerico(cep, 8, "CEP")
    }

    static void validarDescricao(String descricao) {
        if (!descricao) {
            throw new IllegalArgumentException("Descrição não pode estar vazia.")
        }
    }

    static void validarCompetencias(List<String> competencias) {
        ValidacaoUtils.validarCompetencias(competencias)
    }
}