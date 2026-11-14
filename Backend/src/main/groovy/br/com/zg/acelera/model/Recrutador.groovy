package br.com.zg.acelera.model

import java.time.LocalDate

class Recrutador {

    Integer id
    String empresaId
    String nome
    String areaAtuacao
    String nivelPermissao
    String descricao
    String email
    String cpf
    LocalDate dataNascimento
    String senhaHash
    boolean ativo = true
}
