package br.com.zg.acelera.service

import br.com.zg.acelera.dao.CompetenciaDAO

class GerenciadorDeCompetencias {

    private CompetenciaDAO competenciaDAO = new CompetenciaDAO()

    void listarCompetencias() {

        List<String> competencias = competenciaDAO.listarTodos()

        println "\n--- COMPETÊNCIAS CADASTRADAS ---"

        if (competencias.isEmpty()) {
            println "Nenhuma competência cadastrada."
            return
        }

        competencias.eachWithIndex { competencia, indice ->
            println "${indice + 1} - ${competencia}"
        }
    }

    void cadastrarCompetencia(Scanner sc) {

        print "\nNome da competência: "
        String nome = sc.nextLine().trim()

        if (nome.isEmpty()) {
            println "O nome da competência não pode ser vazio."
            return
        }

        competenciaDAO.garantirCompetencia(nome)

        println "Competência cadastrada com sucesso!"
    }

    void atualizarCompetenciaPeloTerminal(Scanner sc) {

        listarCompetencias()

        List<String> competencias = competenciaDAO.listarTodos()

        if (competencias.isEmpty()) {
            return
        }

        print "\nSelecione a competência pelo ID: "

        if (!sc.hasNextInt()) {
            println "ID inválido."
            sc.nextLine()
            return
        }

        int opcao = sc.nextInt()
        sc.nextLine()

        if (opcao < 1 || opcao > competencias.size()) {
            println "Competência inválida."
            return
        }

        String nomeAtual = competencias[opcao - 1]

        print "Novo nome: "
        String novoNome = sc.nextLine().trim()

        if (novoNome.isEmpty()) {
            println "O nome da competência não pode ser vazio."
            return
        }

        competenciaDAO.atualizar(nomeAtual, novoNome)

        println "Competência atualizada com sucesso!"
    }

    void excluirCompetenciaPeloTerminal(Scanner sc) {

        listarCompetencias()

        List<String> competencias = competenciaDAO.listarTodos()

        if (competencias.isEmpty()) {
            return
        }

        print "\nSelecione a competência pelo ID: "

        if (!sc.hasNextInt()) {
            println "ID inválido."
            sc.nextLine()
            return
        }

        int opcao = sc.nextInt()
        sc.nextLine()

        if (opcao < 1 || opcao > competencias.size()) {
            println "Competência inválida."
            return
        }

        String nome = competencias[opcao - 1]

        competenciaDAO.excluir(nome)

        println "Competência excluída com sucesso!"
    }
}