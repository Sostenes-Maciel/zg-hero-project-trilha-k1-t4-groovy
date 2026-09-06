package br.com.zg.acelera.service

import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Curtidas
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga
import br.com.zg.acelera.repository.BancodeDados

class GerenciadordeMatches {


    static void exibirPainel() {

        println "---STATUS DE CURTIDAS E MATCHES---"
        println "\n"

        if (BancodeDados.curtidas.isEmpty()) {
            println "  Nenhuma interação registrada no sistema ainda."
            println "________________________________________________"
            return
        }

        BancodeDados.curtidas.each { curtida ->
            String nomeCandidato = curtida.candidato.nome
            String tituloVaga = curtida.vaga.titulo
            String nomeEmpresaDonaDaVaga = curtida.vaga.empresa.nome

            if (curtida.Match()) {
                println " [ MATCH! ]"
                println "  -> ${nomeCandidato} e ${nomeEmpresaDonaDaVaga} deram match!"
                println "  -> Vaga: ${tituloVaga}"
            } else {
                println " [ PENDENTE ]"
                println "  -> ${nomeCandidato} curtiu a vaga '${tituloVaga}' de ${nomeEmpresaDonaDaVaga}."
                println "  -> Aguardando a empresa curtir de volta..."
            }
            println "───────────────────────────────────────────────────────────────"
        }

        println "\nPressione [ENTER] para voltar ao menu..."
        System.in.newReader().readLine()
    }

    static void candidatoCurteVaga(Candidato candidato, Vaga vaga) {
        Curtidas novaCurtida = new Curtidas(candidato: candidato, vaga: vaga)
        BancodeDados.curtidas.add(novaCurtida)
    }

    static void empresaCurteCandidato(Empresa empresa, Candidato candidato, Vaga vaga) {
        Curtidas curtidaExistente = BancodeDados.curtidas.find {
            it.candidato == candidato && it.vaga == vaga
        }

        if (curtidaExistente) {
            curtidaExistente.empresa = empresa
        }
    }

    static void simularInteracoes() {

        BancodeDados.curtidas.clear()
        BancodeDados.vagas.clear()

        Candidato teste = new Candidato(nome: "Antônio")
        Empresa ifood = new Empresa(nome: "iFood")
        Vaga vagaDev = new Vaga(titulo: "Desenvolvedor Groovy", empresa: ifood)


        candidatoCurteVaga(teste, vagaDev)
        empresaCurteCandidato(ifood, teste, vagaDev)

        Candidato maria = new Candidato(nome: "Maria")
        candidatoCurteVaga(maria, vagaDev)
    }
}
