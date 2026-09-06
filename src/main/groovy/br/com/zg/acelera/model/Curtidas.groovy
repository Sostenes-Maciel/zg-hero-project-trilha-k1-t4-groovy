package br.com.zg.acelera.model

class Curtidas {

    Candidato candidato
    Vaga vaga
    Empresa empresa

    boolean Match() {
        return candidato != null && vaga != null && empresa != null
    }
}
