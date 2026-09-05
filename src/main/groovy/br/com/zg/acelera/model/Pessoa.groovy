package br.com.zg.acelera.model

abstract class Pessoa implements IPessoa {

    String nome
    String Email
    String estado
    String cep
    String descricao

    List<String> competencias = []


}
