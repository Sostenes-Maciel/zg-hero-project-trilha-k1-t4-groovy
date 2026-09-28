package br.com.zg.acelera.service

import br.com.zg.acelera.model.Candidato
import br.com.zg.acelera.model.Curtidas
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Match
import br.com.zg.acelera.model.Vaga
import br.com.zg.acelera.dao.BancodeDados

class GerenciadordeMatches {


    static void exibirPainel() {

        println "---STATUS DE CURTIDAS E MATCHES---"
        println "\n"

        if (BancodeDados.curtidas.isEmpty() && BancodeDados.matches.isEmpty()) {
            println "  Nenhuma interação registrada no sistema ainda."
            println "________________________________________________"
            return
        }

        BancodeDados.curtidas.each { curtida ->
            if (curtida.candidato != null && curtida.vaga != null) {
                boolean virouMatch = BancodeDados.matches.any {
                    it.candidato == curtida.candidato && it.vaga == curtida.vaga
                }
                if (!virouMatch) {
                    println " [ PENDENTE ]"
                    println "  -> ${curtida.candidato.nome} curtiu a vaga '${curtida.vaga.titulo}' de ${curtida.vaga.empresa.nome}."
                    println "  -> Aguardando a empresa curtir de volta..."
                    println "--------------------------------------------------"
                }
            }
        }

        println "\nPressione [ENTER] para voltar ao menu..."
        System.in.newReader().readLine()
    }

    static void candidatoCurteVaga(Candidato candidato, Vaga vaga) {
        BancodeDados.curtidas.add(new Curtidas(candidato: candidato, vaga: vaga))
        verificarMatch(candidato, vaga.empresa, vaga)
    }

    static void empresaCurteCandidato(Empresa empresa, Candidato candidato) {
        BancodeDados.curtidas.add(new Curtidas(empresa: empresa, candidato: candidato))

        Vaga vagaAlvo = BancodeDados.curtidas.find { it.candidato == candidato && it.vaga?.empresa == empresa }?.vaga
        if (vagaAlvo) {
            verificarMatch(candidato, empresa, vagaAlvo)
        }

    }

    private static void verificarMatch(Candidato candidato, Empresa empresa, Vaga vaga) {
        boolean candidatoCurtiu = BancodeDados.curtidas.any { it.candidato == candidato && it.vaga == vaga }
        boolean empresaCurtiu = BancodeDados.curtidas.any { it.empresa == empresa && it.candidato == candidato }

        if (candidatoCurtiu && empresaCurtiu) {
            boolean matchJaExiste = BancodeDados.matches.any { it.candidato == candidato && it.vaga == vaga }
            if (!matchJaExiste) {
                BancodeDados.matches.add(new Match(candidato: candidato, empresa: empresa, vaga: vaga))
            }
        }
    }

    static void simularInteracoes() {
        BancodeDados.curtidas.clear()
        BancodeDados.matches.clear()

        println "-> Iniciando simulação..."

        Candidato teste = new Candidato(nome: "Sóstenes")
        Empresa ifood = new Empresa(nome: "iFood")
        Vaga vagaDev = new Vaga(titulo: "Desenvolvedor Groovy", empresa: ifood)

        println "-> Sóstenes curtiu a vaga do iFood..."
        candidatoCurteVaga(teste, vagaDev)

        println "-> iFood curtiu o Sóstenes..."
        empresaCurteCandidato(ifood, teste)

        println "-> Simulação concluída! Curtidas geradas: ${BancodeDados.curtidas.size()} | Matches gerados: ${BancodeDados.matches.size()}\n"
    }

}
