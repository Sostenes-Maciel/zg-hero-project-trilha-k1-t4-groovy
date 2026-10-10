package br.com.zg.acelera.service

import br.com.zg.acelera.dao.MatchDAO

class GerenciadordeMatches {

    static MatchDAO matchDAO = new MatchDAO()

    static void exibirPainel() {

        println "--- MATCHES REGISTRADOS ---\n"

        List<Map<String, Object>> matches = matchDAO.listarMatches()

        if (matches.isEmpty()) {
            println "Nenhum match registrado no sistema."
            println "________________________________________________"
            return
        }

        matches.each { match ->

            println "Candidato: ${match.nomeCandidato}"
            println "Empresa: ${match.nomeEmpresa}"
            println "Vaga: ${match.tituloVaga}"
            println "--------------------------------------------------"
        }

        println "\nPressione [ENTER] para voltar ao menu..."
        System.in.newReader().readLine()
    }
    static void exibirMatchesDoCandidato(String cpfCandidato) {

        println "--- MEUS MATCHES ---\n"

        List<Map<String, Object>> matches =
                matchDAO.listarMatchesDoCandidato(cpfCandidato)

        if (matches.isEmpty()) {
            println "Nenhum match encontrado."
            println "________________________________________________"
            return
        }

        matches.each { match ->
            println "Candidato: ${match.nomeCandidato}"
            println "Empresa: ${match.nomeEmpresa}"
            println "Vaga: ${match.tituloVaga}"
            println "--------------------------------------------------"
        }

        println "\nPressione [ENTER] para voltar ao menu..."
        System.in.newReader().readLine()
    }
    static void exibirMatchesDaEmpresa(String cnpjEmpresa) {

        println "--- MEUS MATCHES ---\n"

        List<Map<String, Object>> matches =
                matchDAO.listarMatchesDaEmpresa(cnpjEmpresa)

        if (matches.isEmpty()) {
            println "Nenhum match encontrado."
            println "________________________________________________"
            return
        }

        matches.each { match ->
            println "Candidato: ${match.nomeCandidato}"
            println "Empresa: ${match.nomeEmpresa}"
            println "Vaga: ${match.tituloVaga}"
            println "--------------------------------------------------"
        }

        println "\nPressione [ENTER] para voltar ao menu..."
        System.in.newReader().readLine()
    }
}
