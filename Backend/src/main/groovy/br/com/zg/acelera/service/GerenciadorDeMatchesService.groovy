package br.com.zg.acelera.service

import br.com.zg.acelera.dao.MatchDAO

class GerenciadorDeMatchesService {

    private final MatchDAO matchDAO

    GerenciadorDeMatchesService(MatchDAO matchDAO) {
        this.matchDAO = matchDAO
    }


    void exibirMatchesDoCandidato(String cpfCandidato) {
        def matches = matchDAO.listarMatchesDoCandidato(cpfCandidato)
        imprimirListaDeMatches("MEUS MATCHES", matches, "Nenhum match encontrado.") { match ->
            println "Empresa: ${match.nomeEmpresa}"
            println "Vaga: ${match.tituloVaga}"
        }
    }

    void exibirMatchesDaEmpresa(String cnpjEmpresa) {
        def matches = matchDAO.listarMatchesDaEmpresa(cnpjEmpresa)
        imprimirListaDeMatches("MEUS MATCHES", matches, "Nenhum match encontrado.") { match ->
            println "Candidato: ${match.nomeCandidato}"
            println "Vaga: ${match.tituloVaga}"
        }
    }

    private static void imprimirListaDeMatches(String titulo, List<Map<String, Object>> matches, String mensagemVazio, Closure formatacaoMatch) {
        println "\n--- ${titulo} ---\n"

        if (matches.isEmpty()) {
            println mensagemVazio
            println "________________________________________________\n"
        } else {
            matches.each { match ->
                formatacaoMatch(match)
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