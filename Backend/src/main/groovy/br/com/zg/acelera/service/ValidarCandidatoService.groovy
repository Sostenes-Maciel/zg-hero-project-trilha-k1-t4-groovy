package br.com.zg.acelera.service

import br.com.zg.acelera.utils.ValidacaoUtils

class ValidarCandidatoService {

    static boolean validarApenasLetras(String texto, String... nomeCampo) {
        if (!texto || !texto.matches(/^[a-zA-ZÀ-ÿ\s]+$/)) {
            throw new IllegalArgumentException("${nomeCampo ? nomeCampo[0] : 'Texto'} inválido.")
        }
        true
    }

    static void validarNome(String nome) {
        validarApenasLetras(nome, "Nome")
    }

    static void validarEmail(String email) {
        if (!email.matches(/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/)) {
            throw new IllegalArgumentException("E-mail inválido.")
        }
    }

    static void validarCpf(String cpf) {
        ValidacaoUtils.validarTamanhoNumerico(cpf, 11, "CPF")
    }

    static void validarIdade(int idade) {
        if (idade < 18 || idade > 120) {
            throw new IllegalArgumentException("Idade inválida.")
        }
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