package br.com.zg.acelera.model

import br.com.zg.acelera.interfaces.IPessoa

abstract class Pessoa implements IPessoa {

    String nome
    String email
    String pais
    String estado
    String cep
    String descricao

    List<String> competencias = []


}
