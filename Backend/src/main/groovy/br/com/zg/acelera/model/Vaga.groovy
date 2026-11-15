package br.com.zg.acelera.model

class Vaga {

    Integer id
    String titulo
    String pais
    String estado
    String descricao
    String areaAtuacao
    List<String> competencia = []
    Empresa empresa
}
