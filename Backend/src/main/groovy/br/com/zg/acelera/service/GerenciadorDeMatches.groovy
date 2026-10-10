package br.com.zg.acelera.service

import br.com.zg.acelera.dao.MatchDAO

class GerenciadorDeMatches {

    static MatchDAO matchDAO = new MatchDAO()

    static void exibirPainel() {
        def matches = matchDAO.listarMatches()
        imprimirListaDeMatches("MATCHES REGISTRADOS", matches, "Nenhum match registrado no sistema.") { match ->
            println "Candidato: ${match.nomeCandidato}"
            println "Empresa: ${match.nomeEmpresa}"
            println "Vaga: ${match.tituloVaga}"
        }
    }
    static void exibirMatchesDoCandidato(String cpfCandidato) {
        def matches = matchDAO.listarMatchesDoCandidato(cpfCandidato)
        imprimirListaDeMatches("MEUS MATCHES", matches, "Nenhum match encontrado.") { match ->
            println "Empresa: ${match.nomeEmpresa}"
            println "Vaga: ${match.tituloVaga}"
        }
    }
    static void exibirMatchesDaEmpresa(String cnpjEmpresa) {
        def matches = matchDAO.listarMatchesDaEmpresa(cnpjEmpresa)
        imprimirListaDeMatches("MEUS MATCHES", matches, "Nenhum match encontrado.") { match ->
            println "Candidato: ${match.nomeCandidato}"
            println "Vaga: ${match.tituloVaga}"
        }
    }
    private static void imprimirListaDeMatches(String titulo, List<Map<String, Object>> matches, String mensagemVazio, Closure formatacaoMatch) {
        println "\n--- ${titulo} ---\n" // Adicionado \n extra para respirar

        if (matches.isEmpty()) {
            println mensagemVazio
            println "________________________________________________\n"
        } else {
            matches.each { match ->
                formatacaoMatch(match) // Aqui ele executa o bloco de código específico que passarmos!
                println "--------------------------------------------------\n"
            }
        }

        aguardarEnter()
    }
    private static void aguardarEnter() {
        println "\nPressione [ENTER] para voltar ao menu..."
        System.in.newReader().readLine()
    }
}
