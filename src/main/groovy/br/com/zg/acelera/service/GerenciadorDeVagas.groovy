package br.com.zg.acelera.service

import br.com.zg.acelera.dao.CompetenciaDAO
import br.com.zg.acelera.dao.VagaDAO
import br.com.zg.acelera.model.Empresa
import br.com.zg.acelera.model.Vaga

class GerenciadorDeVagas {

    private VagaDAO vagaDAO = new VagaDAO()
    private CompetenciaDAO competenciaDAO = new CompetenciaDAO()


    Integer cadastrarVaga(Vaga vaga) {
        Integer idVaga = vagaDAO.cadastrar(vaga)

        vaga.id = idVaga

        vaga.competencia.each { competencia ->
            competenciaDAO.garantirCompetencia(competencia)
            competenciaDAO.adicionarAVaga(idVaga, competencia)
        }

        return idVaga
    }

    List<Vaga> listarVagas() {
        List<Vaga> vagas = vagaDAO.listarTodos()

        vagas.each { vaga ->
            vaga.competencia = competenciaDAO.listarDaVaga(vaga.id)
        }

        return vagas
    }

    List<Vaga> listarVagasDaEmpresa(String cnpj) {
        List<Vaga> vagas = vagaDAO.listarPorEmpresa(cnpj)

        vagas.each { vaga ->
            vaga.competencia = competenciaDAO.listarDaVaga(vaga.id)
        }

        return vagas
    }

    Vaga buscarVaga(Integer id) {
        Vaga vaga = vagaDAO.buscarPorId(id)

        if (vaga != null) {
            vaga.competencia = competenciaDAO.listarDaVaga(vaga.id)
        }

        return vaga
    }

    void atualizarVaga(Vaga vaga) {
        vagaDAO.atualizar(vaga)
    }

    void excluirVaga(Integer id) {
        vagaDAO.excluir(id)
    }

    void cadastrarVagaPeloTerminal(Scanner sc, Empresa empresa) {

        println "\n--- CADASTRAR NOVA VAGA ---"
        println "Empresa: ${empresa.nome}"

        print "Título da vaga: "
        String titulo = sc.nextLine().trim()

        print "País: "
        String pais = sc.nextLine().trim()

        print "Estado: "
        String estado = sc.nextLine().trim()

        print "Descrição: "
        String descricao = sc.nextLine().trim()

        print "Competências exigidas: "
        String entradaComps = sc.nextLine()

        List<String> competencias = entradaComps
                .tokenize(',')
                .collect { it.trim() }
                .findAll { it }

        Vaga vaga = new Vaga(
                titulo: titulo,
                pais: pais,
                estado: estado,
                descricao: descricao,
                competencia: competencias,
                empresa: empresa
        )

        Integer idVaga = cadastrarVaga(vaga)

        println "Vaga cadastrada com sucesso!"
        println "ID da vaga: ${idVaga}"
    }

    void listarVagasPeloTerminal(Empresa empresa) {

        List<Vaga> vagas = listarVagasDaEmpresa(empresa.cnpj)

        println "\n--- VAGAS DA EMPRESA: ${empresa.nome} ---"

        if (vagas.isEmpty()) {
            println "Nenhuma vaga cadastrada."
            return
        }

        vagas.each { vaga ->
            println "ID: ${vaga.id}"
            println "Título: ${vaga.titulo}"
            println "País: ${vaga.pais}"
            println "Estado: ${vaga.estado}"
            println "Descrição: ${vaga.descricao}"
            println "Competências: ${vaga.competencia.join(', ')}"
            println "-" * 60
        }
    }

    void atualizarVagaPeloTerminal(Scanner sc, Empresa empresa) {

        listarVagasPeloTerminal(empresa)

        print "\nInforme o ID da vaga que deseja atualizar: "

        if (!sc.hasNextInt()) {
            println "ID inválido."
            sc.nextLine()
            return
        }

        Integer id = sc.nextInt()
        sc.nextLine()

        Vaga vaga = buscarVaga(id)

        if (vaga == null || vaga.empresa.cnpj != empresa.cnpj) {
            println "Vaga não encontrada para esta empresa."
            return
        }

        List<String> competenciasAntigas = new ArrayList<>(vaga.competencia)

        print "Novo título: "
        vaga.titulo = sc.nextLine().trim()

        print "Novo país: "
        vaga.pais = sc.nextLine().trim()

        print "Novo estado: "
        vaga.estado = sc.nextLine().trim()

        print "Nova descrição: "
        vaga.descricao = sc.nextLine().trim()

        print "Novas competências (separe por vírgula): "
        String entradaComps = sc.nextLine()

        vaga.competencia = entradaComps
                .tokenize(',')
                .collect { it.trim() }
                .findAll { it }

        vagaDAO.atualizar(vaga)

        competenciasAntigas.each { competencia ->
            competenciaDAO.removerDaVaga(id, competencia)
        }

        vaga.competencia.each { competencia ->
            competenciaDAO.garantirCompetencia(competencia)
            competenciaDAO.adicionarAVaga(id, competencia)
        }

        println "Vaga atualizada com sucesso!"
    }

    void excluirVagaPeloTerminal(Scanner sc, Empresa empresa) {

        listarVagasPeloTerminal(empresa)

        print "\nInforme o ID da vaga que deseja excluir: "

        if (!sc.hasNextInt()) {
            println "ID inválido."
            sc.nextLine()
            return
        }

        Integer id = sc.nextInt()
        sc.nextLine()

        Vaga vaga = buscarVaga(id)

        if (vaga == null || vaga.empresa.cnpj != empresa.cnpj) {
            println "Vaga não encontrada para esta empresa."
            return
        }

        excluirVaga(id)

        println "Vaga excluída com sucesso!"
    }

    Empresa selecionarEmpresa(Scanner sc) {

        List<Empresa> empresas = empresaDAO.listarTodos()

        if (empresas.isEmpty()) {
            println "Nenhuma empresa cadastrada ainda."
            return null
        }

        println "\n-- Empresas Cadastradas --"

        empresas.eachWithIndex { empresa, indice ->
            println "${indice + 1} - ${empresa.nome} | CNPJ: ${empresa.cnpj}"
        }

        print "Selecione a empresa: "

        if (!sc.hasNextInt()) {
            println "Erro: informe apenas o número da empresa."
            sc.nextLine()
            return null
        }

        int opcao = sc.nextInt()
        sc.nextLine()

        if (opcao < 1 || opcao > empresas.size()) {
            println "Empresa inválida."
            return null
        }

        return empresas[opcao - 1]
    }
}