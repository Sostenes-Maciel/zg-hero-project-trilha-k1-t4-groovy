package br.com.zg.acelera.model

abstract class Pessoa implements IPessoa {

    String nome
    String email
    String estado
    String cep
    String descricao

    List<String> competencias = []


}
